package com.paypal.firebasemobitalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        setContentView(R.layout.activity_login);

        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();

                            configFetched();
                        }
                    }
                });

        mUsername = findViewById(R.id.text_username);
        mPassword = findViewById(R.id.text_password);

        findViewById(R.id.registerbtn).setOnClickListener(this);
        findViewById(R.id.loginbtn).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Log.e("Current User : ", currentUser.toString());
    }

    private void configFetched() {
        String welcomeMessage = mFirebaseRemoteConfig.getString("show_ios_message");
        Log.e("configFetched : ", welcomeMessage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginbtn:
                loginToFirebase();
                break;

            case R.id.registerbtn:
                registerForFirebase();
                break;
        }
    }

    private void registerForFirebase() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if (!doValidation(username, password)) return;

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d("REGISTER : ", "createUserWithEmail:success");
                            loginSuccess();
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w("REGISTER : ", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginToFirebase() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        if (!doValidation(username, password)) return;

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN : ", "signInWithEmailAndPassword:success");
                            loginSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN : ", "signInWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean doValidation(String username, String password) {

        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username filed should not bbe empty");
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password filed should not bbe empty");
            return false;
        }

        if (!isValidEmail(mUsername.getText())) {
            mUsername.setError("Please provide valid email address");
            return false;
        }

        if (password.length() < 6) {
            mPassword.setError("Password must be atleast 5 characters long");
            return false;
        }

        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void loginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loginFailed(String message) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit1) {
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
