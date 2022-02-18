package com.nic.AGMTAssets.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nic.AGMTAssets.Activity.FullImageActivity;
import com.nic.AGMTAssets.Adapter.ViewImagesAdapter;
import com.nic.AGMTAssets.Api.Api;
import com.nic.AGMTAssets.Api.ApiService;
import com.nic.AGMTAssets.Api.ServerResponse;
import com.nic.AGMTAssets.Constant.AppConstant;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Utils.UrlGenerator;
import com.nic.AGMTAssets.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoogleMapFragment extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,Api.ServerResponseListener,
        LocationListener {
        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        Marker mCurrLocationMarker;
        LocationRequest mLocationRequest;
        private GoogleMap mMap;

        String type="";
        String form_id="";
        String asset_id="";
        String  hab_code= "";
        String  online_hab_code= "";
        String  online_form_id= "";
        String  online_asset_id= "";
        String  serial_n_o= "";
        int h_code;
        ArrayList<RoadListValue> onlineImageList;
        private PrefManager prefManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_maps);

                prefManager = new PrefManager(this);

                hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
                type = getIntent().getStringExtra("type");
                form_id = getIntent().getStringExtra("form_id");
                asset_id = getIntent().getStringExtra("asset_id");

                if(type.equals("Online")){
                        getOnlineImageList();
                }


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
                }
                SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.map);

                mapFragment.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                }
                } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
        }
        protected synchronized void buildGoogleApiClient() {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
                mGoogleApiClient.connect();
        }
        @Override
        public void onConnected(Bundle bundle) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
                }
        }
        @Override
        public void onConnectionSuspended(int i) {
        }
        @Override
        public void onLocationChanged(Location location) {
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
        getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
        return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
        double longitude = locations.getLongitude();
        double latitude = locations.getLatitude();
        Geocoder geocoder = new Geocoder(getApplicationContext(),
        Locale.getDefault());
        try {
        List<Address> listAddresses = geocoder.getFromLocation(latitude,
        longitude, 1);
        if (null != listAddresses && listAddresses.size() > 0) {
        String state = listAddresses.get(0).getAdminArea();
        String country = listAddresses.get(0).getCountryName();
        String subLocality = listAddresses.get(0).getSubLocality();
        markerOptions.title("" + latLng + "," + subLocality + "," + state
        + "," + country);
        }
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
        this);
        }
        }
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
        }
        public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.ACCESS_FINE_LOCATION)) {
        ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
        ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        MY_PERMISSIONS_REQUEST_LOCATION);
        }
        return false;
        } else {
        return true;
        }
        }
        @Override
        public void onRequestPermissionsResult(int requestCode,
                String permissions[], int[] grantResults) {
                switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                if (mGoogleApiClient == null) {
                buildGoogleApiClient();
                }
                mMap.setMyLocationEnabled(true);
                }
                } else {
                Toast.makeText(this, "permission denied",
                Toast.LENGTH_LONG).show();
                }
                return;
                }
                }
                }

        public void getOnlineImageList() {
                try {
                        new ApiService(this).makeJSONObjectRequest("imageList", Api.Method.POST, UrlGenerator.getRoadListUrl(), encryptedJson(), "not cache", this);
                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }
        public JSONObject encryptedJson() throws JSONException {
                String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), normalJson().toString());
                JSONObject dataSet = new JSONObject();
                dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
                dataSet.put(AppConstant.DATA_CONTENT, authKey);
                Log.d("iamgeListJsonParams", "" + dataSet);
                return dataSet;
        }
        public JSONObject normalJson() throws JSONException {
                JSONObject dataSet = new JSONObject();
                dataSet.put(AppConstant.KEY_SERVICE_ID, "agmt_habasset_photos_view");
                dataSet.put("hab_code", hab_code);
                dataSet.put("form_id", form_id);
                dataSet.put("asset_id", asset_id);
                Log.d("Image_LIST", "" + dataSet);
                return dataSet;
        }
        @Override
        public void OnMyResponse(ServerResponse serverResponse) {
                try {
                        String urlType = serverResponse.getApi();
                        JSONObject responseObj = serverResponse.getJsonResponse();

                        /////////////// ********** Now Not Need/////////////


                        ///////////// *********************///////////////

                        if ("imageList".equals(urlType) && responseObj != null) {
                                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                                        //new InsertAGMTFormTask().execute(jsonObject);
                                        new InsertAGMTFormDisplayAndCommonDataTask().execute(jsonObject);
                                }
                                Log.d("imageList", "" + responseDecryptedBlockKey);
                        }



                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

        @Override
        public void OnError(VolleyError volleyError) {

        }

        public class InsertAGMTFormDisplayAndCommonDataTask extends AsyncTask<JSONObject, Void, Void> {

                @Override
                protected Void doInBackground(JSONObject... params) {

                        if (params.length > 0) {
                                JSONArray jsonArray = new JSONArray();
                                try {
                                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                                } catch (JSONException e) {
                                        e.printStackTrace();
                                }
                                onlineImageList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                        RoadListValue habListValue = new RoadListValue();

                                        try {
                                                habListValue.setPmgsyDcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("dcode"))));
                                                habListValue.setPmgsyBcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("bcode"))));
                                                habListValue.setPmgsyHabcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("habcode"))));
                                                habListValue.setForm_id(((jsonArray.getJSONObject(i).getString("form_id"))));
                                                habListValue.setAsseet_id(((jsonArray.getJSONObject(i).getString("asset_id"))));
                                                habListValue.setSl_no(((jsonArray.getJSONObject(i).getString("sl_no"))));
                                                habListValue.setLatitude(((jsonArray.getJSONObject(i).getString("latitude"))));
                                                habListValue.setLongitude(((jsonArray.getJSONObject(i).getString("longitude"))));
                                                habListValue.setImageDescription(((jsonArray.getJSONObject(i).getString("image"))));
                                                habListValue.setImage(StringToBitmap((jsonArray.getJSONObject(i).getString("hab_asset_image"))));

                                                onlineImageList.add(habListValue);

                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }

                                }
                        }


                        return null;


                }

                @Override
                protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if(onlineImageList.size()>0){
                                addMarkersToMap();
                        }
                }
        }

        public Bitmap StringToBitmap(String image){
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                return decodedByte;
        }
        private void addMarkersToMap() {
                //mMap.clear();
                /*for (int i = 0; i < onlineImageList.size(); i++) {
                        LatLng ll = new LatLng(Double.parseDouble(onlineImageList.get(i).getLatitude()), Double.parseDouble(onlineImageList.get(i).getLongitude()));
                        BitmapDescriptor bitmapMarker;
                        Bitmap bitmap;
                        bitmapMarker = BitmapDescriptorFactory.fromBitmap(onlineImageList.get(i).getImage());
                        */
                /*switch (Cars.get(i).getState()) {
                                case 0:
                                        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                        Log.i("TAG", "RED");
                                        break;
                                case 1:
                                        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                        Log.i("TAG", "GREEN");
                                        break;
                                case 2:
                                        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                                        Log.i("TAG", "ORANGE");
                                        break;
                                default:
                                        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                        Log.i("TAG", "DEFAULT");
                                        break;
                        }*/
                /*
                        mMap.addMarker(new MarkerOptions().position(ll).title("").icon(bitmapMarker));


                        //Log.i("TAG","Car number "+i+"  was added " +mMarkers.get(mMarkers.size()-1).getId());
                }*/

                for(int i = 0 ; i < onlineImageList.size() ; i++) {
                        int height = 200;
                        int width = 200;

                        BitmapDescriptor bitmapMarker;
                        Bitmap bitmap;
                        Bitmap smallMarker = Bitmap.createScaledBitmap(onlineImageList.get(i).getImage(), width, height, false);
                        bitmapMarker = BitmapDescriptorFactory.fromBitmap(smallMarker);
                        createMarker(Double.parseDouble(onlineImageList.get(i).getLatitude()), Double.parseDouble(onlineImageList.get(i).getLongitude()),"","",  bitmapMarker);
                }
        }

        protected Marker createMarker(double latitude, double longitude, String title, String snippet, BitmapDescriptor bitmapMarker) {
                LatLng location = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                return mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(bitmapMarker));

        }

}



