package com.example.bharuvijay.fitnessapp;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMapClickListener, LocationListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private MarkerOptions marker;
    private ArrayList<LatLng> latLngList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMyLocationEnabled(true);
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
            drawRoute();
            LatLng from = new LatLng(49.62244358, 6.10898852);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from,15));
                    // Zoom in, animating the camera.
                    //mMap.animateCamera(CameraUpdateFactory.zoomIn());

        }
        catch (SecurityException ex)
        {
            throw (ex);
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override public void onMapClick(LatLng point) {
        //add marker MarkerOptions
        marker = new MarkerOptions();
        marker.position(point);
        mMap.addMarker(marker);
        // setting polyline in the map
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        latLngList.add(point);
        polylineOptions.addAll(latLngList);
        mMap.addPolyline(polylineOptions);
    }


    @Override
    public void onLocationChanged(Location location) {

        /*mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("my position");

        mMap.addMarker(mp);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));*/

    }

    private void drawRoute()
    {
        if(latLngList == null)
            latLngList = new ArrayList<LatLng>();
        LatLng from = new LatLng(49.62244358, 6.10898852);
        LatLng to = new LatLng(49.61864853, 6.11171365);
        latLngList.add(from);
        latLngList.add(to);
        polylineOptions = new PolylineOptions().add(new LatLng(49.62244358, 6.10898852),
                    new LatLng(49.61864853, 6.11171365));
        //polylineOptions.addAll(latLngList);
        mMap.addPolyline(polylineOptions);
    }

}
