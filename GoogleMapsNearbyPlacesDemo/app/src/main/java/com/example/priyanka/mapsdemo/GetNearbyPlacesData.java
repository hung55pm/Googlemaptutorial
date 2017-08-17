package com.example.priyanka.mapsdemo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.priyanka.mapsdemo.MapUtil.zoomFilterToSpan;
import static com.example.priyanka.mapsdemo.MarkerAnimation.animateMarkerToGB;
import static com.example.priyanka.mapsdemo.MarkerAnimation.animateMarkerToHC;
import static com.example.priyanka.mapsdemo.MarkerAnimation.animateMarkerToICS;

/**
 * @author Priyanka
 */

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    Thread thread;
    private List<HashMap<String, String>> nearbyPlaceList;
    private String googlePlacesData;
    private GoogleMap mMap;
    String url;
    double latitude, longitude;

    public GetNearbyPlacesData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        thread = null;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    int index = 0;

    @Override
    protected void onPostExecute(String s) {
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata", "called parse method");
        showNearbyPlaces(nearbyPlaceList);
        final Handler h= new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                double lat = Double.parseDouble(nearbyPlaceList.get(index).get("lat"));
                double lng = Double.parseDouble(nearbyPlaceList.get(index).get("lng"));
                double lat1 = Double.parseDouble(nearbyPlaceList.get(index + 1).get("lat"));
                double lng1 = Double.parseDouble(nearbyPlaceList.get(index + 1).get("lng"));
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).draggable(true).visible(true));

                animateMarkerToICS(marker, new LatLng(lat1, lng1), new LatLngInterpolator.Linear());
                zoomFilterToSpan(marker.getPosition(), new LatLng(lat1, lng1),
                        mMap);
                if(index==nearbyPlaceList.size()-2){
                   h.removeCallbacks(this);
                }else {
                    index++;
                    h.postDelayed(this, 3000);
                }

            }
        }, 100);


    }

    private void showNearbyPlaces(final List<HashMap<String, String>> nearbyPlaceList) {
        MarkerOptions markerOptionsfind = new MarkerOptions();
        LatLng lngs = new LatLng(latitude, longitude);
        markerOptionsfind.position(lngs);
        markerOptionsfind.title("bạn đang ở đây");
        markerOptionsfind.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markerOptionsfind);

        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        }

        for (int i = 0; i < nearbyPlaceList.size() - 1; i++) {
            double lat = Double.parseDouble(nearbyPlaceList.get(i).get("lat"));
            double lng = Double.parseDouble(nearbyPlaceList.get(i).get("lng"));
            double lat1 = Double.parseDouble(nearbyPlaceList.get(i + 1).get("lat"));
            double lng1 = Double.parseDouble(nearbyPlaceList.get(i + 1).get("lng"));
            Polyline line = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(lat, lng),
                            new LatLng(lat1, lng1)
                    ).width(2).color(Color.BLUE).geodesic(true)
            );


        }

    }

}

