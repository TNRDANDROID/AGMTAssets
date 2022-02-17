package com.nic.AGMTAssets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nic.AGMTAssets.Activity.AgmtTypeList;
import com.nic.AGMTAssets.Activity.ViewFormDetails;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AgmtFormDetailsAdapter extends RecyclerView.Adapter<AgmtFormDetailsAdapter.MyViewHolder>{

    private final com.nic.AGMTAssets.DataBase.dbData dbData;
    private Context context;
    private List<RoadListValue> habitationList;
    private PrefManager prefManager;

    public AgmtFormDetailsAdapter(Context context, List<RoadListValue> assetListValues, com.nic.AGMTAssets.DataBase.dbData dbData) {
        this.context = context;
        this.habitationList = assetListValues;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
    }

    @Override
    public AgmtFormDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_details_item_design, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView habitation_name;
        RelativeLayout hab_rl;


        public MyViewHolder(View itemView) {
            super(itemView);
            habitation_name = itemView.findViewById(R.id.hab_name);
            hab_rl = itemView.findViewById(R.id.hab_rl);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final AgmtFormDetailsAdapter.MyViewHolder holder, final int position) {

       holder.habitation_name.setText(habitationList.get(position).getText_value());
       //holder.habitation_name.setText("AssetId:"+habitationList.get(position).getAsseet_id()+"\n"+habitationList.get(position).getDisp_name()+habitationList.get(position).getDisp_value());
        holder.hab_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewFormDetails)context).gotoViewFormDetails(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return habitationList.size();
    }

}

