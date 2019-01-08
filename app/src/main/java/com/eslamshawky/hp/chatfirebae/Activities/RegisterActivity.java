package com.eslamshawky.hp.chatfirebae.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eslamshawky.hp.chatfirebae.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
MaterialEditText username,email,password;
Button button_register;
FirebaseAuth mAuth;
DatabaseReference reference;
    EditText select_place;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button_register = findViewById(R.id.button_register);
        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        select_place = findViewById(R.id.select_place);
        select_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(RegisterActivity.this), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

                    e.printStackTrace();

                }
                ;


            }
        });




        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserName = username.getText().toString();
                String Email = email.getText().toString();
                String PassWord = password.getText().toString();

                if(TextUtils.isEmpty(UserName) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(PassWord)){
                    Toast.makeText(RegisterActivity.this, "All fields are required...", Toast.LENGTH_SHORT).show();
                }
                else if (PassWord.length() < 6 ){
                    Toast.makeText(RegisterActivity.this, "Password at least 6 character", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(UserName,Email,PassWord);
                }
            }
        });

    }

    private void register(final String username, String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid  = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance()
                                    .getReference("Users").child(userid);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status","offline");
                            hashMap.put("search",username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()){
                                     Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                     startActivity(intent);
                                     finish();
                                 }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "you can't register this email or password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(this, data);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String Address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(Address);
                select_place.setText(Address);

            }
        }
    }
}
