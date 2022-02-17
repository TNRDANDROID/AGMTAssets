package com.nic.AGMTAssets.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nic.AGMTAssets.Activity.FormPrimaryDetails;
import com.nic.AGMTAssets.Activity.FullImageActivity;
import com.nic.AGMTAssets.Activity.NewPendingScreen;
import com.nic.AGMTAssets.Activity.ViewFormDetails;
import com.nic.AGMTAssets.Constant.AppConstant;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewPendingScreenAdapter extends RecyclerView.Adapter<NewPendingScreenAdapter.MyViewHolder>{

    private final com.nic.AGMTAssets.DataBase.dbData dbData;
    private Context context;
    private List<RoadListValue> habitationList;
    private PrefManager prefManager;

    public NewPendingScreenAdapter(Context context, List<RoadListValue> assetListValues, com.nic.AGMTAssets.DataBase.dbData dbData) {
        this.context = context;
        this.habitationList = assetListValues;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
    }

    @Override
    public NewPendingScreenAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_pending_recycler_items, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView habitation_name;
        RelativeLayout hab_rl;
        ImageView view_icon,upload_icon;


        public MyViewHolder(View itemView) {
            super(itemView);
            habitation_name = itemView.findViewById(R.id.hab_name);
            hab_rl = itemView.findViewById(R.id.hab_rl);
            view_icon = itemView.findViewById(R.id.view_icon);
            upload_icon = itemView.findViewById(R.id.upload_icon);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final NewPendingScreenAdapter.MyViewHolder holder, final int position) {

        holder.habitation_name.setText(habitationList.get(position).getDisp_value());
        //holder.habitation_name.setText("AssetId:"+habitationList.get(position).getAsseet_id()+"\n"+habitationList.get(position).getDisp_name()+habitationList.get(position).getDisp_value());
        holder.hab_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((ViewFormDetails)context).gotoViewFormDetails(position);
            }
        });
        holder.view_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra("hab_code",""+habitationList.get(position).getPmgsyHabcode());
                intent.putExtra("form_id",habitationList.get(position).getForm_id());
                intent.putExtra("asset_id",habitationList.get(position).getAsseet_id());
                intent.putExtra("type","Offline");
                context.startActivity(intent);
            }
        });

        holder.upload_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert(position,"save");
            }
        });

    }


    @Override
    public int getItemCount() {
        return habitationList.size();
    }

    public void save_and_delete_alert(int position,String save_delete){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText(context.getResources().getString(R.string.do_you_wnat_to_upload));
            }
            else if(save_delete.equals("delete")){
                text.setText(context.getResources().getString(R.string.do_you_wnat_to_delete));
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
                        uploadPending(position);
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
    public void uploadPending(int position) {
        dbData.open();
        JSONArray image_details = new JSONArray();
        JSONArray image_list = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jsonObject1 = null;
        String form_id = habitationList.get(position).getForm_id();
        String asset_id = habitationList.get(position).getAsseet_id();
        String hab_code = String.valueOf(habitationList.get(position).getPmgsyHabcode());
        ArrayList<RoadListValue> photos_details = new ArrayList<>();
        photos_details.addAll(dbData.getAgmtImages(form_id,asset_id,hab_code));
        for(int j=0;j<1;j++) {
            jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("form_id",photos_details.get(j).getForm_id());
                jsonObject1.put("asset_id",photos_details.get(j).getAsseet_id());
                jsonObject1.put("hab_code",photos_details.get(j).getPmgsyHabcode());
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
            ((NewPendingScreen)context).SyncData(jsonObject,asset_id,form_id,hab_code);

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


}


