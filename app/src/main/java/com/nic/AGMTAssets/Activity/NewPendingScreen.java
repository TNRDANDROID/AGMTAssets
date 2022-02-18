package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.nic.AGMTAssets.Adapter.NewPendingScreenAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewPendingScreen extends AppCompatActivity implements Api.ServerResponseListener{

    RecyclerView pending_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    ImageView no_data_icon;

    ArrayList<RoadListValue> listValues;
    NewPendingScreenAdapter newPendingScreenAdapter;

    String asset_id="";
    String form_id="";
    String hab_code="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pending_screen);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);

        pending_recycler = findViewById(R.id.pending_recycler);
        no_data_icon = findViewById(R.id.no_data_icon);
        pending_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        no_data_icon.setVisibility(View.GONE);
        pending_recycler.setVisibility(View.GONE);
        getPendigList();
    }

    public void getPendigList() {
        listValues = new ArrayList<>();
        dbData.open();
        listValues.clear();

        //agmtFormList.addAll(dbData.getAGMTDisplayDataHabitationwise(hab_code,"no",""));
        listValues.addAll(dbData.getUniqueAgmtImages());
        //Collections.sort(agmtFormList, (lhs, rhs) -> lhs.getForm_id().compareTo(rhs.getForm_id()));
        if(listValues.size()>0){
            pending_recycler.setVisibility(View.VISIBLE);
            no_data_icon.setVisibility(View.GONE);
            newPendingScreenAdapter = new NewPendingScreenAdapter(this, listValues,dbData);
            pending_recycler.setAdapter(newPendingScreenAdapter);
            newPendingScreenAdapter.notifyDataSetChanged();
        }
        else {
            pending_recycler.setVisibility(View.GONE);
            no_data_icon.setVisibility(View.VISIBLE);
        }

    }

    public void SyncData(JSONObject jsonObject, String key_id_n, String form_id_,String hab_id_){
        asset_id =key_id_n;
        form_id =form_id_;
        hab_code =hab_id_;
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), jsonObject.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);
            Log.d("EncryptedDetails", "" + dataSet);
            Log.d("JSONDetails", "" + jsonObject);
            try {
                new ApiService(this).makeJSONObjectRequest("SaveDetails", Api.Method.POST, UrlGenerator.getRoadListUrl(), dataSet, "not cache", this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        catch (JSONException e){

        }


    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        String urlType = serverResponse.getApi();
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("SaveDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {

                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                        int sdsm = db.delete(DBHelper.AGMT_SAVE_IMAGE_TABLE, "form_id = ? and  asset_id =? and hab_code = ? ", new String[]{form_id,asset_id, hab_code});
                        getPendigList();



                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")){
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                }
                Log.d("SaveDetails", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
}
