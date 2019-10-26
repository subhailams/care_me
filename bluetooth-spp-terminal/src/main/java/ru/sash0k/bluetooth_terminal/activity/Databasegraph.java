package ru.sash0k.bluetooth_terminal.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ru.sash0k.bluetooth_terminal.R;

public class Databasegraph extends AppCompatActivity {
    private DatabaseReference dr;
    Button btn_insert;
    EditText xValue,yValue;
    GraphView graphView;
    LineGraphSeries series;

//DATABASE RETRIEVAL OF DATA TO DISPLAY THE GRAPH

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // FIREBASE PART
        dr=FirebaseDatabase.getInstance().getReference().child("realtime");
        dr.keepSynced(true);

        // GRAPH PART


//        xValue = findViewById(R.id.x_value);
//        yValue = findViewById(R.id.y_value);
        btn_insert = findViewById(R.id.btn);
        graphView = findViewById(R.id.graphView);
        series= new LineGraphSeries();
        graphView.addSeries(series);

        setListeners();
    }

    private void setListeners() {
        btn_insert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String id=dr.push().getKey();

                int x=Integer.parseInt(xValue.getText().toString());
                int y=Integer.parseInt(yValue.getText().toString());

                PointValue pointValue=new PointValue(x,y);

                dr.child(id).setValue(pointValue);

                xValue.setText("");
                yValue.setText("");
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp=new DataPoint[(int)dataSnapshot.getChildrenCount()];
                int index=0;

                for(DataSnapshot myDataSnapshot:dataSnapshot.getChildren())
                {
//                    Databaseobj pointValue=myDataSnapshot.getValue(Databaseobj
//                            .class);
//          dp[index]=new DataPoint(pointValue.getTempature(),pointValue.getBpm());
//                    index++;
                }
                series.resetData(dp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
