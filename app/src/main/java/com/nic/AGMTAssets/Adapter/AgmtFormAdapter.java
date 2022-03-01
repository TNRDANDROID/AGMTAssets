package com.nic.AGMTAssets.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.AGMTAssets.Activity.AgmtTypeList;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Support.MyCustomTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgmtFormAdapter extends RecyclerView.Adapter<AgmtFormAdapter.MyViewHolder>{

    private final com.nic.AGMTAssets.DataBase.dbData dbData;
    private Context context;
    private List<RoadListValue> habitationList;
    private PrefManager prefManager;
    String hab_code;

    public AgmtFormAdapter(Context context, List<RoadListValue> assetListValues, com.nic.AGMTAssets.DataBase.dbData dbData,String hab_code) {
        this.context = context;
        this.habitationList = assetListValues;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
        this.hab_code=hab_code;
    }

    @Override
    public AgmtFormAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_agmt_form_item_list_adapter_design, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout road_layout;
        RelativeLayout top;
        MyCustomTextView form_ID;
        MyCustomTextView form_number;
        MyCustomTextView form_name;
        ImageView imageView;
        CardView district_card;

        public MyViewHolder(View itemView) {
            super(itemView);
            road_layout = itemView.findViewById(R.id.road_layout);
            top = itemView.findViewById(R.id.top);
            form_ID = itemView.findViewById(R.id.road_code);
            form_number = itemView.findViewById(R.id.road_village_name);
            form_name = itemView.findViewById(R.id.road_name);
            imageView = itemView.findViewById(R.id.imageView);
            district_card = itemView.findViewById(R.id.district_card);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final AgmtFormAdapter.MyViewHolder holder, final int position) {
        int count=0;
        holder.imageView.setVisibility(View.GONE);
        holder.form_name.setText(habitationList.get(position).getForm_name_ta());
        /*holder.form_ID.setText(habitationList.get(position).getForm_id());
        holder.form_number.setText(habitationList.get(position).getForm_number());*/
        dbData.open();
        holder.form_ID.setText("Total "+"\n"+dbData.getAGMTCommonDataHabitationwise(hab_code,habitationList.get(position).getForm_id()).size());
        ArrayList<RoadListValue> phottakencount = new ArrayList<>();
        phottakencount.addAll(dbData.getAGMTCommonDataHabitationwise(hab_code,habitationList.get(position).getForm_id()));
        for (int i =0;i<phottakencount.size();i++){
            if(phottakencount.get(i).getPhoto_taken().equals("Y")){
                count=count+1;
            }
        }
        holder.form_number.setText("Completed "+"\n"+count);
        int[] colors = new int[2];
        colors[0] = getRandomColor();
        colors[1] = getRandomColor();


        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);

        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(300f);
        gd.setCornerRadius(50f);
        holder.district_card.setBackground(gd);

        holder.top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AgmtTypeList)context).gotoViewFormDetails(position);
            }
        });
        /*if(position % 2==0){
            holder.road_layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.img_running));
        }
        else {
            holder.road_layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.img_golf));
        }*/

    }


    @Override
    public int getItemCount() {
        return habitationList.size();
    }
    public static int getRandomColor(){
        Random rnd = new Random();
        //return Color.argb(100, rnd.nextInt(120), rnd.nextInt(56), rnd.nextInt(60));
        //return Color.argb(100, rnd.nextInt(250), rnd.nextInt(10), rnd.nextInt(100));
        return Color.argb(120, rnd.nextInt(120), rnd.nextInt(50), rnd.nextInt(60));
        //return Color.argb(80, rnd.nextInt(120), rnd.nextInt(50), rnd.nextInt(60));
    }

}

