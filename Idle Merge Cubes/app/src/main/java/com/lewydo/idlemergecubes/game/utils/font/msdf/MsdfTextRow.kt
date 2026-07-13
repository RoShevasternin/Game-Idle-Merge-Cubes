package com.lewydo.idlemergecubes.game.utils.font.msdf

import com.badlogic.gdx.scenes.scene2d.Group

// ─────────────────────────────────────────────────────────────────────────────
// MsdfTextRow — «багатий рядок»: кілька MsdfLabel РІЗНИХ шрифтів, розмірів,
// кольорів і ефектів В ОДИН РЯДОК, вирівняні по СПІЛЬНІЙ BASELINE
// (як мішаний текст у Figma — не по низу рамок, а по лінії письма).
//
//   val row = MsdfTextRow(gap = 8f)
//       .add(MsdfLabel(msdf.REWARD, "1500", 90f))       // число велике
//       .add(MsdfLabel(msdf.HINT,   "coins"))           // слово менше, інший шрифт
//   row.setPosition(x, y)
//
//   Кожна частина — повноцінний MsdfLabel: свій стиль, колір, stroke/тіні.
//   Це і є правильна відповідь на «різні шрифти й розміри в одному лейблі»:
//   scene2d Label тримає ОДИН шрифт — тож мішаний текст збирається композицією.
//
//   МЕЖІ (чесно): це ОДИН рядок — переносу (wrap) між частинами немає.
//   Якщо зміниш текст частини після додавання — виклич layoutRow() ще раз.
// ─────────────────────────────────────────────────────────────────────────────

class MsdfTextRow(
    /** Відстань між частинами (world-px). */
    var gap: Float = 0f,
) : Group() {

    private val parts = ArrayList<MsdfLabel>(4)

    fun add(label: MsdfLabel): MsdfTextRow {
        parts.add(label)
        addActor(label)
        layoutRow()
        return this
    }

    /** Кількість частин. */
    val partCount get() = parts.size

    /** Частина за індексом — повний доступ до MsdfLabel (колір, ефекти...). */
    fun part(index: Int): MsdfLabel = parts[index]

    /** Оновити текст частини. Ряд перекладається одразу. */
    fun setText(index: Int, text: CharSequence): MsdfTextRow {
        parts[index].setText(text)
        layoutRow()
        return this
    }

    fun clearParts(): MsdfTextRow {
        for (p in parts) p.remove()
        parts.clear()
        setSize(0f, 0f)
        return this
    }

    // ── Авто-релейаут ────────────────────────────────────────────────────────
    // Якщо будь-яка частина змінила розмір (setText на самій частині, зміна
    // worldSize/spacing) — помічаємо це в act() і перекладаємо ряд ще ДО
    // малювання кадру. Тож руками layoutRow() кликати не обов'язково.
    private var sizeStamp = -1f

    override fun act(delta: Float) {
        super.act(delta)
        var stamp = 0f
        for (p in parts) stamp += p.width * 31f + p.height
        if (stamp != sizeStamp) { sizeStamp = stamp; layoutRow() }
    }

    /** Перекласти частини: X — послідовно з gap, Y — по спільній baseline.
     *  Викликати після зміни тексту/розміру будь-якої частини. */
    fun layoutRow() {
        if (parts.isEmpty()) { setSize(0f, 0f); return }

        // спільна baseline ряду = найглибша серед частин
        var baseline = 0f
        for (p in parts) baseline = maxOf(baseline, baselineFromBottom(p))

        var x = 0f
        var top = 0f
        for (p in parts) {
            p.setPosition(x, baseline - baselineFromBottom(p))
            x += p.width + gap
            top = maxOf(top, p.y + p.height)
        }
        setSize(x - gap, top)
    }

    // Висота baseline від НИЗУ рамки лейбла — залежить від режиму рамки:
    //   figmaBox: рамка = lineHeight, baseline на basePx від ВЕРХУ
    //             → від низу = (lineHeight − basePx) × scale
    //   щільна:   Label центрує капітелі, baseline сидить на |descent| від низу
    private fun baselineFromBottom(l: MsdfLabel): Float {
        val d = l.font.bitmapFont.data
        return if (l.useFigmaBox)
            (d.lineHeight - l.font.basePx) * l.fontScaleY
        else
            -d.descent * l.fontScaleY
    }
}