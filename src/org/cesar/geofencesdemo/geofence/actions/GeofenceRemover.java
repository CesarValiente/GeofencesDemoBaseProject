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
import org.cesar.geofencesdemo.geofence.util.GeofenceUtils;
import org.cesar.geofencesdemo.geofence.util.GeofenceUtils.AddType;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;

public class GeofenceRemover implements ConnectionCallbacks, OnConnectionFailedListener,
        OnRemoveGeofencesResultListener {

    private final String LOG_TAG = GeofenceRemover.class.getSimpleName();

    // Storage for a context from the calling client
    private Context mContext;
    private GeofenceCallbacks mListener;

    private String mPlaceId;

    // Stores the current list of geofences
    private List<String> mCurrentGeofenceIds;

    // Stores the current instantiation of the location client
    private LocationClient mLocationClient;

    // The PendingIntent sent in removeGeofencesByIntent
    private PendingIntent mCurrentIntent;

    private AddType mAddType;

    /*
     * Record the type of removal. This allows continueRemoveGeofences to call
     * the appropriate removal request method.
     */
    private GeofenceUtils.RemoveType mRequestType;

    /*
     * Flag that indicates whether an add or remove request is underway. Check
     * this flag before attempting to start a new request.
     */
    private boolean mInProgress;

    /**
     * Default constructor
     * 
     * @param context
     */
    public GeofenceRemover(final Context context, final GeofenceCallbacks listener) {
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

    // FIXME INCLUDE Step9.2. getLocationClient()

    // FIXME INCLUDE Step9.5 requestConnection()

    // FIXME INCLUDE Step9.3 removeGeofencesByIntent()

    // FIXME INCLUDE Step9.8 onRemoveGeofencesByPendingIntentResult()

    // FIXME INCLUDE Step9.4 removeGeofencesById()

    // FIXME INCLUDE Step9.9 onRemoveGeofencesByRequestIdsResult()

    /**
     * Get a location client and disconnect from Location Services
     */
    private void requestDisconnection() {

        // A request is no longer in progress
        mInProgress = false;

        getLocationClient().disconnect();
        /*
         * If the request was done by PendingIntent, cancel the Intent. This
         * prevents problems if the client gets disconnected before the
         * disconnection request finishes; the location updates will still be
         * cancelled.
         */
        if (mRequestType == GeofenceUtils.RemoveType.INTENT) {
            mCurrentIntent.cancel();
        }

    }

    // FIXME INCLUDE Step9.11 onConnectionFailed()

    // FIXME INCLUDE Step9.6 onConnected()

    // FIXME INCLUDE Step9.7 continueRemoveGeofences()

    // FIXME INCLUDE Step9.10 onDisconnected()

}
