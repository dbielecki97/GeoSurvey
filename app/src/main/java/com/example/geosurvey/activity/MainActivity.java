package com.example.geosurvey.activity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geosurvey.R;
import com.example.geosurvey.model.User;
import com.example.geosurvey.service.RetrofitService;
import com.example.geosurvey.service.UserService;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button registerButton;
    ImageButton backButton;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText emailEditText;
    LinearLayout registerFields;
    View.OnClickListener goToRegisterListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        usernameEditText = findViewById(R.id.credential_login);
        passwordEditText = findViewById(R.id.credential_password);
        emailEditText = findViewById(R.id.email);
        registerFields = findViewById(R.id.register_fields);
        backButton = findViewById(R.id.back_button);
        ((ViewGroup) findViewById(R.id.landing_root)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);



        View.OnClickListener register = v -> register(usernameEditText.getText().toString(), passwordEditText.getText().toString(), emailEditText.getText().toString());

        loginButton.setOnClickListener(v -> login(usernameEditText.getText().toString(), passwordEditText.getText().toString()));


        View.OnClickListener backListener = v -> {
            toggleVisibility(registerFields);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.animate()
                    .alpha(1f)
                    .translationYBy(15)
                    .setDuration(250);
            backButton.animate()
                    .alpha(0f)
                    .setDuration(250);
            backButton.setOnClickListener(null);
            registerButton.setOnClickListener(goToRegisterListener);
        };

        goToRegisterListener = v -> {
            toggleVisibility(registerFields);
            loginButton.animate()
                    .alpha(0f)
                    .translationYBy(-15)
                    .setDuration(250);
            backButton.setOnClickListener(backListener);
            backButton.animate()
                    .alpha(1f)
                    .setDuration(250);
            registerButton.setOnClickListener(register);
        };


        registerButton.setOnClickListener(goToRegisterListener);
        backButton.setOnClickListener(backListener);

    }

    private void toggleVisibility(View v) {
        if (v.getVisibility() == View.VISIBLE) v.setVisibility(View.GONE);
        else v.setVisibility(View.VISIBLE);
    }


    private void register(String username, String password, String email) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService();
        UserService userService = retrofit.create(UserService.class);
        Call<User> registerApiCall = userService.registerUser(new User(username, password, email));
        registerApiCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.landing_root), getString(R.string.register_success), Snackbar.LENGTH_LONG).show();
                    toggleVisibility(registerFields);
                    toggleVisibility(loginButton);
                    toggleVisibility(backButton);
                    registerButton.setOnClickListener(goToRegisterListener);
                } else if (response.code() == HttpURLConnection.HTTP_CONFLICT)
                    Snackbar.make(findViewById(R.id.landing_root), String.format(getString(R.string.register_conflict), username), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Snackbar.make(findViewById(R.id.landing_root), getString(R.string.fail), Snackbar.LENGTH_LONG).show();
            }


        });
    }

    private void login(String username, String password) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService();
        UserService userService = retrofit.create(UserService.class);
        Call<User> registerApiCall = userService.login(new User(username, password));
        registerApiCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.landing_root), getString(R.string.register_success), Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, TabActivity.class);
                    User user = response.body();
                    if (user != null) {
                        user.setPassword(password);
                    }
                    intent.putExtra("user", user);
                    startActivity(intent);
                } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN || response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    Snackbar.make(findViewById(R.id.landing_root), getString(R.string.incorect_credentials), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Snackbar.make(findViewById(R.id.landing_root), getString(R.string.fail), Snackbar.LENGTH_LONG).show();
            }

        });
    }
}
