package mebeidcreations.apps.livegosee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.ProgressDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static mebeidcreations.apps.livegosee.Helper.showDatePicker;


public class LoginActivity extends AppCompatActivity{




//Intents
    Intent goToMainActivity;

//Firebase
    FirebaseDBHandler firebaseDBHandler;
    FirebaseAuth.AuthStateListener mAuthListener;
    static FirebaseAuth mAuth;


    //DataTypes
    String email, password,userId;
    int backPress = 0;
    float x = -400;

//Objects
    UserInfo newUser;

//Views
    RelativeLayout LoginActivityLayout;
    RelativeLayout  containerLayout;
    LinearLayout  createAccountLayout;
    ImageButton loginBackButton;
    Button loginButton, createAccounButton;
    TextView createAccountLink, forgotPasswordLink, loginPageTitle;
    View resetPasswordView, createAccountView, loginView;
    static EditText emailField, passwordField,createNameField
            ,createEmailField,createPasswordField;
    static TextView createAgeField;
    static AutoCompleteTextView createCountryField;
    static  EditText recoveryEmail;
    ProgressDialog  progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        loginView = getLayoutInflater().inflate(R.layout.login_layout,null);
        createAccountView = getLayoutInflater().inflate(R.layout.create_account,null);
        resetPasswordView = getLayoutInflater().inflate(R.layout.forgot_password_layout,null);

        containerLayout = (RelativeLayout)findViewById(R.id.container_layout);
        containerLayout.addView(loginView);


        LoginActivityLayout = (RelativeLayout) findViewById(R.id.activity_login_layout);

        loginBackButton = (ImageButton)findViewById(R.id.login_page_back_button);
        loginPageTitle = (TextView)findViewById(R.id.login_page_title);

        createAccountLink = (TextView)findViewById(R.id.create_account_link);
        Context context = getApplicationContext();
        emailField = (EditText)findViewById(R.id.emailField);

        progressDialog = new ProgressDialog(LoginActivity.this,R.style.MyDialogTheme);
        progressDialog.setMessage("Signing In ....");
        progressDialog.setInverseBackgroundForced(true);

        firebaseDBHandler = new FirebaseDBHandler(context);


        loginBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                containerLayout.removeAllViews();
                containerLayout.addView(loginView);
                loginBackButton.setVisibility(View.GONE);
                loginPageTitle.setText("LOG IN");
                resetLoginFields();
                resetSignUpFields();
                resetRecoveryEmailField();


            }
        });



        forgotPasswordLink = (TextView)findViewById(R.id.forgotPasswordLink);
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loginBackButton.setVisibility(View.VISIBLE);
                loginPageTitle.setText("RESET YOUR PASSWORD");
                containerLayout.removeView(loginView);
                containerLayout.addView(resetPasswordView);

                recoveryEmail = (EditText)resetPasswordView.findViewById(R.id.reset_password_emailField);
                final Button resetPassword = (Button)resetPasswordView.findViewById(R.id.reset_password_button);

                resetPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(recoveryEmail.getText().toString().isEmpty())
                        {
                            recoveryEmail.setError("Please enter email address");
                        }
                        else
                        {
                            sendResetPassword(recoveryEmail.getText().toString());




                        }

                    }
                });




            }
        });

        passwordField=(EditText)findViewById(R.id.passwordField);
        goToMainActivity  = new Intent(this, MainActivity.class);





////Instance and Listener to know if a user is checked in or not
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null) {
                    // User is signed in
                    userId =  mAuth.getCurrentUser().getUid().toString();
                    goToMainActivity.putExtra("user_id",userId);
                    startActivity(goToMainActivity);


                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };







///////////////////////////////////////////End of listener//////////////////////////////////////////


//Login with email and password
        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                email = emailField.getText().toString().replaceAll("\\s+","");
                password = passwordField.getText().toString();
                progressDialog.show();
                signInWithEmail(email,password);



            }
        });


        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Locale[] locales = Locale.getAvailableLocales();
                ArrayList<String> countries = new ArrayList<String>();
                for (Locale locale : locales) {
                    String country = locale.getDisplayCountry();
                    if (country.trim().length()>0 && !countries.contains(country)) {
                        countries.add(country);
                    }
                }
                Collections.sort(countries);

                ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.drop_down_text,R.id.country_text,countries);



                createEmailField= (EditText)createAccountView.findViewById(R.id.create_account_emailField);
                createNameField= (EditText)createAccountView.findViewById(R.id.create_account_name_field);
                createPasswordField = (EditText)createAccountView.findViewById(R.id.create_account_password_field);
                createAgeField = (TextView)createAccountView.findViewById(R.id.create_account_age_field);
                createAgeField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showDatePicker(createAgeField,getSupportFragmentManager());
                    }
                });



                createCountryField = (AutoCompleteTextView)createAccountView.findViewById(R.id.create_account_country_field);
                createCountryField.setAdapter(countriesAdapter);

                containerLayout.removeAllViews();
                containerLayout.addView(createAccountView);
                loginBackButton.setVisibility(View.VISIBLE);
                loginPageTitle.setText("CREATE ACCOUNT");


                createAccounButton = (Button)findViewById(R.id.create_account_button);
                createAccounButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressDialog.setMessage("Creating Account ...");
                        progressDialog.show();

                        createAccount(createEmailField.getText().toString(),createPasswordField.getText().toString());






                    }
                });



            }
        });


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


    private void signInWithEmail(final String email, final String password) {
        Log.d("", "signIn:" + email);
        if (!validateSignInForms(emailField, passwordField)) {
            progressDialog.dismiss();
            return;
        }


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInWithEmail:onComplete:" + task.isSuccessful());





                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            progressDialog.dismiss();
                             Log.d("SIGN_IN_ERROR", task.getException().getMessage());
                             String error = task.getException().getMessage();
                                if (error.contains("deleted")) {
                                    Toast.makeText(getApplicationContext(), "We can't find your email in our databases, could you please sign up!!", Toast.LENGTH_LONG).show();
                                }

                                else if (error.contains("badly"))
                                {
                                    emailField.setError("Please eneter a valid email");
                                }
                                else if(error.contains("password"))
                                {
                                    Toast.makeText(getApplicationContext(), "Invalid password or email combination, please try again", Toast.LENGTH_LONG).show();

                                }

                        }
                        else
                        {
                            resetLoginFields();
                            progressDialog.dismiss();

                            startActivity(goToMainActivity);

                            finishAffinity();
                        }
                    }
                });
        ////////////////////////////////////////////// [END sign_in_with_email]///////////////////////////////////
    }





    public  boolean validateSignInForms(EditText email_field, EditText pass_field) {
        boolean valid = true;


        String email = email_field.getText().toString();
        if (TextUtils.isEmpty(email)) {
            email_field.setError("Required.");
            valid = false;
        } else {
            email_field.setError(null);
        }

        String password = pass_field.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pass_field.setError("Required.");
            valid = false;
        } else {
            pass_field.setError(null);
        }

        return valid;
    }



    public  boolean validateSignUpForms(EditText createEmailField, EditText createPasswordField,
                                                EditText createNameField, TextView createAgeField, EditText createCountryField) {
        boolean valid = true;


        String email = createEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            createEmailField.setError("Required.");
            valid = false;
        } else {
            createEmailField.setError(null);
        }

        String password = createPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            createPasswordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        String name = createNameField.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            valid = false;
            createNameField.setError("Required");
        }
        else
        {
            createNameField.setError(null);

        }
        String country = createCountryField.getText().toString();
        if(TextUtils.isEmpty(country))
        {
            valid = false;
            createCountryField.setError("Required");
        }
        else
        {
            createCountryField.setError(null);

        }


        String dob = createAgeField.getText().toString();
        if(TextUtils.isEmpty(dob))
        {
            valid = false;
            createAgeField.setError("Required");
        }
        else
        {
            createAgeField.setError(null);

        }

        return valid;
    }




    private void createAccount(String email, String password) {
        Log.d("Email", "createAccount:" + email);
        if (!validateSignUpForms(createEmailField, createPasswordField,createNameField,createAgeField,createCountryField)) {

            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Email", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            String error = task.getException().getMessage();
                            Log.d("CREATE_ACCOUNT_ERROR", error);
                            if(error.contains("badly"))
                            {
                               createEmailField.setError("Please enter a valid email address");
                            }
                            else if(error.contains("password"))
                            {
                                createPasswordField.setError("Password should be at least 6 characters");
                            }
                            else if(error.contains("another"))
                            {
                                createEmailField.setError("Email address already in use by another account");
                            }
                               progressDialog.dismiss();

                        } else {
                               newUser = new UserInfo();
                                newUser.user_id = mAuth.getCurrentUser().getUid();
                                newUser.user_name = createNameField.getText().toString();
                                newUser.user_dob =  createAgeField.getText().toString();
                                newUser.user_country = createCountryField.getText().toString();
                                newUser.user_trip_count = 0;
                                firebaseDBHandler.addUserInfoToDB(newUser,userId);
                                progressDialog.dismiss();




                        }

                    }
                });

    }


    private void sendResetPassword(final String email) {

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("", "resetPassword:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {

                    Log.w("", "resetPassword:failed", task.getException());

                    Toast.makeText(getApplicationContext(), "Email doesn't exist, please check your details", Toast.LENGTH_SHORT).show();



                } else {
                    Toast.makeText(getApplicationContext(), "Password reset link was sent to "+email, Toast.LENGTH_SHORT).show();
                    loginPageTitle.setText("LOG IN");
                    loginBackButton.setVisibility(View.GONE);

                    containerLayout.removeView(resetPasswordView);
                    containerLayout.addView(loginView);
                    recoveryEmail.setText("");
                }

            }
        });


    }


    public static void signOut() {
        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {



        }

    public void resetSignUpFields()
    {
       if(createEmailField!=null)
        {
             createEmailField.setText("");
        }

        if(createAgeField!=null)
        {
            createAgeField.setText("");

        }

        if(createPasswordField!=null)
        {
            createPasswordField.setText("");

        }

        if(createNameField!=null)
        {
            createNameField.setText("");

        }

        if(createCountryField!=null)
        {
            createCountryField.setText("");

        }

    }

    public static void resetLoginFields()
    {

        if(emailField!=null)
        {
            emailField.setText("");

        }
        if(emailField!=null)
        {
            passwordField.setText("");
        }
    }

    public void resetRecoveryEmailField()
    {
        if(recoveryEmail!=null)
        {
            recoveryEmail.setText("");
        }

    }







}

