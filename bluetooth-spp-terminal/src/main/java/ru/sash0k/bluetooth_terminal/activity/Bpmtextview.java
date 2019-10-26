package ru.sash0k.bluetooth_terminal.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

//import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import ru.sash0k.bluetooth_terminal.R;

public class Bpmtextview extends BaseActivity  {
    private TextView logTextView;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//FIREBASE CONNECTION


        logTextView = (TextView)findViewById(R.id.log_textview);
       // logTextView.append(Html.fromHtml( temp+" "+msg+" "+values[0]+" "+values[1]));
        logTextView = (TextView)findViewById(R.id.log_textview);

        Intent intent = getIntent();
        str = intent.getStringExtra("message");
        logTextView.setText(str);
    }
}