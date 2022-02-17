package com.nic.AGMTAssets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import org.json.JSONArray;

import java.util.List;

public class AgmtFormFullDetails extends RecyclerView.Adapter<AgmtFormFullDetails.MyViewHolder>{

    private final com.nic.AGMTAssets.DataBase.dbData dbData;
    private Context context;
    private List<RoadListValue> habitationList;
    private PrefManager prefManager;

    public AgmtFormFullDetails(Context context, List<RoadListValue> assetListValues, com.nic.AGMTAssets.DataBase.dbData dbData) {
        this.context = context;
        this.habitationList = assetListValues;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
    }

    @Override
    public AgmtFormFullDetails.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_full_details_items, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView disp_name,disp_value;
        RelativeLayout hab_rl;


        public MyViewHolder(View itemView) {
            super(itemView);
            disp_name = itemView.findViewById(R.id.disp_name);
            disp_value = itemView.findViewById(R.id.display_value);
            hab_rl = itemView.findViewById(R.id.hab_rl);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final AgmtFormFullDetails.MyViewHolder holder, final int position) {

        holder.disp_name.setText(habitationList.get(position).getDisp_name());
        holder.disp_value.setText(habitationList.get(position).getDisp_value());
        holder.hab_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((AgmtTypeList)context).gotoViewFormDetails(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return habitationList.size();
    }

}


