package com.tapptitude.weatherforecast.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ambroziepaval on 10/3/16.
 */
public class LocationPickerActivity extends Activity implements OnMapReadyCallback, View.OnClickListener {
    public static final int MY_PERMISSION_ACCESS_LOCATION = 1;
    public static final String KEY_LOCATION_LONGITUDE = "KEY_LOCATION_LONGITUDE";
    public static final String KEY_LOCATION_LATITUDE = "KEY_LOCATION_LATITUDE";
    private GoogleMap mMap;
    private LatLng mCenterPosition;
    private LatLng mDeviceLocation;
    private double mLongitude = 23.6006;
    private double mLatitude = 46.7595;

    MapFragment mMapFragment;
    @BindView(R.id.alp_b_chooseLocation)
    Button mPickLocationB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        readBundleData();
        ButterKnife.bind(this);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.alp_f_map);
        mMapFragment.getMapAsync(this);
        mPickLocationB.setOnClickListener(this);
    }

    private void readBundleData() {
        Bundle bundle = getIntent().getExtras();
        mLongitude = bundle.getDouble(KEY_LOCATION_LONGITUDE);
        mLatitude = bundle.getDouble(KEY_LOCATION_LATITUDE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    useMap();
                } else {
                    sendResultPosition(mLatitude, mLongitude);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_LOCATION);
        }

        useMap();
    }

    private void useMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        Location deviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (deviceLocation != null) {
            mDeviceLocation = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
            placeDeviceMarkerOnMap();
        }

        mCenterPosition = mMap.getCameraPosition().target;

        LatLng lastKnownLocation = new LatLng(mLatitude, mLongitude);
        CameraUpdate lastLocation = CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 16);
        mMap.animateCamera(lastLocation);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mCenterPosition = mMap.getCameraPosition().target;
            }
        });
    }

    protected void placeDeviceMarkerOnMap() {
        mMap.clear();
        MarkerOptions deviceMarker = new MarkerOptions().position(mDeviceLocation).title("You").visible(true);
        mMap.addMarker(deviceMarker);
    }

    @Override
    public void onClick(View v) {
        sendResultPosition(mCenterPosition.latitude, mCenterPosition.longitude);
    }

    private void sendResultPosition(double latitude, double longitude) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_LOCATION_LATITUDE, latitude);
        resultIntent.putExtra(KEY_LOCATION_LONGITUDE, longitude);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mDeviceLocation = new LatLng(latitude, longitude);
            placeDeviceMarkerOnMap();
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
