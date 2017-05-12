/*
 * Copyright (c) 2016.
 *
 * DReflect and Minuku Libraries by Shriti Raj (shritir@umich.edu) and Neeraj Kumar(neerajk@uci.edu) is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Based on a work at https://github.com/Shriti-UCI/Minuku-2.
 *
 *
 * You are free to (only if you meet the terms mentioned below) :
 *
 * Share — copy and redistribute the material in any medium or format
 * Adapt — remix, transform, and build upon the material
 *
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *
 * Under the following terms:
 *
 * Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
 * NonCommercial — You may not use the material for commercial purposes.
 * ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
 * No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.
 */

package edu.umich.si.inteco.minuku_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import edu.umich.si.inteco.minuku.config.LocationPreference;
import edu.umich.si.inteco.minuku.config.SelectedLocation;
import edu.umich.si.inteco.minuku.logger.Log;

/**
 * Created by shriti on 8/16/16.
 * Activity to launch the place picker widget, show a map view of selected/preferred locations, and
 * show a list view of locations where a location item can also be deleted.
 * Uses SelectedLocation model object.
 * Uses LocationPreference for storing and modifying the list of locations
 */
public class LocationConfigurationActivity extends BaseActivity implements OnMapReadyCallback {

    private Button addLocationButton;
    private Button seeLocationButton;

    private int PLACE_PICKER_REQUEST = 1;
    private static GoogleMap mGoogleMap = null;
    private String locationLabel;

    private String TAG = "LocationConfigurationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);

        Log.d(TAG, "Getting the map fragmet to show google map on screen");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);

        addLocationButton = (Button) findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open the place picker widget
                Log.d(TAG, "Creating intent for Place picker widget");
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent placePickerIntent = null;
                try {
                    placePickerIntent = builder.build(LocationConfigurationActivity.this);
                    startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        seeLocationButton = (Button) findViewById(R.id.seeLocationButton);
        seeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to a new activity to show list
                Intent configureLocationIntent = new Intent(LocationConfigurationActivity.this, LocationListViewRenderActivity.class);
                startActivity(configureLocationIntent);
                Log.d(TAG, "Starting LocationListViewRenderActivity");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //TODO: not sure about this yet, need a better way to deal with map as a parameter to method calls
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            googleMap.setMyLocationEnabled(true);
            return;
        }
        Log.d(TAG, "Check for location permissions. Now updating location markers on map");
        LocationPreference.getInstance().updateMapMarkers(googleMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            Log.d(TAG, "PLACE_PICKER_REQUEST obtained");

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "RESULT OK");

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.location_label_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                final Context mContext = this;

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        locationLabel = userInput.getText().toString();
                                        Place place = PlacePicker.getPlace(data, mContext);
                                        String placename = String.format("%s", place.getName());
                                        String address = String.format("%s", place.getAddress());
                                        Log.d(TAG, "Adding location to the preferred location list");
                                        SelectedLocation newLocation = new SelectedLocation(placename, address,
                                                place.getLatLng().latitude, place.getLatLng().longitude, locationLabel,
                                                R.drawable.ic_delete_black_24dp);

                                        LocationPreference.getInstance().addLocation(newLocation);
                                        Log.d(TAG, "Updating the map with location markers");
                                        LocationPreference.getInstance().updateMapMarkers(mGoogleMap);
                                        Log.d(TAG, "Adding location data to preferences");
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }
        }


    }
}
