package ru.sash0k.bluetooth_terminal.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.sash0k.bluetooth_terminal.R;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email,pass;
    LinearLayout login_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email =(EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);
        login_box =(LinearLayout)findViewById(R.id.login_box);

        // login_box.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

        mAuth = FirebaseAuth.getInstance();
//
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // check if the user is already logged in , if yes then skip this login page //

        if(currentUser != null ){
            Intent i = new Intent (this, DeviceControlActivity.class);
            startActivity(i);
            finish();
        }

    }


    public void auth(View view) {
        String e = email.getText().toString();
        String p = pass.getText().toString();

        if (!e.isEmpty() && !p.isEmpty()) {
            mAuth.signInWithEmailAndPassword(e, p)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();

                                Intent i = new Intent(Login.this, DeviceControlActivity.class);
                                startActivity(i);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

        else {
            Toast.makeText(Login.this, "Please provide valid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
