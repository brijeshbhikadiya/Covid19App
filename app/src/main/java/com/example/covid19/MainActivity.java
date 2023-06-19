package com.example.covid19;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecoverd,mtodayrecoverd,mdeaths,mtodaydeaths;

    String country;
    TextView mfilter;
    Spinner spinner;

    String[] types={"cases","deaths","recovered","active"};
    private List<ModelClass> mmodealclasslist;
    private List<ModelClass> mmodelClassList2;

    PieChart mpiechart;
    private RecyclerView recyclerView;
    com.example.covid19.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //getSupportActionBar().hide();
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }

        countryCodePicker=findViewById(R.id.ccp);
        mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecase);
        mdeaths=findViewById(R.id.deathcase);
        mtodaydeaths=findViewById(R.id.todayDeaths);
        mrecoverd=findViewById(R.id.recoverdcase);
        mtodayrecoverd=findViewById(R.id.todayrecoverd);
        mtotal=findViewById(R.id.totalcase);
        mtodaytotal=findViewById(R.id.todaytotal);

        mpiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recyclerview);

         mmodealclasslist=new ArrayList<>();
        mmodelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0,true);

        ApiUtilities.getAPIInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
               mmodelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter=new Adapter(getApplicationContext(),mmodelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });
        fetchData();

    }


    private void fetchData() {

        ApiUtilities.getAPIInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                mmodealclasslist.addAll(response.body());

                for(int i=0;i<mmodealclasslist.size();i++)
                {
                    if(mmodealclasslist.get(i).getCountry().equals(country))
                    {

                        mtotal.setText((mmodealclasslist.get(i).getCases()));
                        mtodaytotal.setText((mmodealclasslist.get(i).getTodayCases()));
                        mdeaths.setText((mmodealclasslist.get(i).getDeaths()));
                        mtodaydeaths.setText((mmodealclasslist.get(i).getTodayDeaths()));
                        mrecoverd.setText((mmodealclasslist.get(i).getRecovered()));
                        mtodayrecoverd.setText((mmodealclasslist.get(i).getTodayRecovered()));
                        mactive.setText((mmodealclasslist.get(i).getActive()));



                        int active,total,recoverd,deaths;

                        active=Integer.parseInt(mmodealclasslist.get(i).getActive());
                        total=Integer.parseInt(mmodealclasslist.get(i).getCases());
                        recoverd=Integer.parseInt(mmodealclasslist.get(i).getRecovered());
                        deaths=Integer.parseInt(mmodealclasslist.get(i).getDeaths());


                        updategraph(active,total,recoverd,deaths);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

    }

    private void updategraph(int active, int total, int recoverd, int deaths) {
        mpiechart.clearChart();
        mpiechart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4caf50")));
        mpiechart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        mpiechart.addPieSlice(new PieModel("Recovered",recoverd,Color.parseColor("#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor("#F55c47")));
        mpiechart.startAnimation();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String item=types[i];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}