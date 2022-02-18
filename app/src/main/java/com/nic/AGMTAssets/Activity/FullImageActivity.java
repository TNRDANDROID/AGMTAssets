package com.nic.AGMTAssets.Activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.AGMTAssets.Adapter.ViewImagesAdapter;
import com.nic.AGMTAssets.Api.Api;
import com.nic.AGMTAssets.Api.ApiService;
import com.nic.AGMTAssets.Api.ServerResponse;
import com.nic.AGMTAssets.Constant.AppConstant;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Utils.UrlGenerator;
import com.nic.AGMTAssets.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity implements Api.ServerResponseListener{

    ImageView back,home_img;
    RecyclerView image_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    ArrayList<RoadListValue> imageList;

    ViewImagesAdapter viewImagesAdapter;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_recycler);

        prefManager = new PrefManager(this);
        back = findViewById(R.id.back_img);
        home_img = findViewById(R.id.home_img);
        image_recycler = findViewById(R.id.image_preview_recyclerview);
        //image_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        image_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
        type = getIntent().getStringExtra("type");
        form_id = getIntent().getStringExtra("form_id");
        asset_id = getIntent().getStringExtra("asset_id");

        if(type.equals("Offline")){
            getAgmtForms();
        }
        else {
            getOnlineImageList();
        }

    }

    public void getAgmtForms() {
        imageList = new ArrayList<>();
        dbData.open();
        imageList.clear();

        imageList.addAll(dbData.getAgmtImages(form_id,asset_id,hab_code));
        //Collections.sort(agmtFormList, (lhs, rhs) -> lhs.getForm_id().compareTo(rhs.getForm_id()));
        if(imageList.size()>0){
            //image_recycler.smoothScrollToPosition(0);
            viewImagesAdapter = new ViewImagesAdapter(this, imageList,dbData,"");
            image_recycler.setAdapter(viewImagesAdapter);
        }

    }
    public void getOnlineImageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("imageList", Api.Method.POST, UrlGenerator.getRoadListUrl(), encryptedJson(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void deleteOnlineImage(int position) {
        online_hab_code = String.valueOf(onlineImageList.get(position).getPmgsyHabcode());
        online_asset_id = onlineImageList.get(position).getAsseet_id();
        online_form_id = onlineImageList.get(position).getForm_id();
        serial_n_o = onlineImageList.get(position).getSl_no();
        try {
            new ApiService(this).makeJSONObjectRequest("deleteImage", Api.Method.POST, UrlGenerator.getRoadListUrl(), deleteEncryptedJson(), "not cache", this);
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
    public JSONObject deleteEncryptedJson() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), deleteNormalJson().toString());
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
    public JSONObject deleteNormalJson() throws JSONException {
        JSONArray image_details = new JSONArray();
        JSONArray image_list = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("form_id",online_form_id);
        jsonObject1.put("asset_id",online_asset_id);
        jsonObject1.put("hab_code",online_hab_code);
        image_list.put(jsonObject1);

        jsonObject.put("sl_no", serial_n_o);
        image_details.put(jsonObject);

        jsonObject1.put("image_details",image_details);
        image_list.put(jsonObject1);
        try {
            for(int k=0;k<image_list.length();k++){
                for(int l=k+1;l<image_list.length();l++){
                    if (image_list.get(k) == image_list.get(l)) {
                        image_list.remove(l);
                        l--;
                    }
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID,"agmt_habasset_photos_delete");
        dataSet.put("image_list",image_list);

        /*dataSet.put(AppConstant.KEY_SERVICE_ID, "agmt_habasset_photos_delete");
        dataSet.put("hab_code", online_hab_code);
        dataSet.put("form_id", form_id);
        dataSet.put("asset_id", asset_id);*/
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
            if ("deleteImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                    getOnlineImageList();
                }
                Log.d("deleteImage", "" + responseDecryptedBlockKey);
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
                //image_recycler.smoothScrollToPosition(0);
                viewImagesAdapter = new ViewImagesAdapter(FullImageActivity.this, onlineImageList,dbData,"Online");
                image_recycler.setAdapter(viewImagesAdapter);
            }
        }
    }

    public Bitmap StringToBitmap(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
