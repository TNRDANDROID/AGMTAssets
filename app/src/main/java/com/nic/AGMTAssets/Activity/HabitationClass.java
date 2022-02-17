package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nic.AGMTAssets.Adapter.HabitationAdapter;
import com.nic.AGMTAssets.Api.Api;
import com.nic.AGMTAssets.Api.ApiService;
import com.nic.AGMTAssets.Api.ServerResponse;
import com.nic.AGMTAssets.Constant.AppConstant;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Dialog.MyDialog;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Utils.UrlGenerator;
import com.nic.AGMTAssets.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import me.texy.treeview.TreeNode;

public class HabitationClass extends AppCompatActivity implements Api.ServerResponseListener,MyDialog.myOnClickListener{

    TextView village_name,designation_name;
    RelativeLayout sync_data_rl;
    RecyclerView habitation_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;

    ArrayList<RoadListValue> HabitationList;
    ArrayList<RoadListValue> HabitationListOrder;
    HabitationAdapter habitationAdapter;
    ImageView log_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitation_class);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);
        village_name = findViewById(R.id.village_name);
        designation_name = findViewById(R.id.designation_name);
        sync_data_rl = findViewById(R.id.sync_data_rl);
        log_out = findViewById(R.id.log_out);

        sync_data_rl.setVisibility(View.GONE);
        designation_name.setText("Village Name: "+prefManager.getPvName());
        village_name.setText("District Name: "+prefManager.getDistrictName());

        habitation_recycler = findViewById(R.id.habitation_recycler);
        habitation_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        HabitationList= new ArrayList<>();
        HabitationListOrder= new ArrayList<>();
        if(Utils.isOnline()){
            getHabList();
            getAgmtList();
            getagmt_list_of_asset();
        }
        else {
            habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
        }

        syncButtonVisibility();

        sync_data_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPendingScreen();
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLogout();
            }
        });

    }

    public void getHabList() {
        try {
            new ApiService(this).makeJSONObjectRequest("HabitationList", Api.Method.POST, UrlGenerator.getServicesListUrl(), habitationListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getagmt_list_of_asset() {
        try {
            new ApiService(this).makeJSONObjectRequest("agmt_list_of_asset", Api.Method.POST, UrlGenerator.getRoadListUrl(), agmt_list_of_asset(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getAgmtList() {
        try {
            new ApiService(this).makeJSONObjectRequest("AgmtList", Api.Method.POST, UrlGenerator.getRoadListUrl(), agmtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject habitationListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.HabitationListDistrictBlockVillageWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("HabitationList", "" + authKey);
        return dataSet;
    }
    public JSONObject agmtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.agmtListJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("agmtListJsonParams", "" + dataSet);
        return dataSet;
    }
    public JSONObject agmt_list_of_asset() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.agmt_list_of_asset(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("agmtListJsonParams", "" + dataSet);
        return dataSet;
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            /////////////// ********** Now Not Need/////////////


            ///////////// *********************///////////////

            if ("HabitationList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertHabTask().execute(jsonObject);
                }
                Log.d("HabitationList", "" + responseDecryptedBlockKey);
            }
            if ("AgmtList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertAGMTFormTask().execute(jsonObject);
                }
                Log.d("AgmtList", "" + responseDecryptedBlockKey);
            }
            if ("agmt_list_of_asset".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    //new InsertAGMTFormTask().execute(jsonObject);
                    new InsertAGMTFormDisplayAndCommonDataTask().execute(jsonObject);
                }
                Log.d("agmt_list_of_asset", "" + responseDecryptedBlockKey);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
    public void habitationFilterSpinner(String dcode,String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);
        HabitationList.clear();
        HabitationListOrder.clear();

        HabitationListOrder.addAll(dbData.getVillageAll_Habitation(dcode,bcode,pvcode));
       /* if (HABList.getCount() > 0) {
            if (HABList.moveToFirst()) {
                do {
                    RoadListValue habList = new RoadListValue();
                    int districtCode = Integer.parseInt(HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    int blockCode = Integer.parseInt(HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    int pvCode = Integer.parseInt(HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    int habCode = Integer.parseInt(HABList.getString(HABList.getColumnIndexOrThrow("habitation_code")));
                    String habName = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME));
                    String habName_ta = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME_TA));

                    habList.setPmgsyDcode(districtCode);
                    habList.setPmgsyBcode(blockCode);
                    habList.setPmgsyPvcode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setPmgsyHabName(habName);
                    habList.setPmgsyHabNameTa(habName_ta);

                    HabitationListOrder.add(habList);
                } while (HABList.moveToNext());
            }
            Log.d("Habitationspinnersize", "" + HabitationListOrder.size());

        }
*/
       Collections.sort(HabitationListOrder, (lhs, rhs) -> lhs.getPmgsyHabNameTa().compareTo(rhs.getPmgsyHabNameTa()));
        if(HabitationListOrder.size()>0){
            habitationAdapter = new HabitationAdapter(this,HabitationListOrder,dbData);
            habitation_recycler.setAdapter(habitationAdapter);
        }

        }
    public class InsertHabTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();

            ArrayList<RoadListValue> hablist_count = dbData.getAll_Habitation(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (hablist_count.size() >= 0) {
                db.execSQL("delete from "+ DBHelper.HABITATION_TABLE);
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RoadListValue habListValue = new RoadListValue();
                        try {
                            habListValue.setPmgsyDcode(Integer.valueOf(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE)));
                            habListValue.setPmgsyBcode(Integer.valueOf(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE)));
                            habListValue.setPmgsyPvcode(Integer.valueOf(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE)));
                            habListValue.setPmgsyHabcode(Integer.valueOf(jsonArray.getJSONObject(i).getString(AppConstant.HABB_CODE)));
                            habListValue.setPmgsyHabName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));
                            habListValue.setPmgsyHabNameTa(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME_TA));

                            dbData.insertHabitation(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            habitationFilterSpinner(prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode());
        }
    }
    public class InsertAGMTFormTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();

            ArrayList<RoadListValue> hablist_count = dbData.getAgmtForm();
            if (hablist_count.size() >= 0) {
                db.execSQL("delete from "+ DBHelper.AGMT_FORM_TABLE);
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RoadListValue habListValue = new RoadListValue();
                        try {
                            habListValue.setForm_id((jsonArray.getJSONObject(i).getString("form_id")));
                            habListValue.setForm_number((jsonArray.getJSONObject(i).getString("form_number")));
                            habListValue.setForm_name_ta((jsonArray.getJSONObject(i).getString("form_name_ta")));
                            habListValue.setNo_of_photos((jsonArray.getJSONObject(i).getString("no_of_photos")));
                            habListValue.setType_of_photos((jsonArray.getJSONObject(i).getString("type_of_photos")));

                            dbData.insertAGMTForm(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class InsertAGMTFormDisplayAndCommonDataTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            StringBuilder data = new StringBuilder();
            String text_value="";
            ArrayList<RoadListValue> hablist_count = dbData.getAGMTDisplayDataHabitationwise("","all","");
            if (hablist_count.size() >= 0) {
                db.execSQL("delete from "+ DBHelper.AGMT_FORM_DISPLAY_COMMON_DATA_TABLE);
                db.execSQL("delete from "+ DBHelper.AGMT_FORM_DISPLAY_DATA_TABLE);
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RoadListValue habListValue = new RoadListValue();
                        String value ="";
                        try {
                            habListValue.setForm_id((jsonArray.getJSONObject(i).getString("form_id")));
                            habListValue.setAsseet_id((jsonArray.getJSONObject(i).getString("asset_id")));
                            habListValue.setState_code((jsonArray.getJSONObject(i).getString("state_code")));
                            habListValue.setPmgsyBcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("bcode"))));
                            habListValue.setPmgsyPvcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("pvcode"))));
                            habListValue.setPmgsyHabcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("hab_code"))));
                            JSONArray structureArray = jsonArray.getJSONObject(i).getJSONArray("display_data");




                            for(int j=0;j<structureArray.length();j++){
                                RoadListValue displayData = new RoadListValue();
                                displayData.setForm_id((jsonArray.getJSONObject(i).getString("form_id")));
                                displayData.setAsseet_id((jsonArray.getJSONObject(i).getString("asset_id")));
                                displayData.setState_code((jsonArray.getJSONObject(i).getString("state_code")));
                                displayData.setPmgsyBcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("bcode"))));
                                displayData.setPmgsyPvcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("pvcode"))));
                                displayData.setPmgsyHabcode(Integer.valueOf((jsonArray.getJSONObject(i).getString("hab_code"))));

                                displayData.setDisp_id(((structureArray.getJSONObject(j).getString("disp_id"))));
                                displayData.setColumn_type(((structureArray.getJSONObject(j).getString("column_type"))));
                                displayData.setDisp_name(((structureArray.getJSONObject(j).getString("disp_name"))));
                                displayData.setDisp_value(((structureArray.getJSONObject(j).getString("disp_value"))));

                                if(structureArray.getJSONObject(j).getString("column_type").equals("primary")) {
                                    value = value + structureArray.getJSONObject(j).getString("disp_name") +" : "
                                            + structureArray.getJSONObject(j).getString("disp_value")+"\n";
                                }

                                dbData.insertAGMTFormDisplayData(displayData,structureArray);

                            }
                            habListValue.setText_value(value);
                            dbData.insertAGMTFormCommonData(habListValue);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }

    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<RoadListValue> assetsCount = dbData.getSavedAsset();
        ArrayList<RoadListValue> trackCount = dbData.getSavedTrack();
        ArrayList<RoadListValue> habitationCount = dbData.getSavedHabitation("0");
        ArrayList<RoadListValue> bridgesCount = dbData.getAllBridges("0","upload");
        ArrayList<RoadListValue> image_count = dbData.getUniqueAgmtImages();


        if(image_count.size()>0){
            sync_data_rl.setVisibility(View.VISIBLE);
        }
        else {
            sync_data_rl.setVisibility(View.GONE);
        }

    }

    public void gotoPendingScreen(){
        Intent intent = new Intent(HabitationClass.this,NewPendingScreen.class);
        startActivity(intent);

    }

    private void closeApplication() {
        new MyDialog(HabitationClass.this).exitDialog(HabitationClass.this, "Are you sure you want to Logout?", "Logout");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        /*if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        }
        else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }*/
        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXIT", false);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void clickLogout(){
        dbData.open();
       /* ArrayList<RoadListValue> assetsCount = dbData.getSavedAsset();
        ArrayList<RoadListValue> trackCount = dbData.getSavedTrack();
        ArrayList<RoadListValue> habitationCount = dbData.getSavedHabitation("0");
        ArrayList<RoadListValue> bridgesCount = dbData.getAllBridges("0","upload");*/
        ArrayList<RoadListValue> image_count = dbData.getUniqueAgmtImages();

        if(!Utils.isOnline()) {
            Utils.showAlert(this,"Logging out while offline may leads to loss of data!");
        }else {
            if (!(image_count.size() > 0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }
}


