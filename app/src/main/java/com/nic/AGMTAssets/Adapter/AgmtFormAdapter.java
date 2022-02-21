package com.nic.AGMTAssets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nic.AGMTAssets.Activity.AgmtTypeList;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import java.util.List;

public class AgmtFormAdapter extends RecyclerView.Adapter<AgmtFormAdapter.MyViewHolder>{

    private final com.nic.AGMTAssets.DataBase.dbData dbData;
    private Context context;
    private List<RoadListValue> habitationList;
    private PrefManager prefManager;

    public AgmtFormAdapter(Context context, List<RoadListValue> assetListValues, com.nic.AGMTAssets.DataBase.dbData dbData) {
        this.context = context;
        this.habitationList = assetListValues;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
    }

    @Override
    public AgmtFormAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.habitation_recyler_item_design, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView habitation_name;
        RelativeLayout hab_rl;
        RelativeLayout hab_rl2;


        public MyViewHolder(View itemView) {
            super(itemView);
            habitation_name = itemView.findViewById(R.id.hab_name);
            hab_rl = itemView.findViewById(R.id.hab_rl);
            hab_rl2 = itemView.findViewById(R.id.hab_rl2);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final AgmtFormAdapter.MyViewHolder holder, final int position) {

        holder.habitation_name.setText(habitationList.get(position).getForm_name_ta());
        if(position % 2==0){
            holder.hab_rl2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.new_gradient_bg_2));
        }
        else {
            holder.hab_rl2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.new_gradient_bg_1));
        }
        holder.hab_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AgmtTypeList)context).gotoViewFormDetails(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return habitationList.size();
    }

}

