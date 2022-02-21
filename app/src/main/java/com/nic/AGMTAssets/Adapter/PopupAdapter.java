package com.nic.AGMTAssets.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    ArrayList<RoadListValue> images;
    Context context;

    public PopupAdapter(LayoutInflater inflater ,ArrayList<RoadListValue> images,Context context ) {
        this.inflater=inflater;
        this.images=images;
        this.context=context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup, null);
        }
        Bitmap bitmap = null;
        TextView tv=(TextView)popup.findViewById(R.id.title);
        ImageView image=(ImageView) popup.findViewById(R.id.icon);

        //tv.setText(marker.getTitle());
        tv=(TextView)popup.findViewById(R.id.snippet);
        //tv.setText(marker.getSnippet());
        tv.setText(getCompleteAddressString(marker.getPosition().latitude,marker.getPosition().longitude));
        for (int i=0;i<images.size();i++){
            if(Double.parseDouble(images.get(i).getLatitude())==(marker.getPosition().latitude)&&
                    Double.parseDouble(images.get(i).getLongitude())==(marker.getPosition().longitude)){
                bitmap = images.get(i).getImage();
            }
        }
        if(bitmap!=null){
            image.setImageBitmap(bitmap);
        }
        else {
            image.setImageResource(R.drawable.background);
        }


        return(popup);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                //Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

}
