package org.teamapps.application.api.theme;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.CssStringColor;

/**
 * Semantic colors backed by the TeamApps client's light and dark CSS variables.
 * Values remain CSS expressions so existing components follow theme changes.
 * Hosts may override the variables; applications do not need a host-specific theme.
 */
public final class ApplicationThemeColors {

    public static final Color TEXT = Color.fromVariableName("--ta-text-color");
    public static final Color MUTED = Color.fromVariableName("--ta-text-color-gray");
    public static final Color EMPHASIS = Color.fromVariableName("--ta-link-color");
    public static final Color SURFACE = Color.fromVariableName("--ta-bg-color");
    public static final Color BORDER = Color.fromVariableName("--ta-inner-border-color");
    public static final Color SELECTED_BACKGROUND = Color.fromVariableName("--ta-active-color");

    public static final Status INFO = status("info");
    public static final Status SUCCESS = status("success");
    public static final Status WARNING = status("warning");
    public static final Status DANGER = status("danger");
    // Base-variable fallbacks also support clients predating the neutral status.
    public static final Status NEUTRAL = new Status(
            new CssStringColor("var(--ta-state-neutral-bg, var(--ta-bg-color))"),
            new CssStringColor("var(--ta-state-neutral-text, var(--ta-text-color))"),
            new CssStringColor("var(--ta-state-neutral-border, var(--ta-inner-border-color))"));

    /** A matched text/background/border set; use the colors together. */
    public record Status(Color background, Color text, Color border) {
        public String cssStyle() {
            return "color:" + text.toHtmlColorString() + ";background-color:"
                    + background.toHtmlColorString() + ";border-color:" + border.toHtmlColorString() + ";";
        }
    }

    /** A colored foreground for ordinary surfaces, not a status background. */
    public static Color tintedText(Color hue) {
        return new CssStringColor("color-mix(in srgb, var(--ta-text-color) 65%, "
                + hue.toHtmlColorString() + ")");
    }

    private static Status status(String name) {
        return new Status(Color.fromVariableName("--ta-state-" + name + "-bg"),
                Color.fromVariableName("--ta-state-" + name + "-text"),
                Color.fromVariableName("--ta-state-" + name + "-border"));
    }

    private ApplicationThemeColors() { }
}
