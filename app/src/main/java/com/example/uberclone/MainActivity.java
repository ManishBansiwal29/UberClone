package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    enum State{
        SignUp,LogIn
    }
    private Button btnSignUpLogin,btnOneTimeLogin;
    private State state;
    private RadioButton passengerRadioButton,driverRadioButton;
    private EditText edtUsername,edtPassword,edtDOrP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        btnSignUpLogin = findViewById(R.id.btnSignUpLogin);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);
        passengerRadioButton = findViewById(R.id.rdbPassenger);
        driverRadioButton = findViewById(R.id.rdbDriver);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtDOrP = findViewById(R.id.edtDOrP);

        btnSignUpLogin.setOnClickListener(this);
        btnOneTimeLogin.setOnClickListener(this);
        if(ParseUser.getCurrentUser()!=null){
           // ParseUser.logOut();
            transitionToPassengerActivity();

        }


        state = State.SignUp;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup_activity,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.loginItem:
                if(state == State.SignUp){
                    state = State.LogIn;
                    item.setTitle("SIgn Up");
                    btnSignUpLogin.setText("Log In");
                }else if(state == State.LogIn){
                    state = State.SignUp;
                    item.setTitle("Log In");
                    btnSignUpLogin.setText("Sign Up");
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnOneTimeLogin:


                if(edtDOrP.getText().toString().equals("Driver") || edtDOrP.getText().toString().equals("Passenger")){
                    if(ParseUser.getCurrentUser()==null){
                        ParseAnonymousUtils.logIn(new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if ( e == null) {
                                    Toast.makeText(MainActivity.this, "Anonymous User", Toast.LENGTH_LONG).show();
                                    user.put("as", edtDOrP.getText().toString());
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            transitionToPassengerActivity();
                                        }
                                    });

                                }
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Are You a Driver Or a Passenger",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnSignUpLogin:

                if(state==State.SignUp){
                    if(driverRadioButton.isChecked()==false && passengerRadioButton.isChecked()==false){
                        Toast.makeText(MainActivity.this,"are you a Passenger or a Driver",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());
                    if(driverRadioButton.isChecked()){
                        appUser.put("as","Driver");
                    }else if(passengerRadioButton.isChecked()){
                        appUser.put("as","Passenger");
                    }
                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(MainActivity.this, "Signed Up",Toast.LENGTH_SHORT).show();
                                transitionToPassengerActivity();
                            }
                        }
                    });
                }else if(state==State.LogIn){
                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(user!=null && e==null){
                                Toast.makeText(MainActivity.this,"User Logged in",Toast.LENGTH_SHORT).show();
                                transitionToPassengerActivity();
                            }
                        }
                    });
                }
                break;

        }

    }
    public void transitionToPassengerActivity(){
        if(ParseUser.getCurrentUser()!= null){
            if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);
            }
    }
}
}
