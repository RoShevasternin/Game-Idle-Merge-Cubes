package com.lewydo.idlemergecubes.game.actors.layout.constraintLayout

import com.badlogic.gdx.scenes.scene2d.Actor

// ═════════════════════════════════════════════════════════════════════════════
//  CLParams — правила позиціонування для одного актора в AConstraintLayout.
//
//  COORDINATE SYSTEM (libGDX, y=0 знизу):
//    topToTop(other)       — верх актора = верх other
//    topToBottom(other)    — верх актора = низ other     → актор НИЖЧЕ other
//    bottomToBottom(other) — низ актора  = низ other
//    bottomToTop(other)    — низ до верху other          → актор ВИЩЕ other
//    startToStart(other)   — лівий до лівого other
//    startToEnd(other)     — лівий до правого other      → актор ПРАВІШЕ other
//    endToEnd(other)       — правий до правого other
//    endToStart(other)     — правий до лівого other      → актор ЛІВІШЕ other
//
//  BIAS (при двох прив'язках одночасно):
//    horizontalBias = 0f   → до start краю
//    horizontalBias = 0.5f → по центру (default)
//    horizontalBias = 1f   → до end краю
//    verticalBias   = 0f   → до bottom краю
//    verticalBias   = 0.5f → по центру (default)
//    verticalBias   = 1f   → до top краю
// ═════════════════════════════════════════════════════════════════════════════

class CLParams(internal val layout: AConstraintLayout) {

    // ── Horizontal raw fields ─────────────────────────────────────────────────
    // Заповнюються або напряму або через shortcut-функції нижче.
    // null = прив'язка не встановлена

    var startToStartActor : Actor? = null
    var startToEndActor   : Actor? = null
    var endToEndActor     : Actor? = null
    var endToStartActor   : Actor? = null

    var horizontalBias: Float = 0.5f
        set(value) { field = value.coerceIn(0f, 1f) }

    var marginStart : Float = 0f
    var marginEnd   : Float = 0f

    // ── Vertical raw fields ───────────────────────────────────────────────────

    var topToTopActor      : Actor? = null
    var topToBottomActor   : Actor? = null
    var bottomToBottomActor: Actor? = null
    var bottomToTopActor   : Actor? = null

    var verticalBias: Float = 0.5f
        set(value) { field = value.coerceIn(0f, 1f) }

    var marginTop    : Float = 0f
    var marginBottom : Float = 0f

    // ── Shortcuts ─────────────────────────────────────────────────────────────
    // Іменовані як в Android ConstraintLayout.
    // anchor = null → прив'язка до самого layout (parent).

    // center

    /** По центру горизонтально відносно [anchor] (default = layout) */
    fun centerX(anchor: Actor? = null) {
        val a = anchor ?: layout
        startToStartActor = a;  endToEndActor = a
        marginStart = 0f;       marginEnd = 0f
        horizontalBias = 0.5f
    }

    /** По центру вертикально відносно [anchor] (default = layout) */
    fun centerY(anchor: Actor? = null) {
        val a = anchor ?: layout
        topToTopActor = a;   bottomToBottomActor = a
        marginTop = 0f;      marginBottom = 0f
        verticalBias = 0.5f
    }

    /** По центру обох осей відносно [anchor] (default = layout) */
    fun center(anchor: Actor? = null) { centerX(anchor); centerY(anchor) }

    // vertical

    /** Верхній край актора = верхній край [anchor] */
    fun topToTop(anchor: Actor? = null, margin: Float = 0f) {
        topToTopActor = anchor ?: layout
        marginTop     = margin
    }

    /** Верхній край актора = нижній край [anchor] — актор буде НИЖЧЕ anchor */
    fun topToBottom(anchor: Actor, margin: Float = 0f) {
        topToBottomActor = anchor
        marginTop        = margin
    }

    /** Нижній край актора = нижній край [anchor] */
    fun bottomToBottom(anchor: Actor? = null, margin: Float = 0f) {
        bottomToBottomActor = anchor ?: layout
        marginBottom        = margin
    }

    /** Нижній край актора = верхній край [anchor] — актор буде ВИЩЕ anchor */
    fun bottomToTop(anchor: Actor, margin: Float = 0f) {
        bottomToTopActor = anchor
        marginBottom     = margin
    }

    // horizontal

    /** Лівий край актора = лівий край [anchor] */
    fun startToStart(anchor: Actor? = null, margin: Float = 0f) {
        startToStartActor = anchor ?: layout
        marginStart       = margin
    }

    /** Лівий край актора = правий край [anchor] — актор буде ПРАВІШЕ anchor */
    fun startToEnd(anchor: Actor, margin: Float = 0f) {
        startToEndActor = anchor
        marginStart     = margin
    }

    /** Правий край актора = правий край [anchor] */
    fun endToEnd(anchor: Actor? = null, margin: Float = 0f) {
        endToEndActor = anchor ?: layout
        marginEnd     = margin
    }

    /** Правий край актора = лівий край [anchor] — актор буде ЛІВІШЕ anchor */
    fun endToStart(anchor: Actor, margin: Float = 0f) {
        endToStartActor = anchor
        marginEnd       = margin
    }

    // ── Internal: список всіх anchor-акторів (для watch реєстрації) ───────────

    fun allAnchors(): List<Actor> = listOfNotNull(
        startToStartActor, startToEndActor,
        endToEndActor,     endToStartActor,
        topToTopActor,     topToBottomActor,
        bottomToBottomActor, bottomToTopActor
    )
}