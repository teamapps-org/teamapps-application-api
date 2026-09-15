package org.teamapps.application.api.theme;

import org.junit.Test;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.format.FontStyle;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ApplicationThemeColorsTest {
    @Test
    public void semanticColorsReachTheClientWithoutResolvingThemOnTheServer() {
        List<Color> colors = new ArrayList<>(List.of(ApplicationThemeColors.TEXT,
                ApplicationThemeColors.MUTED, ApplicationThemeColors.EMPHASIS,
                ApplicationThemeColors.SURFACE, ApplicationThemeColors.BORDER,
                ApplicationThemeColors.SELECTED_BACKGROUND));
        for (var status : List.of(ApplicationThemeColors.INFO, ApplicationThemeColors.SUCCESS,
                ApplicationThemeColors.WARNING, ApplicationThemeColors.DANGER, ApplicationThemeColors.NEUTRAL)) {
            colors.addAll(List.of(status.text(), status.background(), status.border()));
        }
        for (Color color : colors) {
            var ui = new FontStyle(1f, color, color, false, false, false).createUiFontStyle();
            assertEquals(color.toHtmlColorString(), ui.getFontColor());
            assertEquals(color.toHtmlColorString(), ui.getBackgroundColor());
            assertTrue(ui.getFontColor(), ui.getFontColor().startsWith("var(--ta-"));
        }
    }

    @Test
    public void neutralStatusFallsBackToThemeVariablesOnOlderClients() {
        assertEquals("var(--ta-state-neutral-bg, var(--ta-bg-color))", ApplicationThemeColors.NEUTRAL.background().toHtmlColorString());
        assertEquals("var(--ta-state-neutral-text, var(--ta-text-color))", ApplicationThemeColors.NEUTRAL.text().toHtmlColorString());
        assertEquals("var(--ta-state-neutral-border, var(--ta-inner-border-color))", ApplicationThemeColors.NEUTRAL.border().toHtmlColorString());
    }
}
