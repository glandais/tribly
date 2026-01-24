using Toybox.Graphics;

/**
 * Helper class for vertical text layout.
 * Automatically calculates Y positions based on font height.
 */
class VerticalLayout {
    private var _y;
    private var _spacing;

    function initialize(startY, spacing) {
        _y = startY;
        _spacing = spacing;
    }

    /**
     * Draw text and advance Y position.
     * @param dc Device context
     * @param x X position (center)
     * @param font Font to use
     * @param text Text to draw
     * @param options Unused (for future use)
     * @param color Text color
     */
    function draw(dc, x, font, text, options, color) {
        dc.setColor(color, Graphics.COLOR_TRANSPARENT);
        dc.drawText(x, _y, font, text, Graphics.TEXT_JUSTIFY_CENTER);
        _y += dc.getFontHeight(font) + _spacing;
    }

    /**
     * Add vertical spacing without drawing.
     * @param amount Pixels to skip
     */
    function skip(amount) {
        _y += amount;
    }

    /**
     * Get current Y position.
     */
    function getY() {
        return _y;
    }

    /**
     * Reset Y position.
     */
    function reset(startY) {
        _y = startY;
    }
}
