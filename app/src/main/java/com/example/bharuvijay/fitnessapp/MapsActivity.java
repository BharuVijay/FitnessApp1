package com.example.bharuvijay.fitnessapp;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMapClickListener, LocationListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private MarkerOptions marker;
    private ArrayList<LatLng> latLngList = null;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
     }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
            //map.getView().setClickable(true);
            // Focus and Zoom into the chosen route
            LatLng from = new LatLng(49.62244358, 6.10898852);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from, 15));
            // Zoom in, animating the camera.
            //mMap.animateCamera(CameraUpdateFactory.zoomIn());

            new BackgroundOperation().execute("");


        } catch (SecurityException ex) {
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

    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(getApplicationContext(),"Map Clicked" , Toast.LENGTH_LONG).show();
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

    public void onViewCreated(View view, Bundle savedInstanceState) {

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

    private void drawRoute() {

        /*LatLng from = new LatLng(49.62244358, 6.10898852);
        LatLng to = new LatLng(49.61864853, 6.11171365);
        latLngList.add(from);
        latLngList.add(to);
        polylineOptions = new PolylineOptions().add(new LatLng(49.62244358, 6.10898852),
                    new LatLng(49.61864853, 6.11171365));*/
        if (latLngList == null)
            return;
        if (polylineOptions == null)
            polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngList);
        mMap.addPolyline(polylineOptions);
    }

    private ArrayList getDirections(double lat1, double lon1, double lat2, double lon2) {

        Handler handler = new Handler(getApplicationContext().getMainLooper());
        try {
            final String urlstring = "http://maps.googleapis.com/maps/api/directions/xml?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric";
            URL url = new URL(urlstring);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
           InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            final String contentAsString = in.toString();
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), contentAsString, Toast.LENGTH_LONG).show();
                }
            });
            String tag[] = {"lat", "lng"};
            final int response = urlConnection.getResponseCode();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            if (doc != null) {
                NodeList nl1, nl2;
                nl1 = doc.getElementsByTagName(tag[0]);
                nl2 = doc.getElementsByTagName(tag[1]);
                if (nl1.getLength() > 0) {
                    latLngList = new ArrayList();
                    for (int i = 0; i < nl1.getLength(); i++) {
                        final Node node1 = nl1.item(i);
                        Node node2 = nl2.item(i);
                        double lat = Double.parseDouble(node1.getTextContent());
                        double lng = Double.parseDouble(node2.getTextContent());
                        latLngList.add(new LatLng(lat, lng));
                    }
                }
                else {
                    // No points found
                    handler.post(new Runnable() {
                                     public void run() {
                                         Toast.makeText(getApplicationContext(), "Doc is null", Toast.LENGTH_LONG).
                                                 show();
                                     }
                                 }
                    );
                }
            }
            else {

                // No points found
                handler.post(new Runnable() {
                                 public void run() {
                                     Toast.makeText(getApplicationContext(), "Doc is null", Toast.LENGTH_LONG).
                                             show();
                                 }
                             }
                );

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            final String error = e.getMessage();
            handler.post(new Runnable() {
                             public void run() {
                                 Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).
                                         show();
                             }
                         }
            );

        }

        return latLngList;
    }

    private class BackgroundOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                if (latLngList == null)
                    latLngList = new ArrayList<LatLng>();
                latLngList = getDirections(49.62244358, 6.10898852, 49.61864853, 6.11171365);

            } catch (Exception e) {
                Thread.interrupted();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            drawRoute();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
