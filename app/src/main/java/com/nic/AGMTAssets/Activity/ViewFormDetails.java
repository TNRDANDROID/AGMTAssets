package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nic.AGMTAssets.Adapter.AgmtFormDetailsAdapter;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import java.util.ArrayList;

public class ViewFormDetails extends AppCompatActivity {
    TextView village_name,designation_name,hab_name_text,form_name_text;
    RecyclerView habitation_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    ImageView no_data_gif;

    ArrayList<RoadListValue> agmtFormList;
    AgmtFormDetailsAdapter agmtFormDetailsAdapter;

    String hab_code="";
    String hab_name="";
    String form_name="";
    String form_number="";
    String form_id="";
    String type_of_photos="";
    String min_no_of_photos ="";
    String max_no_of_photos ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_form_details);

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
        no_data_gif = findViewById(R.id.no_data_gif);


        hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
        hab_name = String.valueOf(getIntent().getStringExtra("hab_name"));
        form_id = (getIntent().getStringExtra("form_id"));
        form_number = (getIntent().getStringExtra("form_number"));
        form_name = (getIntent().getStringExtra("form_name"));
        min_no_of_photos = (getIntent().getStringExtra("min_no_of_photos"));
        max_no_of_photos = (getIntent().getStringExtra("max_no_of_photos"));
        type_of_photos = (getIntent().getStringExtra("type_of_photos"));

        designation_name.setText("Village Name: "+prefManager.getPvName());
        village_name.setText("District Name: "+prefManager.getDistrictName());
        hab_name_text.setText("Habitation: "+hab_name);
        form_name_text.setText(form_name);

        habitation_recycler = findViewById(R.id.habitation_recycler);
        habitation_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        /*DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        habitation_recycler.addItemDecoration(itemDecoration);*/
        agmtFormList = new ArrayList<>();
        getAgmtForms();
    }

    public void getAgmtForms() {
        dbData.open();
        agmtFormList.clear();

        //agmtFormList.addAll(dbData.getAGMTDisplayDataHabitationwise(hab_code,"no",""));
        agmtFormList.addAll(dbData.getAGMTCommonDataHabitationwise(hab_code,form_id));
        //Collections.sort(agmtFormList, (lhs, rhs) -> lhs.getForm_id().compareTo(rhs.getForm_id()));
        if(agmtFormList.size()>0){
            no_data_gif.setVisibility(View.GONE);
            habitation_recycler.setVisibility(View.VISIBLE);
            agmtFormDetailsAdapter = new AgmtFormDetailsAdapter(this, agmtFormList,dbData);
            habitation_recycler.setAdapter(agmtFormDetailsAdapter);
        }
        else {
            no_data_gif.setVisibility(View.VISIBLE);
            habitation_recycler.setVisibility(View.GONE);
        }

    }

    public void gotoViewFormDetails(int position){
        Intent intent = new Intent(ViewFormDetails.this,FormPrimaryDetails.class);
        intent.putExtra("hab_code",hab_code);
        intent.putExtra("hab_name",hab_name);
        intent.putExtra("form_name",form_name);
        intent.putExtra("form_id",form_id);
        intent.putExtra("form_number",form_number);
        intent.putExtra("min_no_of_photos", min_no_of_photos);
        intent.putExtra("max_no_of_photos", max_no_of_photos);
        intent.putExtra("type_of_photos",type_of_photos);
        intent.putExtra("asset_id",agmtFormList.get(position).getAsseet_id());
        startActivity(intent);
    }


}
