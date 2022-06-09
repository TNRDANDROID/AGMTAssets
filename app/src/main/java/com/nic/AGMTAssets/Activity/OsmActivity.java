package com.nic.AGMTAssets.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.system.Os;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
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
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OsmActivity extends Activity implements Api.ServerResponseListener {
    private MapView map;
    private IMapController mapController;

    private static final String TAG = "OsmActivity";


    private static final int PERMISSION_REQUEST_CODE = 1;

    ArrayList<RoadListValue> onlineImageList;
    String type="";
    String form_id="";
    String asset_id="";
    String  hab_code= "";
    private PrefManager prefManager;
    ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay;
    ArrayList<OverlayItem> getLatLongs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here


        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map

        setContentView(R.layout.activity_osm_activty);
        prefManager = new PrefManager(this);
        hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
        type = getIntent().getStringExtra("type");
        form_id = getIntent().getStringExtra("form_id");
        asset_id = getIntent().getStringExtra("asset_id");


        if (Build.VERSION.SDK_INT >= 23) {
            if (isStoragePermissionGranted()){

            }
        }


        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(51496994, -134733);
        mapController.setCenter(startPoint);

        //loadKml();
        if (Utils.isOnline()) {
            getOnlineImageList();
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

    public void loadKml() {
        new KmlLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                else {

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


    class KmlLoader extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(OsmActivity.this);
        KmlDocument kmlDocument;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading Project...");
            progressDialog.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            kmlDocument = new KmlDocument();
            kmlDocument.parseKMLStream(getResources().openRawResource(R.raw.study_areas), null);
            FolderOverlay kmlOverlay = (FolderOverlay)kmlDocument.mKmlRoot.buildOverlay(map, null, null,kmlDocument);
            map.getOverlays().add(kmlOverlay);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            map.invalidate();
            BoundingBox bb = kmlDocument.mKmlRoot.getBoundingBox();
            map.zoomToBoundingBox(bb, true);
//            mapView.getController().setCenter(bb.getCenter());
            super.onPostExecute(aVoid);
        }
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (map != null)
            map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (map != null)
            map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
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
            getLatLongs = new ArrayList<OverlayItem>();

            for (int i=0;i<onlineImageList.size();i++){
                RoadListValue locListValue = new RoadListValue();
                double latitude = Double.parseDouble(onlineImageList.get(i).getLatitude());
                double longtitude = Double.parseDouble(onlineImageList.get(i).getLongitude());
                getLatLongs.add(new OverlayItem(
                        "Location", "Chennai", new GeoPoint(latitude, longtitude)));
            }
            try{
                IGeoPoint geoPoint = new GeoPoint(Double.parseDouble(onlineImageList.get(0).getLatitude()),Double.parseDouble(onlineImageList.get(0).getLongitude()));
                anotherItemizedIconOverlay
                        = new ItemizedIconOverlay<OverlayItem>(OsmActivity.this, getLatLongs, null);
                map.getOverlays().add(anotherItemizedIconOverlay);
                map.getController().animateTo(geoPoint);
                //---
                anotherItemizedIconOverlay = new ItemizedOverlayWithFocus<OverlayItem>(OsmActivity.this, getLatLongs, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //here it should decide if the title & description is already shown or not. (true => hide it, false => display it)
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });

                //Add Scale Bar
                /*ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
                map.getOverlays().add(myScaleBarOverlay);*/
            }
            catch (Exception e){
                Log.d("Error",e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Bitmap StringToBitmap(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


}