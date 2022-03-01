package com.nic.AGMTAssets.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.nic.AGMTAssets.Adapter.AgmtFormAdapter;
import com.nic.AGMTAssets.DataBase.DBHelper;
import com.nic.AGMTAssets.DataBase.dbData;
import com.nic.AGMTAssets.Model.RoadListValue;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;

import java.util.ArrayList;
import java.util.Collections;

public class AgmtTypeList extends AppCompatActivity {

    TextView village_name,designation_name,hab_name_text;
    RecyclerView habitation_recycler;
    public com.nic.AGMTAssets.DataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;


    ArrayList<RoadListValue> agmtFormList;
    AgmtFormAdapter agmtFormAdapter;

    String hab_code="";
    String hab_name="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agmt_type_list);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);
        village_name = findViewById(R.id.village_name);
        designation_name = findViewById(R.id.designation_name);
        hab_name_text = findViewById(R.id.hab_name_text);



        hab_code = String.valueOf(Integer.parseInt(getIntent().getStringExtra("hab_code")));
        hab_name = String.valueOf(getIntent().getStringExtra("hab_name"));

        designation_name.setText("Village Name: "+prefManager.getPvName());
        village_name.setText("District Name: "+prefManager.getDistrictName());
        hab_name_text.setText(hab_name);

        habitation_recycler = findViewById(R.id.habitation_recycler);
        habitation_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));

        agmtFormList = new ArrayList<>();

        getAgmtForms();

    }
    public void getAgmtForms() {
        dbData.open();
        agmtFormList.clear();

        agmtFormList.addAll(dbData.getAgmtForm());
        //Collections.sort(agmtFormList, (lhs, rhs) -> lhs.getForm_id().compareTo(rhs.getForm_id()));
        if(agmtFormList.size()>0){
            agmtFormAdapter = new AgmtFormAdapter(this, agmtFormList,dbData,hab_code);
            habitation_recycler.setAdapter(agmtFormAdapter);
        }

    }

    public void gotoViewFormDetails(int position){
        Intent intent = new Intent(AgmtTypeList.this,ViewFormDetails.class);
        intent.putExtra("hab_code",hab_code);
        intent.putExtra("hab_name",hab_name);
        intent.putExtra("form_name",agmtFormList.get(position).getForm_name_ta());
        intent.putExtra("form_id",agmtFormList.get(position).getForm_id());
        intent.putExtra("form_number",agmtFormList.get(position).getForm_number());
        intent.putExtra("min_no_of_photos",agmtFormList.get(position).getMin_no_of_photos());
        intent.putExtra("max_no_of_photos",agmtFormList.get(position).getMax_no_of_photos());
        intent.putExtra("type_of_photos",agmtFormList.get(position).getType_of_photos());
        startActivity(intent);
    }
}
