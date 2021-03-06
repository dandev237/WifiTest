package dandev237.wifitest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


/**
 * Clase que implementa un método de localización del dispositivo del usuario mediante la API de Google Play Services.
 *
 * <p/>
 * Autor: Daniel Castro García
 * Email: dandev237@gmail.com
 * Fecha: 16/05/2016
 */
public class GeolocationGPS implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = "GeolocationGPS";
    private static final int PERMISSION_REQUEST_FINE = 1111;

    private GoogleApiClient googleApiClient;
    private Context appContext;
    private Activity mainActivity;
    private Location location;
    private double latitude, longitude;

    public GeolocationGPS(Context appContext, Activity mainActivity) {
        latitude = 0.0;
        longitude = 0.0;

        this.appContext = appContext;
        this.mainActivity = mainActivity;

        googleApiClient = new GoogleApiClient.Builder(appContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Este método se ejecuta en la llamada a onStart() de MainActivity
     */
    public void connect() {
        googleApiClient.connect();
    }

    /**
     * Este método se ejecuta en la llamada a onStop() de MainActivity
     */
    public void disconnect() {
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Comprobación de permisos en runtime (Necesario porque ACCESS_FINE_LOCATION y ACCESS_COARSE_LOCATION son permisos
        // "Dangerous")
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, Manifest
                .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission
                    .ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_FINE);
        }

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

    //Getters

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
