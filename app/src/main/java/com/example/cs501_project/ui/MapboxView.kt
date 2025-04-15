package com.example.cs501_project.ui

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import androidx.compose.runtime.mutableStateOf
import com.example.cs501_project.R
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

// separate mapbox view for the location screen
@Composable
fun MapboxView(
    modifier: Modifier = Modifier,
    initialCameraPosition: Point?, // the initial camera position is based on the current location
    predefinedMarkerLocations: List<Point> = emptyList(), // markers from historical place suggestions
    onMapClick: ((Point) -> Unit)? = null, // callback for map clicks
    customMarkers: List<Point> = emptyList() // markers added by user when they click on the map
) {

    // mutable state that holds android view that displays map and the remember ensures MapView instance is retained across recompositions
    val mapboxMapView = remember { mutableStateOf<MapView?>(null) }
    // holds mutable state for MapboxMap object which has programmatic control over the map such as adding layers
    val mapboxMap = remember { mutableStateOf<MapboxMap?>(null) }
    // mutable state for AnnotationPlugin, which keeps track of annotations on the map (in our case markers)
    val annotationPlugin = remember { mutableStateOf<AnnotationPlugin?>(null) }
    // specific type of AnnotationManager that is used for placing point markers
    val pointAnnotationManager = remember { mutableStateOf<PointAnnotationManager?>(null) }

    // used to embed an AndroidView (MapView for us) in our UI
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                mapboxMapView.value = this // stores the created MapView instance in the mapboxMapView state
                this.mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    mapboxMap.value = this.mapboxMap
                    annotationPlugin.value = this@apply.annotations // this@apply explicitly refers to the outer MapView
                    pointAnnotationManager.value = annotationPlugin.value?.createPointAnnotationManager()

                    // adding a default marker image
                    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_marker)
                    style.addImage("default_marker", bitmap)

                    // making the map zoom in on specific location
                    initialCameraPosition?.let {
                        mapboxMap.value?.setCamera(
                            CameraOptions.Builder()
                                .center(it)
                                .zoom(12.0)
                                .build()
                        )
                    }

                    // add predefined markers
                    predefinedMarkerLocations.forEach { point ->
                        pointAnnotationManager.value?.create(
                            PointAnnotationOptions()
                                .withPoint(point)
                                .withIconImage("default_marker")
                                .withIconSize(0.1)
                        )
                    }

                    // set up map click listener if the callback is provided
                    onMapClick?.let { clickCallback ->
                        mapboxMap.value?.addOnMapClickListener { point ->
                            clickCallback(point)
                            true
                        }
                    }
                }
            }
        },
        // updating the mapview when it receives new info
        update = { mapView ->
            mapboxMapView.value = mapView
            mapboxMap.value?.getStyle { style ->
                initialCameraPosition?.let {
                    mapboxMap.value?.setCamera(
                        CameraOptions.Builder()
                            .center(it)
                            .zoom(12.0)
                            .build()
                    )
                }

                // reload all the markers, both predefined and user-added
                pointAnnotationManager.value?.deleteAll()
                predefinedMarkerLocations.forEach { point ->
                    pointAnnotationManager.value?.create(
                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage("default_marker")
                            .withIconSize(0.1)
                    )
                }
                customMarkers.forEach { point ->
                    pointAnnotationManager.value?.create(
                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage("default_marker")
                            .withIconSize(0.1)
                    )
                }
            }
        }
    )
}