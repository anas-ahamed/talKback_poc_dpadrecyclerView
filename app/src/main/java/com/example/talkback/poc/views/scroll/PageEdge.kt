package com.example.talkback.poc.views.scroll

/**
 * @BottomPenultimate when the scroll position is one row before the bottom
 * @BottomAntePenultimate when the scroll position is two rows before the bottom
 */
sealed class PageEdge {
    object Top : PageEdge()
    object Unknown : PageEdge()
    object Bottom : PageEdge()
    object BottomPenultimate : PageEdge()
    object BottomAntePenultimate : PageEdge()
}
