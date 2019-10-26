package ru.sash0k.bluetooth_terminal.activity;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ru.sash0k.bluetooth_terminal.R;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.X;
import static android.view.View.Y;

public class BPM extends AppCompatActivity {
    EditText coor;
    SQLiteDatabase db;
    realtime D = new realtime();
    GraphView graphView;
    LineGraphSeries series;
    //    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
    int min;
    //    private boolean show_timings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm);

        // coor = (EditText)findViewById(R.id.cood);
        graphView = findViewById(R.id.bpmgraph);
//        graphView.getViewport().setMinX(10);
//        graphView.getViewport().setMaxX(50);
//        graphView.getViewport().setMinY(10);
//        graphView.getViewport().setMaxY(50);
////
//        graphView.getViewport().setYAxisBoundsManual(true);
//        graphView.getViewport().setXAxisBoundsManual(true);
        series= new LineGraphSeries();
        graphView.addSeries(series);


        min=D.min;

        // CREATE A DATABASE
        db = openOrCreateDatabase("plots",MODE_PRIVATE,null);

        // CREATING A TABLE
   //     db.execSQL("CREATE TABLE IF NOT EXISTS xy(id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER);");

        // TRY PLOTTING ON CREATE OF THE APP :
        plots();
    }

    // PLOT NEW POINTS BY CLICKING ON UPDATE
    public void update(View v){
        plots();
        Toast.makeText(this,"plot inserted"+min,Toast.LENGTH_LONG).show();

    }

    public void plots() {
        // TRY TO READ ALL THE POINTS

        Cursor r = db.rawQuery("Select * from xy", null);

        // CHECK THE NO. OF LINES (POINTS TO PLOT)
        Integer count = r.getCount();
        Integer i = 0;


        // PLOT THE GRAPH ONLY IF THERE ARE SOME DATA IN THE TABLE
        if (count > 0) {

            DataPoint[] dp = new DataPoint[count];

            if (r.moveToFirst()) {
                do {
                    //HERE 0:TIME ND 1:HEARTBEAT
                    dp[i] = new DataPoint(r.getInt(0), r.getInt(2));
                    i++;
                } while (  r.moveToNext());
            }
            r.close();


            series.resetData(dp);


        }
    }
//
//    public void push(View v){
//
//        // INSTEAD OF GETTING FROM THIS BOX U CAN GET IT FROM OUR "MSG"
//        String data = coor.getText().toString();
//        String[] d = data.split(",");
//        Integer x,y;
//
//        // ASSIGN 2 VALUES HERE
//        x=Integer.parseInt(d[0]);
//        y=Integer.parseInt(d[1]);
//
//        // CHECKING IF THE ENTRY IS GREATER THAT 50 , AND CLEAR THE TABLE IF IT EXCEEDS
//
//        Cursor r = db.rawQuery("Select * from xy",null);
//        Integer count = r.getCount();
//
//        if(count > 100){
//            db.execSQL("DROP TABLE xy");
//            db.execSQL("CREATE TABLE IF NOT EXISTS xy(id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER)");
//        }
//
//        // NOW COMES THE INSERT PART :
//
//        db.execSQL("INSERT INTO xy(x,y) VALUES("+x+","+y+");");
//        coor.setText("");
//        Toast.makeText(this,"plot inserted"+x+y,Toast.LENGTH_LONG).show();
//    }

}
