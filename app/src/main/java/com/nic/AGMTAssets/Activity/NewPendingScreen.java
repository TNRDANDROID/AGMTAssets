package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nic.AGMTAssets.Adapter.NewPendingScreenAdapter;
import com.nic.AGMTAssets.Api.Api;
import com.nic.AGMTAssets.Api.ApiService;
import com.nic.AGMTAssets.Api.ServerResponse;
import com.nic.AGMTAssets.Constant.AppConstant;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Interface.MultipleSelection;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Utils.UrlGenerator;
import com.nic.AGMTAssets.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class NewPendingScreen extends AppCompatActivity implements Api.ServerResponseListener, MultipleSelection {

    RecyclerView pending_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    ImageView no_data_icon;
    TextView village_name,district_name;

    ArrayList<RoadListValue> listValues;
    NewPendingScreenAdapter newPendingScreenAdapter;

    String asset_id="";
    String form_id="";
    String hab_code="";

    Button upload_btn;
    ArrayList<RoadListValue> clickedList;
    String onBackPressFlag="";


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
        village_name = findViewById(R.id.village_name);
        district_name = findViewById(R.id.designation_name);
        pending_recycler = findViewById(R.id.pending_recycler);
        no_data_icon = findViewById(R.id.no_data_icon);
        upload_btn = findViewById(R.id.upload_btn);
        pending_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        pending_recycler.addItemDecoration(itemDecoration);
        no_data_icon.setVisibility(View.GONE);
        pending_recycler.setVisibility(View.GONE);
        upload_btn.setVisibility(View.GONE);
        village_name.setText("Village Name: " + prefManager.getPvName());
        district_name.setText("District Name: " + prefManager.getDistrictName());
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert(clickedList,"save");
            }
        });
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
            newPendingScreenAdapter = new NewPendingScreenAdapter(this, listValues,dbData,this);
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
                    onBackPressFlag="yes";
                    for(int i=0;i<clickedList.size();i++){
                        if(clickedList.get(i).getFlag().equalsIgnoreCase("yes")){
                            String form_id = clickedList.get(i).getForm_id();
                            String asset_id = clickedList.get(i).getAsseet_id();
                            String hab_code = String.valueOf(clickedList.get(i).getPmgsyHabcode());
                            db.delete(DBHelper.AGMT_SAVE_IMAGE_TABLE, "form_id = ? and  asset_id =? and hab_code = ? ", new String[]{form_id,asset_id, hab_code});
                        }
                    }
                        //int sdsm = db.delete(DBHelper.AGMT_SAVE_IMAGE_TABLE, "form_id = ? and  asset_id =? and hab_code = ? ", new String[]{form_id,asset_id, hab_code});
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

    @Override
    public void onClickedItems(ArrayList<RoadListValue> roadListValues) {
            clickedList = new ArrayList<>();
            for(int i=0;i<roadListValues.size();i++){
                if(roadListValues.get(i).getFlag().equalsIgnoreCase("yes")){
                    RoadListValue roadListValue_object= new RoadListValue();
                    roadListValue_object.setForm_id(roadListValues.get(i).getForm_id());
                    roadListValue_object.setAsseet_id(roadListValues.get(i).getAsseet_id());
                    roadListValue_object.setPmgsyHabcode(Integer.valueOf(roadListValues.get(i).getPmgsyHabcode()));
                    roadListValue_object.setDisp_value(roadListValues.get(i).getDisp_value());
                    roadListValue_object.setFlag(roadListValues.get(i).getFlag());

                    clickedList.add(roadListValue_object);
                }
            }
            if(clickedList.size()>0){
                upload_btn.setVisibility(View.VISIBLE);
            }
            else {
                upload_btn.setVisibility(View.GONE);
            }

    }



    public void save_and_delete_alert(ArrayList<RoadListValue> list,String save_delete){
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText(getResources().getString(R.string.do_you_wnat_to_upload));
            }
            else if(save_delete.equals("delete")){
                text.setText(getResources().getString(R.string.do_you_wnat_to_delete));
            }

            Button yesButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button noButton = (Button) dialog.findViewById(R.id.btn_cancel);
            noButton.setVisibility(View.VISIBLE);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(save_delete.equals("save")) {
                        upload_btn.setVisibility(View.GONE);
                        uploadPending(list);
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        //deletePending(position);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void uploadPending(ArrayList<RoadListValue> list) {
        dbData.open();
        JSONArray image_details = new JSONArray();
        JSONArray image_list = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jsonObject1 = null;


        for(int j=0;j<list.size();j++) {
            jsonObject1 = new JSONObject();
            String form_id = list.get(j).getForm_id();
            String asset_id = list.get(j).getAsseet_id();
            String hab_code = String.valueOf(list.get(j).getPmgsyHabcode());
            ArrayList<RoadListValue> photos_details = new ArrayList<>();
            photos_details.addAll(dbData.getAgmtImages(form_id,asset_id,hab_code));
            try {
                jsonObject1.put("form_id",list.get(j).getForm_id());
                jsonObject1.put("asset_id",list.get(j).getAsseet_id());
                jsonObject1.put("hab_code",list.get(j).getPmgsyHabcode());
                image_list.put(jsonObject1);
            }catch (Exception e){
                e.printStackTrace();
            }

            for (int i = 0; i < photos_details.size(); i++) {
                JSONObject jsonObject = new JSONObject();


                try {
                    jsonObject.put("sl_no", photos_details.get(i).getSl_no());
                    jsonObject.put("latitude", photos_details.get(i).getLatitude());
                    jsonObject.put("longitude", photos_details.get(i).getLongitude());
                    jsonObject.put("image", BitMapToString(photos_details.get(i).getImage()));
                    //jsonObject.put("photograph_remark",photos_details.get(i).getPhotograph_remark());
                    image_details.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            try {
                jsonObject1.put("image_details",image_details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            image_list.put(jsonObject1);
        }

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




        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstant.KEY_SERVICE_ID,"agmt_habasset_photos_save");
            jsonObject.put("image_list",image_list);
            Log.d("JSON",""+jsonObject);
            SyncData(jsonObject,asset_id,form_id,hab_code);

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    public void onBackPressed() {
        if(onBackPressFlag.equals("yes")){
            Intent intent = new Intent(NewPendingScreen.this,HabitationClass.class);
            intent.putExtra("Home","Login");
            startActivity(intent);
            finish();
        }
        else {
        super.onBackPressed();}
    }

}
