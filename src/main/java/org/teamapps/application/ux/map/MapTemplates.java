/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.ux.map;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.UiTemplate;
import org.teamapps.dto.UiTemplateReference;
import org.teamapps.ux.component.format.*;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.gridtemplate.GridTemplate;
import org.teamapps.ux.component.template.gridtemplate.ImageElement;
import org.teamapps.ux.component.template.gridtemplate.TextElement;
import org.teamapps.ux.component.template.htmltemplate.MustacheTemplate;
import org.teamapps.ux.session.SessionContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MapTemplates implements Template {

	USER_PIN(-20, 5, createUserPinTemplate()),
	PIN(-25, 3, createTemplate2()),
	CARD(-3, 3, createMarkerTemplate(32, 170, Color.MATERIAL_BLUE_700.withAlpha(0.9f), Color.MATERIAL_BLUE_900, 1, 5)),


	;

	private final int offsetX;
	private final int offsetY;
	private final Template template;
	private final UiTemplateReference uiTemplateReference;

	MapTemplates(int offsetX, int offsetY, Template template) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.template = template;
		this.uiTemplateReference = new UiTemplateReference(name());
	}

	@Override
	public UiTemplate createUiTemplate() {
		return uiTemplateReference;
	}

	@Override
	public List<String> getPropertyNames() {
		return template.getPropertyNames();
	}

	public Template getTemplate() {
		return template;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	private static RgbaColor colors[] = new RgbaColor[]{
			Color.MATERIAL_BLUE_700,
			Color.MATERIAL_ORANGE_700,
			Color.MATERIAL_GREEN_700,
			Color.MATERIAL_RED_800,
			Color.MATERIAL_AMBER_700,
			Color.MATERIAL_GREY_700,
			Color.MATERIAL_BROWN_700,
			Color.MATERIAL_DEEP_PURPLE_700,
			Color.MATERIAL_INDIGO_800,
			Color.MATERIAL_LIME_700,
			Color.MATERIAL_PINK_800,
			Color.MATERIAL_LIGHT_BLUE_800,
			Color.MATERIAL_LIGHT_GREEN_800,
			Color.MATERIAL_TEAL_800,
			Color.MATERIAL_YELLOW_900
	};

	private static Template userPinTemplate;
	private static Template pinTemplate;
	private static Template imageTemplate;
	private static Template[] imageTemplates;
	private static Template cardTemplate;
	private static Template[] cardTemplates;

	static {
		userPinTemplate = createUserPinTemplate();
		pinTemplate = createTemplate2();
		imageTemplate = createImageTemplate(Color.MATERIAL_ORANGE_700, 35, 2);
		cardTemplate = createMarkerTemplate(32, 170, Color.MATERIAL_BLUE_700.withAlpha(0.9f), Color.MATERIAL_BLUE_900, 1, 5);
		imageTemplates = new Template[colors.length];
		cardTemplates = new Template[colors.length];
		for (int i = 0; i < colors.length; i++) {
			imageTemplates[i] = createImageTemplate(colors[i], 35, 2);
			cardTemplates[i] = createMarkerTemplate(32, 170, colors[i].withAlpha(0.9f), colors[i].withBrightness(0.2f), 1, 5);
		}
	}

	public static void registerTemplatesForSession() {
		SessionContext.current().registerTemplates(Arrays.stream(MapTemplates.values())
				.collect(Collectors.toMap(Enum::name, MapTemplates::getTemplate)));
	}

	public static Template getImageTemplate(int id) {
		return imageTemplates[id % imageTemplates.length];
	}


	public static Template getCardTemplate(int id) {
		return cardTemplates[id % cardTemplates.length];
	}

	private static MustacheTemplate createTemplate() {
		String tpl =
				"<div style=\"background-image: url({{image}}); top:-17px; left:-60px; position: absolute;width: 80px; height: 80px;background-size: cover; background-position: center center; margin-bottom: 4px; border-radius: 50%; border: 2px solid {{borderColor}}; \"></div>\n" +
						"<div style='width:100%;height:100%; padding-left: 24px; padding-top: 5px; overflow: hidden;'>\n" +
						"<div style=\"white-space: nowrap;\">{{caption}}</div>\n" +
						"<div style=\"white-space: nowrap;\">{{description}}</div>\n" +
						"<div style=\"white-space: nowrap; width: fit-content; border-radius: 1000px; color:rgba(66,66,66,1);background-color:rgba(238,238,238,1);font-size:60%; padding: 1px 5px;\">{{badge}}</div>\n" +
						"</div>";
		return new MustacheTemplate(tpl);
	}

	private static Template createUserPinTemplate() {
		String tpl = "<div>\n" +
				"  <img src=\"ta-media/map-pin.png\" width=\"40\" height=\"56\" />\n" +
				"  <img style=\"top:4px; left:4px; position: absolute;width: 32px; height: 32px;border-radius: 50%; border: 2px solid white; \" src=\"{{image}}\" />\n" +
				"</div>\n";
		return new MustacheTemplate(tpl);
	}

	private static MustacheTemplate createImageTemplate(RgbaColor color, int size, int borderSize) {
		String tpl = "<div style=\"background-image: url({{image}}); width: " + size + "px; height: " + size + "px;background-size: cover; background-position: center center; border-radius: 50%; border: " + borderSize + "px solid " + color.toHtmlColorString() + "; \"></div>";
		return new MustacheTemplate(tpl);
	}

	private static MustacheTemplate createTemplate2() {
		String tpl = "<img src=\"ta-media/map-pin2.png\" width=\"50\" height=\"46\">";
		return new MustacheTemplate(tpl);
	}

	private static MustacheTemplate createPin2() {
		String tpl = "<img src=\"ta-media/map-pin2.png\" width=\"50\" height=\"46\">";
		return new MustacheTemplate(tpl);
	}

	private static Template createMarkerTemplate(int iconSize, int maxWidth, Color backgroundColor, Color borderColor, float borderWidth, float borderRadius) {
		GridTemplate tpl = new GridTemplate()
				.setPadding(new Spacing(0))
				.addColumn(SizingPolicy.AUTO)
				.addColumn(SizingPolicy.FRACTION)
				.addRow(SizeType.AUTO, 0, 0, 0, 1)
				.addRow(SizeType.AUTO, 0, 0, 0, 1)
				.addElement(new ImageElement(BaseTemplate.PROPERTY_IMAGE, 0, 0, iconSize, iconSize)
						.setRowSpan(2)
						.setBorder(new Border(new Line(Color.WHITE, LineType.SOLID, 1.0f)).setBorderRadius(300))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setMargin(new Spacing(1, 4, 1, 2)))
				.addElement(new TextElement(BaseTemplate.PROPERTY_CAPTION, 0, 1)
						.setWrapLines(false)
						.setFontStyle(new FontStyle(1f, Color.WHITE).setBold(true))
						.setPadding(new Spacing(2, 3, 1, 0))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT))
				.addElement(new TextElement(BaseTemplate.PROPERTY_DESCRIPTION, 1, 1)
						.setWrapLines(false)
						.setFontStyle(new FontStyle(0.8f, Color.WHITE).setBold(true))
						.setPadding(new Spacing(0, 3, 0, 0))
						.setVerticalAlignment(VerticalElementAlignment.TOP)
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT));

		tpl.setMaxWidth(maxWidth);
		tpl.setBackgroundColor(backgroundColor);
		if (borderColor != null) {
			tpl.setBorder(new Border(borderColor, borderWidth, borderRadius));
		}
		return tpl;
	}

}
