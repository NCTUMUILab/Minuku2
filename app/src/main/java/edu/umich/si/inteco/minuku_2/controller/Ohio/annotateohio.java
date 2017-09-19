package edu.umich.si.inteco.minuku_2.controller.Ohio;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;

import edu.umich.si.inteco.minuku.logger.Log;
import edu.umich.si.inteco.minuku_2.R;

/**
 * Created by Lawrence on 2017/7/5.
 */

public class annotateohio extends Activity implements OnMapReadyCallback {

    private final String TAG = "annotateohio";

    private Context mContext;
    private MapFragment map;
    private MapView mMapView;
    private Barcode.GeoPoint geoPoint;
    private GoogleMap mGoogleMap;

    private Button submit;

    private Spinner activityspinner, preplanspinner;
    final String[] activityString = {"Choose an activity", "Static", "Biking", "In a car\\. (I\\'m the driver)",
            "In a car\\. (I\\'m NOT the driver)", "Taking a bus", "Walking outdoors", "Walking indoors"};
    final String[] preplanString = {"Did you have preplaned this trip?","Yes", "No"};

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotate_activity_ohio);

        initannotateohio(savedInstanceState);

    }

    public void initannotateohio(Bundle savedInstanceState){

        setUpMapIfNeeded();

        activityspinner = (Spinner)findViewById(R.id.activityspinner);
        preplanspinner = (Spinner)findViewById(R.id.preplanspinner);

        ArrayAdapter<String> activityList = new ArrayAdapter<String>(annotateohio.this,
                android.R.layout.simple_spinner_dropdown_item,
                activityString);
        ArrayAdapter<String> preplanList = new ArrayAdapter<String>(annotateohio.this,
                android.R.layout.simple_spinner_dropdown_item,
                preplanString);

        activityspinner.setAdapter(activityList);
        preplanspinner.setAdapter(preplanList);

        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(submitting);

    }

    private void setUpMapIfNeeded() {

        if (map==null)
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.Mapfragment));

        map.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng nkut = new LatLng(24.78680, 120.99722);
        LatLng end = new LatLng(24.79283, 120.99284);
        LatLng middle = new LatLng(24.78965, 120.99535);

        ArrayList<LatLng> points = new ArrayList<LatLng>();
        points.add(nkut);
        points.add(middle);
        points.add(end);
/*
        mGoogleMap.addMarker(new MarkerOptions().position(nkut).title("Here you are."));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nkut,15.0f));
*/
        showMapWithPaths(mGoogleMap, points, middle);

    }

    public static void showMapWithPaths(GoogleMap map, ArrayList<LatLng> points, LatLng cameraCenter) {

        //map option
        GoogleMapOptions options = new GoogleMapOptions();
        options.tiltGesturesEnabled(false);
        options.rotateGesturesEnabled(false);

        //center the map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraCenter, 13));

        // Marker start = map.addMarker(new MarkerOptions().position(startLatLng));

        //draw linges between points and add end and start points
        PolylineOptions pathPolyLineOption = new PolylineOptions().color(Color.RED).geodesic(true);
        pathPolyLineOption.addAll(points);

        //draw lines
        Polyline path = map.addPolyline(pathPolyLineOption);
    }

    private Button.OnClickListener submitting = new Button.OnClickListener() {
        public void onClick(View v) {
            Log.e(TAG, "submit clicked");

            /* TODO storing data */

            annotateohio.this.finish();
        }
    };

}
