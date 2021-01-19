package com.example.piechart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int[] colorClassArray = new int[]{Color.LTGRAY, Color.CYAN, Color.DKGRAY, Color.GREEN};
    PieChart piechart;
    EditText xValue, yValue;
    Button insertBtn;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference Ref;
    PieData pieData;
    private List<PieEntry> dataValues;
    PieDataSet pieDataSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = findViewById(R.id.x_value);
        yValue = findViewById(R.id.y_value);
        insertBtn = findViewById(R.id.btn_insert);
        Ref = firebaseDatabase.getReference("ChartValues");
        piechart = findViewById(R.id.pieChart_view);
        PieDataSet pieDataSet = new PieDataSet(dataValues, "");
        firebaseDatabase = FirebaseDatabase.getInstance();
        pieDataSet.setColors(colorClassArray);
        PieData pieData = new PieData(pieDataSet);
        //PieDataSet lineDataSet = new PieDataSet(null, null);
        insertData();
    }


    private void insertData() {
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Ref.push().getKey();
                int x = Integer.parseInt(xValue.getText().toString());
                int y = Integer.parseInt(yValue.getText().toString());
                DataPoint dataPoint = new DataPoint(x, y);
                Ref.child(id).setValue(dataPoint);

                retrieveData();
            }
        });
    }

    private void retrieveData() {
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PieEntry> dataVals = new ArrayList<>();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        DataPoint dataPoint = myDataSnapshot.getValue(DataPoint.class);
                        dataVals.add(new PieEntry(dataPoint.getxValue(), dataPoint.getyValue()));
                    }
                    showChart(dataVals);
                } else {
                    piechart.clear();
                    piechart.invalidate();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    private void showChart(ArrayList<PieEntry> dataVals) {
        pieDataSet.setValues(dataVals);
        piechart.clear();
        piechart.setData(pieData);
    }


}