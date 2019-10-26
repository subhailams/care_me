package ru.sash0k.bluetooth_terminal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.lang.Math;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.series.Series;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ru.sash0k.bluetooth_terminal.DeviceData;
import ru.sash0k.bluetooth_terminal.R;
import ru.sash0k.bluetooth_terminal.Utils;
import ru.sash0k.bluetooth_terminal.bluetooth.DeviceConnector;
import ru.sash0k.bluetooth_terminal.bluetooth.DeviceListActivity;

import static java.sql.Types.FLOAT;
import static java.sql.Types.NULL;


public final class DeviceControlActivity extends BaseActivity {


    Queue<Double> q = new LinkedList<>();
    Double n;
    AlertDialog.Builder builder;

    int flag = 1;
    int count=0;
    double[] out = new double[3];
    double data[];

    double theta[][] = {{-0.362018786, -0.0123595727, 0.0600493069, -0.196411666, 0.305428114, -1.52285669, -0.674198958, 7.91907513, -2.21831760, 1.50843367},
            {0.180213542, 0.0314819099, -0.119360476, 0.128081769, -0.0892499659, -1.85181618, 0.750404432, -5.70908392, 1.42156881, -2.43165445},
            {0.563107655, 0.00184205195, -0.0433835402, -0.00437300553, 0.119742259, 0.849754534, 0.354160080, -1.11276557, 0.688470461, 0.527334326}};
    double sum;

    //PLOTING GRAPH
    EditText coor;
    SQLiteDatabase db;
    GraphView graphView, graph;
    LineGraphSeries series;
    String column1,column2,column3;

    private static final String DEVICE_NAME = "DEVICE_NAME";
    private static final String LOG = "LOG";
    //chartdata


    // Подсветка crc
    private static final String CRC_OK = "#FFFF00";
    private static final String CRC_BAD = "#FF0000";

    private static final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
    public static String msg;
    String temp;
    realtime D= new realtime();
    private DatabaseReference dr, DatabaseObject;

    Float tempature;
    Float  bpm, ax, ay, az, gx, gy, gz;
    int min, hrs,sec;
    int[] accx = new int[20];
    int[] accy = new int[20];
    int[] accz = new int[20];
    int i;


    String values[];
    String times[];

    private static String MSG_NOT_CONNECTED;
    String EmailHolder, PasswordHolder;
    private static String MSG_CONNECTING;
    private static String MSG_CONNECTED;

    Button btn_insert;

    private static DeviceConnector connector;
    // private static BluetoothResponseHandler mHandler;
    private static BluetoothResponseHandler mHandler;

    private StringBuilder logHtml;
    private TextView logTextView;
    private EditText commandEditText;

    SQLiteDatabase sqLiteDatabaseObj;
    String NameHolder, NumberHolder, SQLiteDataBaseQueryHolder;
    Button EnterData;


    private boolean hexMode, checkSum, needClean;
    private boolean show_timings, show_direction;
    private String command_ending;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //FIREBASE CONNECTION

        dr = FirebaseDatabase.getInstance().getReference().child("realtime");
        dr.keepSynced(true);

        db = openOrCreateDatabase("plots", MODE_PRIVATE, null);
        // CREATING A TABLE
        db.execSQL("CREATE TABLE IF NOT EXISTS xy(id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER);");


        PreferenceManager.setDefaultValues(this, R.xml.settings_activity, false);

        if (mHandler == null) mHandler = new BluetoothResponseHandler(this);
        else mHandler.setTarget(this);

        MSG_NOT_CONNECTED = getString(R.string.msg_not_connected);
        MSG_CONNECTING = getString(R.string.msg_connecting);
        MSG_CONNECTED = getString(R.string.msg_connected);

        setContentView(R.layout.activity_terminal);
        if (isConnected() && (savedInstanceState != null)) {
            setDeviceName(savedInstanceState.getString(DEVICE_NAME));
        } else getSupportActionBar().setSubtitle(MSG_NOT_CONNECTED);

        this.logHtml = new StringBuilder();
        if (savedInstanceState != null) this.logHtml.append(savedInstanceState.getString(LOG));

        this.logTextView = (TextView) findViewById(R.id.log_textview);
        this.logTextView.setMovementMethod(new ScrollingMovementMethod());
        this.logTextView.setText(Html.fromHtml(logHtml.toString()));

        this.commandEditText = (EditText) findViewById(R.id.command_edittext);
        // soft-keyboard send button
        this.commandEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendCommand(null);
                    return true;
                }
                return false;
            }
        });
        // hardware Enter button
        this.commandEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            sendCommand(null);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }
    // ==========================================================================

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DEVICE_NAME, deviceName);
        if (logTextView != null) {
            outState.putString(LOG, logHtml.toString());
        }
    }
    // ============================================================================



    private boolean isConnected() {
        return (connector != null) && (connector.getState() == DeviceConnector.STATE_CONNECTED);
    }
    // ==========================================================================



    private void stopConnection() {
        if (connector != null) {
            connector.stop();
            connector = null;
            deviceName = null;
        }
    }
    // ==========================================================================


    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    // ============================================================================


    @Override
    public boolean onSearchRequested() {
        if (super.isAdapterReady()) startDeviceListActivity();
        return false;
    }
    // ==========================================================================


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control_activity, menu);
        final MenuItem bluetooth = menu.findItem(R.id.menu_search);
        if (bluetooth != null) bluetooth.setIcon(this.isConnected() ?
                R.drawable.ic_action_device_bluetooth_connected :
                R.drawable.ic_action_device_bluetooth);
        return true;
    }
    // ============================================================================


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;

            case R.id.menu_clear:
                if (logTextView != null) logTextView.setText("");
                return true;

            case R.id.menu_send:
                if (logTextView != null) {
                    final String msg = logTextView.getText().toString();
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
                }
                return true;

            case R.id.menu_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ============================================================================


    @Override
    public void onStart() {
        super.onStart();

        // hex mode
        final String mode = Utils.getPrefence(this, getString(R.string.pref_commands_mode));
        this.hexMode = "HEX".equals(mode);
        if (hexMode) {
            commandEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            commandEditText.setFilters(new InputFilter[]{new Utils.InputFilterHex()});
        } else {
            commandEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            commandEditText.setFilters(new InputFilter[]{});
        }

        // checksum
        final String checkSum = Utils.getPrefence(this, getString(R.string.pref_checksum_mode));
        this.checkSum = "Modulo 256".equals(checkSum);


        this.command_ending = getCommandEnding();


        this.show_timings = Utils.getBooleanPrefence(this, getString(R.string.pref_log_timing));
        this.show_direction = Utils.getBooleanPrefence(this, getString(R.string.pref_log_direction));
        this.needClean = Utils.getBooleanPrefence(this, getString(R.string.pref_need_clean));
    }
    // ============================================================================



    private String getCommandEnding() {
        String result = Utils.getPrefence(this, getString(R.string.pref_commands_ending));
        if (result.equals("\\r\\n")) result = "\r\n";
        else if (result.equals("\\n")) result = "\n";
        else if (result.equals("\\r")) result = "\r";
        else result = "";
        return result;
    }
    // ============================================================================


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (super.isAdapterReady() && (connector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Utils.log("BT not enabled");
                }
                break;
        }
    }
    // ==========================================================================



    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            connector = new DeviceConnector(data, mHandler);
            connector.connect();
        } catch (IllegalArgumentException e) {
            Utils.log("setupConnector failed: " + e.getMessage());
        }
    }
    // ==========================================================================



    public void sendCommand(View view) {
        if (commandEditText != null) {
            String commandString = commandEditText.getText().toString();
            if (commandString.isEmpty()) return;


            if (hexMode && (commandString.length() % 2 == 1)) {
                commandString = "0" + commandString;
                commandEditText.setText(commandString);
            }

            // checksum
            if (checkSum) {
                commandString += Utils.calcModulo256(commandString);
            }

            byte[] command = (hexMode ? Utils.toHex(commandString) : commandString.getBytes());
            if (command_ending != null) command = Utils.concat(command, command_ending.getBytes());
            if (isConnected()) {
                connector.write(command);
                appendLog(commandString, hexMode, true, needClean);
            }
        }
    }

    // ==========================================================================
//    public DeviceControlActivity() {
//    }
//
//
//    public int getBpm() {
//        return bpm;
//    }
//
//    public int getMin() {
//        return min;
//    }
//
//    public int getHrs() {
//        return hrs;
//    }
//
//    public void setHrs(int hrs) {
//        this.hrs = hrs;
//    }
//
//    public void setMin(int min) {
//        this.min = min;
//    }
//
//    public void setBpm(int bpm) {
//        this.bpm = bpm;
//    }
//
//    public Float getTempature() {
//        return tempature;
//    }
//
//    public void setTempature(Float tempature) {
//        this.tempature = tempature;
//    }


    void appendLog(String message, boolean hexMode, boolean outgoing, boolean clean) {

        StringBuilder msg = new StringBuilder();

        if (show_timings) msg.append(timeformat.format(new Date()));

        //        if (show_direction) {
//            final String arrow = (outgoing ? " << " : " >> ");
//            msg.append(arrow);
//        } else msg.append(" ");
//
//
        message = message.replace("\r", "").replace("\n", "");
//
//
//        String crc = "";
//        boolean crcOk = false;
//        if (checkSum) {
//            int crcPos = message.length() - 2;
//            crc = message.substring(crcPos);
//            message = message.substring(0, crcPos);
//            crcOk = outgoing || crc.equals(Utils.calcModulo256(message).toUpperCase());
//            if (hexMode) crc = Utils.printHex(crc.toUpperCase());
//        }
//
//        // Лог в html
//        msg.append("<b>")
//                .append(hexMode ? Utils.printHex(message) : message)
//                .append(checkSum ? Utils.mark(crc, crcOk ? CRC_OK : CRC_BAD) : "")
//                .append("</b>")
//                .append("<br>");
//


        temp = message.toString();
        values = temp.split(",");
        times = String.valueOf(msg).split(":");
        logHtml.append(msg);
        // Toast.makeText(this, "data : "+msg,Toast.LENGTH_LONG).show();

        /// HERE WE DO OUR STUFF - FIREBASE :
//        String time= msg.toString();

//        int HH=Integer.parseInt(times[0]);
//        int MM=Integer.parseInt(times[1]);
////     int SS=Integer.parseInt(times[2]);

        //PUSHING DATA TO CLOUD

        tempature = Float.parseFloat(values[0]);
        bpm =Float.parseFloat(values[1]);
        ax = Float.parseFloat(values[2]);
        ay = Float.parseFloat(values[3]);
        az = Float.parseFloat(values[4]);
        min = Integer.parseInt(times[1]);
        hrs = Integer.parseInt(times[0]);
        sec = Integer.parseInt(times[2]);


        while(i<20) {
            //get data for x,y and z
            Float x = ax;
            Float y = ay;
            Float z = az;
            n = Math.sqrt((Math.pow(x, 2)) + (Math.pow(y, 2)) + (Math.pow(z, 2)));
            q.add(n);
            i++;
          /*Toast.makeText(this, "Normalstar: " + n, Toast.LENGTH_LONG).show();*/
            break;
        }


        // Toast.makeText(this, +ay +" "+az, Toast.LENGTH_LONG).show();

        if (tempature != NULL && bpm != NULL && ax != NULL && ay != NULL &&az!=NULL) {

            D.setBpm(bpm);
            D.setAx(ax);
            D.setTempature(tempature);
            D.setAy(ay);
            D.setAz(az);

            D.setTime(msg.toString());
            D.setMin(min);
            D.setHrs(hrs);

            dr.push().setValue(D);

            // Toast.makeText(this, "data pushed!!" + sec, Toast.LENGTH_LONG).show();
            //  Toast.makeText(this, "data pushed!!" + tempature+" "+bpm , Toast.LENGTH_LONG).show();


            //ADDING DATA TO SQL TO DISPLAY THE GRAPH

            if (min == 0 && sec < 3) {
                Cursor r = db.rawQuery("Select * from xy", null);
                Integer count = r.getCount();

                if (count > 100) {
                    db.execSQL("DROP TABLE xy");
                    db.execSQL("CREATE TABLE IF NOT EXISTS xy(id INTEGER PRIMARY KEY AUTOINCREMENT, x INTEGER, y INTEGER)");
                }

                // NOW COMES THE INSERT PART :

                db.execSQL("INSERT INTO xy(x,y) VALUES(" + bpm + "," + tempature + ");");
                // }
                // Toast.makeText(this, "plot inserted" + min + D.getMin(), Toast.LENGTH_LONG).show();

            }

//        final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
//        if (scrollAmount > 0)
//            logTextView.scrollTo(0, scrollAmount);
//        else logTextView.scrollTo(0, 0);

            //if (clean) commandEditText.setText("");
        }
        // =========================================================================
    }

    void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        getSupportActionBar().setSubtitle(deviceName);
    }
    // ==========================================================================


    static class BluetoothResponseHandler extends Handler {
        private WeakReference<DeviceControlActivity> mActivity;

        public BluetoothResponseHandler(DeviceControlActivity activity) {
            mActivity = new WeakReference<DeviceControlActivity>(activity);
        }

        public void setTarget(DeviceControlActivity target) {
            mActivity.clear();
            mActivity.clear();
            mActivity = new WeakReference<DeviceControlActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceControlActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:

                        Utils.log("MESSAGE_STATE_CHANGE: " + msg.arg1);
                        final ActionBar bar = activity.getSupportActionBar();
                        switch (msg.arg1) {
                            case DeviceConnector.STATE_CONNECTED:
                                bar.setSubtitle(MSG_CONNECTED);
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                bar.setSubtitle(MSG_CONNECTING);
                                break;
                            case DeviceConnector.STATE_NONE:
                                bar.setSubtitle(MSG_NOT_CONNECTED);
                                break;
                        }
                        activity.invalidateOptionsMenu();
                        break;

                    case MESSAGE_READ:
                        final String readMessage = (String) msg.obj;
                        if (readMessage != null) {
                            activity.appendLog(readMessage, false, false, activity.needClean);
                        }
                        break;

                    case MESSAGE_DEVICE_NAME:
                        activity.setDeviceName((String) msg.obj);
                        break;

                    case MESSAGE_WRITE:
                        // stub
                        break;

                    case MESSAGE_TOAST:
                        // stub
                        break;
                }
            }
        }
    }

    //FOR HEATBEAT DISPLAY
    public void heartfuncchart(View view){

        Intent i = new Intent(this, Heartbeat.class);
        startActivity(i);
    }

    public void heartfunctv(View view){

        //  if((min==0||min==30)&&(sec<3)||(sec<33&&sec>=30)) {
        logTextView.append(Html.fromHtml("TIME : " + times[0] + ":" + times[1] + ":" + times[2] + "   " + "TEMPATURE :" + tempature + "<br>"));
        count++;
        // }
        if(count==25)
        {
            logTextView.setText(null);
            count=0;
        }

    }
    //FOR BPM DISPLAY
    public void bpmgraph(View view){
        Intent i = new Intent(this, BPM.class);
        startActivity(i);
    }
    public void bpmtv(View view){

        // if((min==0||min==30)&&(sec<3)||(sec<33&&sec>=30)) {

        logTextView.append(Html.fromHtml("TIME : " + times[0] + ":" + times[1] + ":" + times[2] + "    " + "BPM :" + bpm + "<br>"));
        // }
        if(count==25)
        {
            logTextView.setText(null);
            count=0;
        }
    }
//ML TRAINED MODEL

    public void health(View view){
//        Intent i =new Intent(this,BPM.class);
//        startActivity(i);
        builder = new AlertDialog.Builder(this);


        //Setting message manually and performing action on button click

        //Creating dialog box
        AlertDialog alert = builder.create();
       // alert.setTitle("AlertDialogExample");
        alert.show();
        double[] data = new double[10];
        data[0] = 1;
        //age
        data[1] = 40;
        data[2] = tempature;
        data[3] = bpm;
        //bmi
        data[4] = 25;
        //gender
        data[5] = 1;
        //athelete or not
        data[9] = 0;

//get data for x,y and z
        Float x = ax;
        Float y = ay;
        Float z = az;
        n = Math.sqrt((x * x) + (y * y) + (z * z));
        q.add(n);
        q.remove();
        Double mu = 0.0;
        Iterator<Double> iter = q.iterator();
        while (iter.hasNext()) {
            Double t = iter.next();
            mu = mu + t;
        }
        mu = mu / 20;
        Double sig = 0.0;
        Iterator<Double> iter1 = q.iterator();
        while (iter1.hasNext()) {
            Double t1 = iter1.next();
            sig = sig + (Math.pow((t1 - mu), 2));
        }
        double acc = Math.sqrt(sig / 20);
        //  Toast.makeText(this, "Normalpoor : " , Toast.LENGTH_LONG).show();
        //STILL
        if (acc < 20) {
            data[6] = 0;
            data[7] = 0;
            data[8] = 1;
        } else if (acc >= 100) {
            data[6] = 0;
            data[7] = 1;
            data[8] = 0;
        } else if (acc >= 20 && acc < 100) {
            data[6] = 1;
            data[7] = 0;
            data[8] = 0;
        }

        //Dot product of the two matrices
        for (i = 0; i < 3; i++) {
            sum = 0;
            for (int j = 0; j < 10; j++)
                sum = sum + theta[i][j] * data[j];
            out[i] = sum;
        }
        double max = 0;
        int index = 0;
//Finding sigmoid
        for (i = 0; i < 3; i++) {
            out[i] = (1 / (1 + Math.exp(-out[i])));
            if (out[i] > max) {
                max = out[i];
                index = i;
            }
        }
        switch(index)
        {
            case 2:
                Toast.makeText(this, "Hurray! You are Normal",Toast.LENGTH_LONG).show();
                logTextView.append(Html.fromHtml("TIME : " + times[0] + ":" + times[1] + ":" + times[2] + "   " + "NORMAL"+"<br>"));
               // alert.setTitle("Everything is normal!");
                break;
            case 1:
                //System.out.println("High");
                Toast.makeText(this, "Take Care! Prone to miscarriage",Toast.LENGTH_LONG).show();
                logTextView.append(Html.fromHtml("TIME : " + times[0] + ":" + times[1] + ":" + times[2] + "   " + "HIGH"+"<br>"));
                //Setting the title manually
               // alert.setTitle("ALERT...THE BPM IS TOO HIGH");
                builder.setMessage(R.string.dialog_high) .setTitle(R.string.dialog_title);
                builder.setMessage("ALERT your bpm is high")
                        .setCancelable(false)

                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                //  Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                //    Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case 0:
                //System.out.println("Low");
                Toast.makeText(this, "Take Care! Prone to miscarriage",Toast.LENGTH_LONG).show();

                logTextView.append(Html.fromHtml("TIME : " + times[0] + ":" + times[1] + ":" + times[2] + "   " + "LOW"+"<br>"));
                //alert.setTitle("ALERT...THE BPM IS TOO LOW");
                builder.setMessage(R.string.dialog_low) .setTitle(R.string.dialog_title);

                //Setting message manually and performing action on button click
                builder.setMessage("ALERT YOUR BPM IS LOW")
                        .setCancelable(false)

                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                //  Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                //    Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box


                break;




        }
    }
}


// ==========================================================================