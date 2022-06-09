package com.nic.AGMTAssets.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.AGMTAssets.Activity.AgmtTypeList;
import com.nic.AGMTAssets.Activity.FullImageActivity;
import com.nic.AGMTAssets.ImageZoom.ImageMatrixTouchHandler;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Utils.Utils;

import java.util.List;

public class ViewImagesAdapter extends RecyclerView.Adapter<ViewImagesAdapter.MyViewHolder>{

    private final com.nic.AGMTAssets.DataBase.dbData dbData;
    private Context context;
    private List<RoadListValue> habitationList;
    private PrefManager prefManager;
    String type;

    public ViewImagesAdapter(Context context, List<RoadListValue> assetListValues, com.nic.AGMTAssets.DataBase.dbData dbData,String type) {
        this.context = context;
        this.habitationList = assetListValues;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
        this.type=type;
    }

    @Override
    public ViewImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumnail_view, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image,delete_icon;


        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.thumbnail);
            delete_icon = itemView.findViewById(R.id.delete_icon);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewImagesAdapter.MyViewHolder holder, final int position) {

        holder.image.setImageBitmap(habitationList.get(position).getImage());

        if(type.equals("Online")){
            holder.delete_icon.setVisibility(View.VISIBLE);
        }
        else {
            holder.delete_icon.setVisibility(View.GONE);
        }

        holder.delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(habitationList.size()!=1) {
                    save_and_delete_alert(position, "delete");
                }
                else {
                    Utils.showAlert(context,"You Can't delete all the photos" );
                }

            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandedImage(holder.image.getDrawable());
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
                        //uploadPending(position);
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        //deletePending(position);
                        ((FullImageActivity)context).deleteOnlineImage(position);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ExpandedImage(Drawable profile) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View ImagePopupLayout = inflater.inflate(R.layout.image_fullscreen_preview, null);

            ImageView zoomImage = (ImageView) ImagePopupLayout.findViewById(R.id.image_preview);
            zoomImage.setImageDrawable(profile);

            ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(context);
            zoomImage.setOnTouchListener(imageMatrixTouchHandler);
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setView(ImagePopupLayout);

            final AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_zoomInOut;
            alert.show();
            alert.getWindow().setBackgroundDrawableResource(R.color.full_transparent);
            alert.setCanceledOnTouchOutside(true);

            zoomImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


