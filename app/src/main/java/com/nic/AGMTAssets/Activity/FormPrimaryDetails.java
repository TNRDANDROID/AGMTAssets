package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nic.AGMTAssets.Adapter.AgmtFormDetailsAdapter;
import com.nic.AGMTAssets.Adapter.AgmtFormFullDetails;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import java.util.ArrayList;

public class FormPrimaryDetails extends AppCompatActivity {
    TextView village_name,designation_name,hab_name_text,form_name_text;
    RecyclerView habitation_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    Button tak_photo_btn,view_photo;


    ArrayList<RoadListValue> agmtFormList;
    AgmtFormFullDetails agmtFormDetailsAdapter;

    String hab_code="";
    String hab_name="";
    String form_name="";
    String form_number="";
    String form_id="";
    String type_of_photos="";
    String no_of_photos="";
    String asset_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_primary_details);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);
        village_name = findViewById(R.id.village_name);
        designation_name = findViewById(R.id.designation_name);
        hab_name_text = findViewById(R.id.text_view_1);
        form_name_text = findViewById(R.id.form_name);
        tak_photo_btn = findViewById(R.id.take_photo);
        view_photo = findViewById(R.id.view_photo);



        hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
        hab_name = String.valueOf(getIntent().getStringExtra("hab_name"));
        form_id = (getIntent().getStringExtra("form_id"));
        form_number = (getIntent().getStringExtra("form_number"));
        form_name = (getIntent().getStringExtra("form_name"));
        no_of_photos = (getIntent().getStringExtra("no_of_photos"));
        type_of_photos = (getIntent().getStringExtra("type_of_photos"));
        asset_id = (getIntent().getStringExtra("asset_id"));

        designation_name.setText("Village Name: "+prefManager.getPvName());
        village_name.setText("District Name: "+prefManager.getDistrictName());
        hab_name_text.setText("Habitation: "+hab_name);
        form_name_text.setText(form_name);

        habitation_recycler = findViewById(R.id.habitation_recycler);
        habitation_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        habitation_recycler.addItemDecoration(itemDecoration);

        agmtFormList = new ArrayList<>();

        tak_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTakePhoto();
            }
        });
        view_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewPhoto();
            }
        });

        getAgmtForms();
    }

    public void getAgmtForms() {
        dbData.open();
        agmtFormList.clear();

        agmtFormList.addAll(dbData.getAGMTDisplayDataHabitationwise(hab_code,"",asset_id));
        //Collections.sort(agmtFormList, (lhs, rhs) -> lhs.getForm_id().compareTo(rhs.getForm_id()));
        if(agmtFormList.size()>0){
            agmtFormDetailsAdapter = new AgmtFormFullDetails(this, agmtFormList,dbData);
            habitation_recycler.setAdapter(agmtFormDetailsAdapter);
        }

    }

    public void gotoTakePhoto(){
        Intent intent = new Intent(FormPrimaryDetails.this,TakePhotoScreen.class);
        intent.putExtra("hab_code",hab_code);
        intent.putExtra("hab_name",hab_name);
        intent.putExtra("form_name",form_name);
        intent.putExtra("form_id",form_id);
        intent.putExtra("form_number",form_number);
        intent.putExtra("no_of_photos",no_of_photos);
        intent.putExtra("type_of_photos",type_of_photos);
        intent.putExtra("asset_id",asset_id);
        startActivity(intent);
    }
    public void gotoViewPhoto(){
        Intent intent = new Intent(FormPrimaryDetails.this,FullImageActivity.class);
        intent.putExtra("hab_code",hab_code);
        intent.putExtra("form_id",form_id);
        intent.putExtra("asset_id",asset_id);
        intent.putExtra("type","Online");
        startActivity(intent);
    }

}
