GeofencesDemoBaseProject
========================

This project must be used together the project GeofencesDemo, this is just the structure of the project, but it needs the main functions that are located in GeofencesDemo.

In the demo we are going to follow the next steps, so the way to do it is just follow the order and copy and paste the functions from the project GeofencesDemo to this one (GeofencesDemoBaseProject).

1- Add Google Play Services as a library.

2- Add Geofence permission in your Androidmanifest:

	2.1 ACCESS_FINE_LOCATION

3- Check that Google Services apk is installed, include in the MainActivity:	 

	 3.1 servicesConnected() 

	 3.2 onActivityResult()

	 3.3 ErrorDialogFragment()

4- Add the following whole classes in the package data:

	4.1 SimpleGeofence

	4.2 SimpleGeofenceStorage

5- Add: 

	5.1 createGeofences() in the MainActivity 

	5.2 createRequestPendingIntent() in the GeofenceRequester

6- In the GeofenceRequester:

Add geofences 

	6.1 getLocationClient()

	6.2 addGeofences()	

	6.3 requestConnection()

	6.4 onConnected()
	
Add the Geofence to the LocationListener:

	6.5 continueAddGeofences()

Check the result returned after add the Geofence:

	6.6 onAddGeofencesResult()

7- ConnectionCallbacks in the GeofenceRequester, add:

	7.1 onDisconnected()

Handle connection errors, add:

	7.2 onConnectionFailed()

8- Add de package service, and inside of it the whole class:

	8.1 ReceiveTransitionIntentService

Add the service node to:

	8.2 AndroidManifest

9- Remove geofences, same strategy than the addGeofences:

	9.1 removeGeofences() in the MainActivity	

	9.2 getLocationClient() to GeofenceRemover, where we have an instance of this class

The rest is everything in the GeofenceRemover too:

Two different types of remove Geofences:

	By a list:

		9.3 removeGeofencesById()

	By intent:

		9.4 removeGeofencesByIntent()

Add: 

	9.5 requestConnection() method used to connect to the LocationClient service.		

Add:

	9.6 onConnected() which is the listener to continue with the removing action

Remove geofences, sending a request to the LocationServices:

	9.7 continueRemoveGeofences()

Add the listeners after we have removed successfully the Geofences:

	By list:

		9.8 onRemoveGeofencesByRequestIdsResult()

	By intent:

		9.9 onRemoveGeofencesByPendingIntentResult()

Add handle error functions:

	9.10 onnDisconnected()

	9.11 onConnectionFailed()		
