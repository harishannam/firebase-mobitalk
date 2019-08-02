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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private TextView mLikeCount;
    private int mLikes;
    private int count = 0;

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

        setContentView(R.layout.activity_main);

        mLikes = new Random().nextInt(10);
        mLikeCount = findViewById(R.id.like_count);
        mLikeCount.setText("(" + mLikes + ")");
        findViewById(R.id.thumb_image).setOnClickListener(this);
       /* mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
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
        findViewById(R.id.loginbtn).setOnClickListener(this);*/
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
            case R.id.thumb_image:
                count++;
                mLikes++;
                mLikeCount.setText("(" + mLikes + ")");
                if (count == 4) {
                    throw new IllegalArgumentException("Firebase throws error!");
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutBtn) {
            doFirebaseLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doFirebaseLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
