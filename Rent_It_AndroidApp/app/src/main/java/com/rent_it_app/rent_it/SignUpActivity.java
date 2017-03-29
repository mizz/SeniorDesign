package com.rent_it_app.rent_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.rent_it_app.rent_it.json_models.User;
import com.rent_it_app.rent_it.json_models.UserEndpoint;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mimi on 1/10/17.
 */

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignUp";
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mDisplayNameField;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    User newUser;
    Retrofit retrofit;
    UserEndpoint userEndpoint;
    Gson gson;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mDisplayNameField = (EditText) findViewById(R.id.field_display_name);
        mFirstNameField = (EditText) findViewById(R.id.field_first_name);
        mLastNameField = (EditText) findViewById(R.id.field_last_name);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userEndpoint = retrofit.create(UserEndpoint.class);

        gson = new Gson();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                   /* Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(mDisplayNameField.getText().toString()).build();

                    //Log.d(TAG, "my username is " + mDisplayNameField.getText().toString());

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Username added");

                                    }
                                }
                            });


                    Log.d(TAG, "Username:"+ user.getDisplayName());*/

                    // Only send if email is not confirmed.
                    if (!user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Verfify Email sent");
                                            Toast.makeText(SignUpActivity.this, "We have sent you a verification email", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                            startActivity(intent);
                                            Log.d(TAG, "changed to sign in activity");
                                        }
                                    }
                                });
                        mAuth.signOut();
                        //user = null;
                    }

                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                hideProgressDialog();
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            //String userId = task.getResult().getUser().getUid();

                           /* User userData = new User(mFirstNameField.getText().toString(), mLastNameField
                                    .getText().toString(), mDisplayNameField.getText().toString());*/

                            newUser.setUid(task.getResult().getUser().getUid());
                            newUser.setEmail(task.getResult().getUser().getEmail());
                            newUser.setFirstName(mFirstNameField.getText().toString());
                            newUser.setLastName(mLastNameField.getText().toString());
                            newUser.setDisplayName(mDisplayNameField.getText().toString());
                            newUser.setFcmToken("");
                            newUser.setBraintreeCustomerId(UUID.randomUUID().toString());


                            Call<User> call = userEndpoint.addUser(newUser);
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {

                                    int statusCode = response.code();
                                    //rList = response.body();
                                    Log.d("retrofit.call.enqueue", ""+statusCode);

                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Log.d("retrofit.call.enqueue", t.toString());

                                }

                            });


                            //Toast.makeText(SignUpActivity.this, userId, Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "your user id is:" + userId);
                            //mDatabase.child(userId).setValue(userData);


                        }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(SignUpActivity.this, "Email Address already registered", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(SignUpActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });


        //Log.d(TAG, "createAccount:end:userId:" + userId);
    }




    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        }else if(!isEmailValid(email)){
            mEmailField.setError("Enter Valid Email Address");
            valid = false;
        } else {
            mEmailField.setError(null);
        }



        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required");
            valid = false;
        } else if(password.length() < 6) {
            mPasswordField.setError("6 or more characters required");
            valid = false;
        }else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    public void backToSignIn(View view){
        finish();
    }

    public void signUpUser(View view){
        createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }
}

