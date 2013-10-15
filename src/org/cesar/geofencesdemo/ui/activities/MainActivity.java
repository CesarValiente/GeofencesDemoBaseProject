/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * Modifications and additions by: Cesar Valiente 
 * mail: cesar.valiente@gmail.com
 * twitter: @CesarValiente
 *
 *
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
 */

package org.cesar.geofencesdemo.ui.activities;

import java.util.ArrayList;
import java.util.List;

import org.cesar.geofencesdemo.R;
import org.cesar.geofencesdemo.geofence.actions.GeofenceRemover;
import org.cesar.geofencesdemo.geofence.actions.GeofenceRequester;
import org.cesar.geofencesdemo.geofence.callbacks.GeofenceCallbacks;
import org.cesar.geofencesdemo.geofence.data.GeofenceLocationDetails;
import org.cesar.geofencesdemo.geofence.util.CommonUtils;
import org.cesar.geofencesdemo.geofence.util.GeofenceUtils;
import org.cesar.geofencesdemo.geofence.util.GeofenceUtils.AddType;
import org.cesar.geofencesdemo.geofence.util.GeofenceUtils.RemoveType;
import org.cesar.geofencesdemo.geofence.util.GeofenceUtils.RequestType;
import org.cesar.geofencesdemo.map.MapSearchAutocompletion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends ActionBarActivity implements OnCameraChangeListener, GeofenceCallbacks,
        OnItemClickListener {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    /*
     * Use to set an expiration time for a geofence. After this amount of time
     * Location Services will stop tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

    private static final float GEOFENCE_RADIUS = 50.0f;

    private RequestType mRequestType;
    private RemoveType mRemoveType;

    private List<Geofence> mGeofenceList;
    private List<String> mGeofencesToRemove;
    private SimpleGeofence mSimplegeofence;
    private ArrayAdapter<String> mAutocompleteAdapter;

    private SimpleGeofenceStore mGeofenceStorage;

    // Request and remove handlers
    private GeofenceRequester mGeofenceRequester;
    private GeofenceRemover mGeofenceRemover;

    // Gets the most recently position
    private Double mLatitudeNow;
    private Double mLongitudeNow;
    private GeofenceLocationDetails mLocationDetailsNow;

    private AutoCompleteTextView mAutocompleteText;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mCountry;
    private TextView mCity;
    private TextView mAddress;

    private ActionBar mActionBar;

    // Gogole map object we use to get all things we need
    private GoogleMap mGoogleMap;

    // This is the id used to add geofences in this demo, to use this code on a
    // much more versatil way
    // we have to change this id, and for instance use this as the objects id we
    // have to retrieve info
    // when the transition happens
    private final int PLACE_ID = 1;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();

        mAutocompleteText = (AutoCompleteTextView) findViewById(R.id.map_autocomplete_textview);
        mLatitudeText = (TextView) findViewById(R.id.map_latitude_text);
        mLongitudeText = (TextView) findViewById(R.id.map_longitude_text);
        mCountry = (TextView) findViewById(R.id.map_country_text);
        mCity = (TextView) findViewById(R.id.map_city_text);
        mAddress = (TextView) findViewById(R.id.map_address_text);

        // Instantiate a new geofence storage area
        mGeofenceStorage = new SimpleGeofenceStore(getBaseContext());

        mGeofenceList = new ArrayList<Geofence>(1);
        mGeofencesToRemove = new ArrayList<String>(1);

        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(getBaseContext(), this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(getBaseContext(), this);

        // Autocomplete stuff
        mAutocompleteAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item);
        mAutocompleteText.setAdapter(mAutocompleteAdapter);
        mAutocompleteText.setOnItemClickListener(this);

        mAutocompleteText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                setProgressBarIndeterminateVisibility(true);
                new MapSearchAutocompletion(s.toString(), MainActivity.this).execute();
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(final Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_save:
            // If we had another geofence previously, we have to remove it
            if (mSimplegeofence != null) {
                removeGeofences(AddType.ADD_AFTER_REMOVE);
            } else if (mLatitudeNow != null && mLongitudeNow != null) {
                createGeofences();
            }
            return true;
        case R.id.action_delete:
            if (mGeofencesToRemove.size() > 0) {
                removeGeofences(AddType.NONE);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void showSuggestions(final ArrayList<String> suggestions) {

        if (mAutocompleteAdapter != null && mAutocompleteAdapter.getCount() > 0) {
            mAutocompleteAdapter.clear();
        }
        mAutocompleteAdapter.addAll(suggestions);
        mAutocompleteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Address address = GeofenceUtils.getReverseLocationDetails(this, str);
        if (address != null) {
            CommonUtils.hideSoftKeyboard(this, mAutocompleteText);
            updateCamera(address.getLatitude(), address.getLongitude());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSimplegeofence = mGeofenceStorage.getGeofence(String.valueOf(PLACE_ID));
        if (mSimplegeofence != null) {
            mGeofenceList.add(0, mSimplegeofence.toGeofence());
            mGeofencesToRemove.add(0, String.valueOf(PLACE_ID));
        }
        setUpMapIfNeeded();
    }

    // FIXME INCLUDE Step3.1 servicesConnected()

    // FIXME INCLUDE Step3.2 onActivityForResult()

    // --------- Check google Play services is available -------------//

    // ------------------- Add geofences --------------------------------//

    // FIXME INCLUDE Step5.1 createGeofences()

    // ------------------- Remove geofences ---------------------------------//

    // FIXME INCLUDE Step9.1 removeGeofences()

    @Override
    public void addGeofenceListener(final String placeId) {

        if (placeId.equals(String.valueOf(PLACE_ID))) {
            Log.d(LOG_TAG, "Added new geofence: " + placeId);
            CommonUtils.showShortToast(this, R.string.geofence_added);
        }
    }

    @Override
    public void removeGeofenceListener(final String placeId, final AddType addType) {
        if (placeId.equals(String.valueOf(PLACE_ID))) {
            Log.d(LOG_TAG, "Removed received. PlaceID: " + placeId);
            mGeofenceStorage.clearGeofence(mGeofencesToRemove);
            mGeofenceList.clear();
            mGeofencesToRemove.clear();
            mSimplegeofence = null;
            if (addType == AddType.ADD_AFTER_REMOVE) {
                createGeofences();
            } else {
                CommonUtils.showShortToast(this, R.string.geofence_deleted);
            }
        }
    }

    @Override
    public void errorGeofenceListener(final String placeId, final String message) {
        Log.d(LOG_TAG, "Error received. PlaceId: " + placeId + "\nMessage: " + message);
        CommonUtils.showShortToast(this, message);
    }

    // FIXME INCLUDE Step3.3 ErrorDialogFragment()

    // ------------------ Maps ----------------------------------------//

    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mGoogleMap == null) {
            mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mGoogleMap != null) {
                // The Map is verified. It is now safe to manipulate the map.
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setOnCameraChangeListener(this);

                LatLng latLng = null;
                // If we have a previous location set, we go there
                if (mSimplegeofence != null) {
                    latLng = new LatLng(mSimplegeofence.getLatitude(), mSimplegeofence.getLongitude());
                } else {
                    // If we don't have a previous location set, we try to go to
                    // the last known position, if it's
                    // not possible, then we go to the 0.0, 0.0 location
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    String bestProvider = locationManager.getBestProvider(new Criteria(), true);
                    Location location = locationManager.getLastKnownLocation(bestProvider);
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    } else {
                        latLng = new LatLng(0, 0);
                    }
                }
                updateCamera(latLng);
            }
        }
    }

    @Override
    public void onCameraChange(final CameraPosition cameraPosition) {

        LatLng latlng = cameraPosition.target;
        mLatitudeNow = latlng.latitude;
        mLongitudeNow = latlng.longitude;
        mLatitudeText.setText("latitude: " + mLatitudeNow);
        mLongitudeText.setText("longitude: " + mLongitudeNow);

        mLocationDetailsNow = GeofenceUtils.getLocationDetails(this, latlng.latitude, latlng.longitude);
        if (mLocationDetailsNow != null) {
            mCountry.setText("Country: " + mLocationDetailsNow.getCountry());
            mCity.setText("City: " + mLocationDetailsNow.getCity());
            mAddress.setText("Address: " + mLocationDetailsNow.getAddress());
        }
    }

    private void updateCamera(final double latitude, final double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);
        updateCamera(latLng);
    }

    private void updateCamera(final LatLng latLng) {
        if (latLng != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

}
