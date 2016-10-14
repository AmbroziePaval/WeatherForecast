package com.tapptitude.weatherforecast.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tapptitude.weatherforecast.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by ambroziepaval on 10/3/16.
 */
public class LocationPickerActivity extends Activity implements OnMapReadyCallback, View.OnClickListener {
    private Button mPickLocationB;
    private GoogleMap mMap;
    private LatLng mCenterPosition;
    private LatLng mDeviceLocation;
    private LocationManager mLocationManager;
    private String mLongitude = "23.6006";
    private String mLatitude = "46.7595";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        Bundle bundle = getIntent().getExtras();
        mLongitude = bundle.getString("longitude");
        mLatitude = bundle.getString("latitude");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.alp_f_map);
        mapFragment.getMapAsync(this);
        mPickLocationB = (Button) findViewById(R.id.alp_b_chooseLocation);
        mPickLocationB.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MyLocationListener.MY_PERMISSION_ACCESS_LOCATION);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        Location deviceLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (deviceLocation != null) {
            mDeviceLocation = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
            updateDeviceLocation();
        }

        mCenterPosition = mMap.getCameraPosition().target;

        LatLng lastKnownLocation = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude));
        CameraUpdate lastLocation = CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 16);
        mMap.animateCamera(lastLocation);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mCenterPosition = mMap.getCameraPosition().target;
            }
        });
    }

    protected void updateDeviceLocation() {
        mMap.clear();
        MarkerOptions deviceMarker = new MarkerOptions().position(mDeviceLocation).title("You").visible(true);
        mMap.addMarker(deviceMarker);
    }

    @Override
    public void onClick(View v) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", mCenterPosition.latitude);
        resultIntent.putExtra("longitude", mCenterPosition.longitude);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private class MyLocationListener implements LocationListener {
        public static final int MY_PERMISSION_ACCESS_LOCATION = 1;

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mDeviceLocation = new LatLng(latitude, longitude);
            updateDeviceLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
