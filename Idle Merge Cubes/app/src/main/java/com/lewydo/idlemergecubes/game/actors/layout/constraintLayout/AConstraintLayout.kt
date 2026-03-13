package com.lewydo.idlemergecubes.game.actors.layout.constraintLayout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  AConstraintLayout
//
//  Повноцінний ConstraintLayout як в Android — для libGDX.
//
//  ПРАВИЛА:
//    1. Актор ПОВИНЕН мати setSize() до виклику add()
//    2. Якщо B залежить від A — додавай A першим (порядок = порядок layout())
//    3. При remove() — все очищається автоматично
//    4. clearChildren() — використовуй clearConstrained() щоб очистити і rules теж
//
//  ВИПРАВЛЕНІ ПРОБЛЕМИ:
//    • Kotlin naming conflict: поля CLParams перейменовані в *Actor (внутрішні)
//    • watch(this) в init{} — не залежить від addActorsOnGroup() timing
//    • clearChildren() не викликає removeActor() → окремий clearConstrained()
//    • act() snapshot iteration безпечна: копіюємо entries перед ітерацією
//      щоб уникнути ConcurrentModificationException якщо Action змінює структуру
// ═════════════════════════════════════════════════════════════════════════════

open class AConstraintLayout(override val screen: AdvancedScreen) : AdvancedGroup() {

    // actor → його constraint-правила (у порядку add() — важливо для залежностей)
    private val rules     = LinkedHashMap<Actor, CLParams>()

    // actor → [x, y, w, h] snapshot
    // Містить: layout + всі constrained-актори + всі їх anchor-и
    private val snapshots = HashMap<Actor, FloatArray>()

    // Безпечна копія для ітерації в act() — оновлюється при rebuildSnapshots()
    // Уникає ConcurrentModificationException якщо Action змінює rules під час act()
    private var snapshotEntries = emptyList<Pair<Actor, FloatArray>>()

    init {
        // Реєструємо layout одразу — незалежно від addActorsOnGroup() timing
        watch(this)
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Додає актора з constraint-правилами.
     *
     * Актор ПОВИНЕН мати встановлений розмір (setSize) ДО виклику.
     * Порядок має значення: якщо B залежить від A — додавай A першим.
     *
     * @return actor — для зручного chaining
     */
    fun add(actor: Actor, block: CLParams.() -> Unit): Actor {
        require(actor.width > 0f || actor.height > 0f) {
            "AConstraintLayout.add(): встанови розмір через setSize() перед add()!\n" +
                    "Actor: ${actor::class.simpleName}"
        }
        val params = CLParams(this).apply(block)
        rules[actor] = params
        addActor(actor)

        watch(actor)                 // відстежуємо сам актор (його розмір впливає на позицію)
        params.allAnchors().forEach { watch(it) }  // відстежуємо всі anchor-и
        rebuildSnapshotEntries()     // оновлюємо безпечну копію

        applyPosition(actor, params)
        return actor
    }

    /**
     * Оновлює правила для вже доданого актора в runtime.
     * Безпечно викликати в будь-який момент, навіть під час анімації.
     */
    fun update(actor: Actor, block: CLParams.() -> Unit) {
        val params = rules[actor] ?: CLParams(this)
        params.apply(block)
        rules[actor] = params
        rebuildSnapshots()
        applyPosition(actor, params)
    }

    /**
     * Від'єднує актора від constraint-системи.
     * Актор залишається в групі, але більше не рухається автоматично.
     */
    fun detach(actor: Actor) {
        rules.remove(actor)
        rebuildSnapshots()
    }

    /**
     * Повністю очищає layout: видаляє всі актори + всі rules + snapshots.
     * Використовуй замість clearChildren() якщо хочеш скинути все.
     */
    fun clearConstrained() {
        rules.clear()
        snapshots.clear()
        snapshotEntries = emptyList()
        watch(this)          // layout сам завжди залишається
        clearChildren()      // видаляємо акторів з групи
    }

    // ── Override removeActor ──────────────────────────────────────────────────
    //
    // Спрацьовує при: actor.remove(), layout.removeActor(actor)
    // НЕ спрацьовує при: clearChildren() — тому є clearConstrained()
    //
    // Порядок дій:
    //   1. Якщо actor є anchor для інших → видаляємо і їх rules (каскадно)
    //   2. Видаляємо rule самого actor
    //   3. Перебудовуємо snapshots

    override fun removeActor(actor: Actor): Boolean {
        if (isUsedAsAnchor(actor)) {
            removeRulesWithAnchor(actor)
        }
        rules.remove(actor)
        rebuildSnapshots()
        return super.removeActor(actor)
    }

    // ── addActorsOnGroup ──────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        // Нічого — watch(this) вже в init{}
        // Актори додаються через add() зовні
    }

    // ── act(): snapshot detection ─────────────────────────────────────────────
    //
    // Ітеруємо по snapshotEntries (незмінна копія) — безпечно навіть якщо
    // якийсь Action всередині super.act() викликає removeActor() або rebuildSnapshots().

    override fun act(delta: Float) {
        super.act(delta)
        if (rules.isEmpty()) return

        var dirty = false

        // Ітеруємо по копії — ConcurrentModification безпечно
        for ((actor, snap) in snapshotEntries) {
            if (snap[0] != actor.x     ||
                snap[1] != actor.y     ||
                snap[2] != actor.width ||
                snap[3] != actor.height)
            {
                snap[0] = actor.x
                snap[1] = actor.y
                snap[2] = actor.width
                snap[3] = actor.height
                dirty   = true
                // Не break — оновлюємо snapshot ВСІХ щоб наступний кадр теж спрацював
            }
        }

        if (dirty) invalidate()
    }

    // ── layout() ─────────────────────────────────────────────────────────────

    override fun layout() {
        rules.forEach { (actor, params) ->
            applyPosition(actor, params)
        }
    }

    // ── Position resolver ─────────────────────────────────────────────────────

    private fun applyPosition(actor: Actor, p: CLParams) {
        actor.setPosition(resolveX(actor, p), resolveY(actor, p))
    }

    private fun resolveX(actor: Actor, p: CLParams): Float {
        val w = actor.width

        val startX: Float? = when {
            p.startToStartActor != null -> edgeLeft(p.startToStartActor!!)  + p.marginStart
            p.startToEndActor   != null -> edgeRight(p.startToEndActor!!)   + p.marginStart
            else                        -> null
        }
        val endX: Float? = when {
            p.endToEndActor   != null -> edgeRight(p.endToEndActor!!)  - w - p.marginEnd
            p.endToStartActor != null -> edgeLeft(p.endToStartActor!!) - w - p.marginEnd
            else                      -> null
        }

        return when {
            startX != null && endX != null -> startX + (endX - startX) * p.horizontalBias
            startX != null                 -> startX
            endX   != null                 -> endX
            else                           -> actor.x
        }
    }

    private fun resolveY(actor: Actor, p: CLParams): Float {
        val h = actor.height

        val bottomY: Float? = when {
            p.bottomToBottomActor != null -> edgeBottom(p.bottomToBottomActor!!) + p.marginBottom
            p.bottomToTopActor    != null -> edgeTop(p.bottomToTopActor!!)       + p.marginBottom
            else                          -> null
        }
        val topY: Float? = when {
            p.topToTopActor    != null -> edgeTop(p.topToTopActor!!)       - h - p.marginTop
            p.topToBottomActor != null -> edgeBottom(p.topToBottomActor!!) - h - p.marginTop
            else                       -> null
        }

        return when {
            bottomY != null && topY != null -> bottomY + (topY - bottomY) * p.verticalBias
            bottomY != null                 -> bottomY
            topY    != null                 -> topY
            else                            -> actor.y
        }
    }

    // ── Edge helpers ──────────────────────────────────────────────────────────

    private fun edgeLeft(a: Actor)   = if (a === this) 0f     else a.x
    private fun edgeRight(a: Actor)  = if (a === this) width  else a.x + a.width
    private fun edgeBottom(a: Actor) = if (a === this) 0f     else a.y
    private fun edgeTop(a: Actor)    = if (a === this) height else a.y + a.height

    // ── Snapshot management ───────────────────────────────────────────────────

    private fun watch(actor: Actor) {
        snapshots.getOrPut(actor) {
            floatArrayOf(actor.x, actor.y, actor.width, actor.height)
        }
    }

    private fun rebuildSnapshots() {
        snapshots.clear()
        watch(this)
        rules.forEach { (actor, params) ->
            watch(actor)
            params.allAnchors().forEach { watch(it) }
        }
        rebuildSnapshotEntries()
    }

    // Незмінна копія для безпечної ітерації в act()
    private fun rebuildSnapshotEntries() {
        snapshotEntries = snapshots.entries.map { it.key to it.value }
    }

    // ── Anchor safety ─────────────────────────────────────────────────────────

    private fun isUsedAsAnchor(actor: Actor): Boolean =
        rules.values.any { actor in it.allAnchors() }

    private fun removeRulesWithAnchor(removedAnchor: Actor) {
        val toRemove = rules.entries
            .filter { (_, p) -> removedAnchor in p.allAnchors() }
            .map    { it.key }
        toRemove.forEach { rules.remove(it) }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    override fun dispose() {
        rules.clear()
        snapshots.clear()
        snapshotEntries = emptyList()
        super.dispose()
    }
}