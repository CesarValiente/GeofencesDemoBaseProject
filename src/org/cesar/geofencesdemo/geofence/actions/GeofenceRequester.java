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

package org.cesar.geofencesdemo.geofence.actions;

import java.util.List;

import org.cesar.geofencesdemo.geofence.callbacks.GeofenceCallbacks;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;

/**
 * Class for connecting to Location Services and requesting geofences. <b> Note:
 * Clients must ensure that Google Play services is available before requesting
 * geofences. </b> Use GooglePlayServicesUtil.isGooglePlayServicesAvailable() to
 * check.
 * 
 * 
 * To use a GeofenceRequester, instantiate it and call AddGeofence(). Everything
 * else is done automatically.
 * 
 */
public class GeofenceRequester implements OnAddGeofencesResultListener, ConnectionCallbacks,
        OnConnectionFailedListener {

    private final String LOG_TAG = GeofenceRequester.class.getSimpleName();

    private String mPlaceId;

    private final Context mContext;
    private GeofenceCallbacks mListener;

    // Stores the PendingIntent used to send geofence transitions back to the
    // app
    private PendingIntent mGeofencePendingIntent;

    // Stores the current list of geofences
    private List<Geofence> mCurrentGeofences;

    // Stores the current instantiation of the location client
    private LocationClient mLocationClient;

    /*
     * Flag that indicates whether an add or remove request is underway. Check
     * this flag before attempting to start a new request.
     */
    private boolean mInProgress;

    /**
     * Default private constructor
     * 
     * @param context
     */
    public GeofenceRequester(final Context context, final GeofenceCallbacks listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * Set the "in progress" flag from a caller. This allows callers to re-set a
     * request that failed but was later fixed.
     * 
     * @param flag
     *            Turn the in progress flag on or off.
     */
    public void setInProgressFlag(final boolean flag) {
        mInProgress = flag;
    }

    /**
     * Get the current in progress status.
     * 
     * @return The current value of the in progress flag.
     */
    public boolean getInProgressFlag() {
        return mInProgress;
    }

    /**
     * Returns the current PendingIntent to the caller.
     * 
     * @return The PendingIntent used to create the current set of geofences
     */
    public PendingIntent getRequestPendingIntent() {
        return createRequestPendingIntent();
    }

    // FIXME INCLUDE Step6.3 requestConnection()

    // FIXME INCLUDE Step6.1 getLocationClient()

    // FIXME INCLUDE Step6.2 addGeofences(final List<Geofence> geofences, final String placeId)

    // FIXME INCLUDE Step6.5 continueAddGeofences()

    // FIXME INCLUDE Step6.4 onConnected()

    // FIXME INCLUDE Step7.1 onDisconnected()

    // FIXME INCLUDE Step7.2 onConnectionFailed()

    /**
     * Get a location client and disconnect from Location Services
     */
    private void requestDisconnection() {

        // A request is no longer in progress
        mInProgress = false;

        getLocationClient().disconnect();
    }

    // FIXME INCLUDE Step6.6 onAddGeofencesResult()

    // FIXME INCLUDE Step5.2 createRequestPendingIntent()

}
