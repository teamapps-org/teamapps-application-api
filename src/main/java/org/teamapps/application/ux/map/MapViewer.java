/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2023 TeamApps.org
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

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiMapConfig;
import org.teamapps.event.Event;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.map.*;
import org.teamapps.ux.component.map.shape.MapPolyline;
import org.teamapps.ux.component.map.shape.ShapeProperties;
import org.teamapps.ux.component.panel.Panel;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapViewer<ENTITY> extends AbstractLazyRenderingApplicationView {

	public static final String DEFAULT_BRIGHT_STILE = "osm-liberty-intl";
	public static final String DEFAULT_DARK_STILE = "osm_liberty_dark_custom_intl";

	public final Event<MapMarkerData<ENTITY>> onMarkerClicked = new Event<>();
	private final String mapServer;
	private final String mapStyle;
	private String mapKey;

	private MapView<MapMarkerData<ENTITY>> tileMap;
	private MapView2<MapMarkerData<ENTITY>> vectorMap;
	private Panel panel;
	private Function<ENTITY, MapMarkerData<ENTITY>> entityToMarkerDataFunction;
	private boolean vectorMapVisible;
	private List<Marker<MapMarkerData<ENTITY>>> visibleMarkers = new ArrayList<>();

	public MapViewer(String mapServer, String mapStyle, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.mapServer = mapServer;
		this.mapStyle = mapStyle;
	}

	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}

	public void setEntityToMarkerDataFunction(Function<ENTITY, MapMarkerData<ENTITY>> entityToMarkerDataFunction) {
		this.entityToMarkerDataFunction = entityToMarkerDataFunction;
	}

	public void showEntities(ENTITY... entities) {
		showEntities(Arrays.asList(entities));
	}

	public void showEntities(List<ENTITY> entities) {
		showMarkers(entities.stream()
				.map(entity -> entityToMarkerDataFunction.apply(entity))
				.filter(Objects::nonNull)
				.filter(marker -> marker.getLocation() != null)
				.collect(Collectors.toList()));
	}

	public void showMarkers(MapMarkerData<ENTITY>... markers) {
		showMarkers(Arrays.asList(markers));
	}

	public void showMarkers(List<MapMarkerData<ENTITY>> markerDataList) {
		showMapMarkers(markerDataList.stream().map(data -> new Marker<>(data.getLocation(), data, data.getOffsetX(), data.getOffsetY())).collect(Collectors.toList()));
	}

	private void showMapMarkers(List<Marker<MapMarkerData<ENTITY>>> markers) {
		clearMap();
		DoubleSummaryStatistics latStatistics = markers.stream().mapToDouble(marker -> marker.getLocation().getLatitude()).summaryStatistics();
		DoubleSummaryStatistics lonStatistics = markers.stream().mapToDouble(marker -> marker.getLocation().getLongitude()).summaryStatistics();
		if (markers.size() > 1_000) {
			panel.setContent(tileMap);
			vectorMapVisible = false;
			tileMap.setMarkerCluster(markers);
			tileMap.fitBounds(new Location(latStatistics.getMin(), lonStatistics.getMin()), new Location(latStatistics.getMax(), lonStatistics.getMax()));
		} else {
			panel.setContent(vectorMap);
			vectorMapVisible = true;
			markers.forEach(marker -> vectorMap.addMarker(marker));
			if (markers.size() > 1) {
				vectorMap.fitBounds(new Location(latStatistics.getMin(), lonStatistics.getMin()), new Location(latStatistics.getMax(), lonStatistics.getMax()));
			} else if (markers.size() == 1) {
				vectorMap.setLocation(markers.get(0).getLocation(), 0, 15);
			}
		}
	}

	public void drawLine(Location loc1, Location loc2) {
		vectorMap.addPolyLine(new MapPolyline(List.of(loc1, loc2), new ShapeProperties(Color.MATERIAL_ORANGE_700, 1f)));
	}

	public void clearMap() {
		if (vectorMapVisible) {
			//visibleMarkers.forEach(m -> vectorMap.removeMarker(m));
			vectorMap.clearMarkers();
			vectorMap.clearShapes();
		} else {
			tileMap.clearMarkers();
		}
	}

	public void draw() {
		if(vectorMapVisible) {
		} else {
			tileMap.startDrawingShape(MapShapeType.RECTANGLE, new ShapeProperties(Color.MATERIAL_ORANGE_700, 1.5f, Color.MATERIAL_ORANGE_700.withAlpha(0.6f)));
		}
	}

	public void createUi() {
		panel = new Panel();
		panel.setHideTitleBar(true);
		panel.setCssStyle("border", "none");
		tileMap = createTileMap();
		vectorMap = createVectorMap();

		tileMap.onMarkerClicked.addListener(marker -> onMarkerClicked.fire(marker.getData()));
		vectorMap.onMarkerClicked.addListener(marker -> onMarkerClicked.fire(marker.getData()));
	}

	private MapView<MapMarkerData<ENTITY>> createTileMap() {
		MapView<MapMarkerData<ENTITY>> mapView = new MapView<>();
		String url = mapServer + "/styles/" + mapStyle + "/{z}/{x}/{y}.png?key=" + mapKey; //@2x.png";
		UiMapConfig config = new UiMapConfig();
		config.setUrlTemplate(url);
		config.setMinZoom(0);
		config.setMaxZoom(20);
		mapView.setMapConfig(config);
		mapView.setMarkerTemplateDecider(marker -> marker.getData().getTemplate());
		mapView.setZoomLevel(6);
		mapView.setMarkerPropertyProvider((mapMarkerData, propertyNames) -> mapMarkerData.getTemplateData());
		return mapView;
	}

	private MapView2<MapMarkerData<ENTITY>> createVectorMap() {
		MapView2<MapMarkerData<ENTITY>> mapView = new MapView2<>(mapServer, "ac", mapStyle + "/styles/" + mapStyle + "/style.json?key=" + mapKey);
		mapView.setMarkerTemplateDecider(marker -> marker.getData().getTemplate());
		mapView.setZoomLevel(6);
		mapView.setMarkerPropertyProvider((mapMarkerData, propertyNames) -> mapMarkerData.getTemplateData());
		mapView.setDisplayAttributionControl(false);
		return mapView;
	}

	@Override
	public Component getViewComponent() {
		return panel;
	}


}
