/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ir.malihemoradi.mapapplication.helper

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit


class ForegroundOnlyLocationService : Service() {

    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager

    // TODO: Step 1.1, Review variables (no changes).
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    @SuppressLint("MissingPermission")
    override fun onCreate() {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // TODO: Step 1.2, Review the FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // TODO: Step 1.3, Create a LocationRequest.
        locationRequest = LocationRequest.create().apply {
            // Sets the desired interval for active location updates. This interval is inexact. You
            // may not receive updates at all if no location sources are available, or you may
            // receive them less frequently than requested. You may also receive updates more
            // frequently than requested if other applications are requesting location at a more
            // frequent interval.
            //
            // IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of
            // targetSdkVersion) may receive updates less frequently than this interval when the app
            // is no longer in the foreground.
            interval = TimeUnit.SECONDS.toMillis(60)

            // Sets the fastest rate for active location updates. This interval is exact, and your
            // application will never receive updates more frequently than this value.
            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            // Sets the maximum time when batched location updates are delivered. Updates may be
            // delivered sooner than this interval.
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        // TODO: Step 1.4, Initialize the LocationCallback.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Normally, you want to save a new location to a database. We are simplifying
                // things a bit and just saving it as a local variable, as we only need it again
                // if a Notification is created (when the user navigates away from app).
                currentLocation = locationResult.lastLocation

                // Notify our Activity that a new location was added. Again, if this was a
                // production app, the Activity would be listening for changes to a database
                // with new locations, but we are simplifying things a bit to focus on just
                // learning the location side of things.
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                // Updates notification content if this service is running as a foreground
                // service.
                if (serviceRunningInForeground) {
//                    notificationManager.notify(
//                        NOTIFICATION_ID,
//                        generateNotification(currentLocation))
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            try {
                if (it.isComplete && it.result != null) {
                    currentLocation = it.result

                    // Notify our Activity that a new location was added. Again, if this was a
                    // production app, the Activity would be listening for changes to a database
                    // with new locations, but we are simplifying things a bit to focus on just
                    // learning the location side of things.
                    val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, currentLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")

        SharedPreferenceUtil.saveLocationTrackingPref(this, true)

        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))

        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        try {
            // TODO: Step 1.6, Unsubscribe to location changes.
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        val cancelLocationTrackingFromNotification = intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)
//
//        if (cancelLocationTrackingFromNotification) {
////            unsubscribeToLocationUpdates()
//            stopSelf()
//        }
//        return START_NOT_STICKY
//    }

    override fun onBind(intent: Intent): IBinder {
//        stopForeground(true)
//        serviceRunningInForeground = false
//        configurationChange = false
        return localBinder
    }

//    override fun onRebind(intent: Intent) {
////        stopForeground(true)
////        serviceRunningInForeground = false
////        configurationChange = false
//        super.onRebind(intent)
//    }

//    override fun onUnbind(intent: Intent): Boolean {
//        if (!configurationChange && SharedPreferenceUtil.getLocationTrackingPref(this)) {
//            Log.d(TAG, "Start foreground service")
//            val notification = generateNotification(currentLocation)
//            startForeground(NOTIFICATION_ID, notification)
//            serviceRunningInForeground = true
//        }
//
//        // Ensures onRebind() is called if MainActivity (client) rebinds.
//        return true
//    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        configurationChange = true
//    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
     */
//    private fun generateNotification(location: Location?): Notification {
//        Log.d(TAG, "generateNotification()")
//
//        // Main steps for building a BIG_TEXT_STYLE notification:
//        //      0. Get data
//        //      1. Create Notification Channel for O+
//        //      2. Build the BIG_TEXT_STYLE
//        //      3. Set up Intent / Pending Intent for notification
//        //      4. Build and issue the notification
//
//        // 0. Get data
//        val mainNotificationText = location?.toText() ?: getString(R.string.no_location_text)
//        val titleText = getString(R.string.app_name)
//
//        // 1. Create Notification Channel for O+ and beyond devices (26+).
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            val notificationChannel = NotificationChannel(
//                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT)
//
//            // Adds NotificationChannel to system. Attempting to create an
//            // existing notification channel with its original values performs
//            // no operation, so it's safe to perform the below sequence.
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//
//        // 2. Build the BIG_TEXT_STYLE.
//        val bigTextStyle = NotificationCompat.BigTextStyle()
//            .bigText(mainNotificationText)
//            .setBigContentTitle(titleText)
//
//        // 3. Set up main Intent/Pending Intents for notification.
//        val launchActivityIntent = Intent(this, MainActivity::class.java)
//
//        val cancelIntent = Intent(this, ForegroundOnlyLocationService::class.java)
//        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)
//
//        val servicePendingIntent = PendingIntent.getService(
//            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val activityPendingIntent = PendingIntent.getActivity(
//            this, 0, launchActivityIntent, 0)
//
//        // 4. Build and issue the notification.
//        // Notification Channel Id is ignored for Android pre O (26).
//        val notificationCompatBuilder =
//            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
//
//        return notificationCompatBuilder
//            .setStyle(bigTextStyle)
//            .setContentTitle(titleText)
//            .setContentText(mainNotificationText)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .setOngoing(true)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////            .addAction(
////                R.drawable.ic_launch, getString(R.string.launch_activity),
////                activityPendingIntent
////            )
////            .addAction(
////                R.drawable.ic_cancel,
////                getString(R.string.stop_location_updates_button_text),
////                servicePendingIntent
////            )
//            .build()
//    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: ForegroundOnlyLocationService
            get() = this@ForegroundOnlyLocationService
    }

    companion object {
        private const val TAG = "CurrentLocation"

        private const val PACKAGE_NAME = "com.example.android.whileinuselocation"

        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 12345678

        private const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
    }
}
