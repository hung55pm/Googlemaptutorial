package com.example.priyanka.mapsdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;


public class MapUtil {
    private static MapUtil util = new MapUtil();
    private static Context c;
    private static LatLng mLastLatlng;

    private MapUtil() {
    }

    public static MapUtil init(Context c) {
        MapUtil.c = c;
        return util;
    }

    /**
     * Get the current map zoom
     *
     * @param aMap
     * @return
     */
    public static float getMapZoom(GoogleMap aMap) {
        return aMap.getCameraPosition().zoom;
    }

    protected static int getWalkColor() {
        return Color.parseColor("#6db74d");
    }

    protected static int getCarColor() {
        return Color.parseColor("#00ff00");
    }

    protected static float getBuslineWidth() {
        return 10;
    }

    /**
     * Two linear distance
     *
     * @return
     */
    public static int getDistatce(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        // Earth radius
        double R = 6371;
        // Distance between two points km, if you want rice, the result can be *1000
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;
        return (int) (d * 1000);
    }


    public static float zoomFilterToSpan(final LatLng lat1, final LatLng lat2,
                                         final GoogleMap aMap) {
        LatLngBounds bounds = getLatLngFiletrBounds(lat1, lat2, aMap);
        CameraUpdate newLatLngBounds = CameraUpdateFactory.newLatLngBounds(
                bounds, 0);
        float bearing = bearingBetweenLatLngs(lat1, lat2);
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(lat2) // changed this...
                        .bearing(bearing)
                        .tilt(90)
                        .zoom(aMap.getCameraPosition().zoom)
                        .build();


        aMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition)
        );


        return getMapZoom(aMap);
    }


    private static float bearingBetweenLatLngs(LatLng begin, LatLng end) {
        Location beginL = convertLatLngToLocation(begin);
        Location endL = convertLatLngToLocation(end);

        return beginL.bearingTo(endL);
    }

    private static Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    private static boolean changeCamera;

    public static LatLngBounds getLatLngFiletrBounds(LatLng l1, LatLng l2,
                                                     GoogleMap aMap) {
        double lat1 = l1.latitude, lng1 = l1.longitude, lat2 = l2.latitude, lng2 = l2.longitude;
        double lngD = lng2 - lng1;
        // aMap.animateCamera(changeBearing);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(l1);
        b.include(l2);
        return b.build();
    }

//    public static void animateCameraNO(LatLng latLng1, float f, GoogleMap aMap) {
//	CameraUpdate changeLatLng = CameraUpdateFactory.newLatLngZoom(latLng1,
//		f);
//	aMap.animateCamera(changeLatLng);
//    }

    public static Bitmap drawText2Bitmap(String text, int imgResourceId,
                                         Context mContext) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Log.wtf("Bitmap", "scale:" + scale);
            Bitmap bitmap = BitmapFactory.decodeResource(resources,
                    imgResourceId);
            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            if (bitmapConfig == null)
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            bitmap = bitmap.copy(bitmapConfig, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                    | Paint.DEV_KERN_TEXT_FLAG); // new antialised
            paint.setColor(Color.parseColor("#ff0000")); // text color - #3D3D3D
            paint.setTextSize((int) (20 * scale)); // text size in pixels
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 4;
            int y = (bitmap.getHeight() + bounds.height()) / 3;
            canvas.drawText(text, x * scale, y * scale - bitmap.getWidth() / 4,
                    paint);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }





    private static Marker bgMarker;

    private static LatLng getMiddleP(LatLng latlng_start, LatLng latlng_end) {
        double latitude_s = latlng_start.latitude;
        double longitude_s = latlng_start.longitude;
        double latitude_e = latlng_end.latitude;
        double longitude_e = latlng_end.longitude;
        double lat_m = (latitude_s + latitude_e) / 2;
        double lng_m = (longitude_s + longitude_e) / 2;
        return new LatLng(lat_m, lng_m);
    }

    // ************************************************************************************************
    private static Bitmap startBit;
    private static Bitmap endBit;
    private static AssetManager am;
    private static Marker mAinmMarker;
    //    private static AnimationDrawable drawable;
    private static BitmapDescriptor anim_bitmap;

    protected static BitmapDescriptor getStartBitmapDescriptor() {
        return getBitDes(startBit, "amap_start.png");
    }

    protected static BitmapDescriptor getEndBitmapDescriptor() {
        return getBitDes(endBit, "amap_end.png");
    }

    private static BitmapDescriptor getBitDes(Bitmap bitmap, String fileName) {
        if (am == null) {
            am = c.getResources().getAssets();
        }
        InputStream stream;
        try {
            stream = am.open(fileName);
            bitmap = BitmapFactory.decodeStream(stream);
            bitmap = zoomBitmap(bitmap, 1);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, float res) {
        if (bitmap == null) {
            return null;
        }
        int width, height;
        width = (int) (bitmap.getWidth() * res);
        height = (int) (bitmap.getHeight() * res);
        Bitmap newbmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return newbmp;
    }

}
