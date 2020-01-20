package com.example.geosurvey.activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.geosurvey.R;
import com.example.geosurvey.model.Question;
import com.example.geosurvey.model.User;
import com.example.geosurvey.service.QuestionService;
import com.example.geosurvey.service.RetrofitService;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateQuestionActivity extends AppCompatActivity {
    private EditText title;
    private EditText content;
    private EditText radius;
    private LinearLayout answers;
    private ImageButton remove_answer;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_question);
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");
        title = findViewById(R.id.question_create_title);
        content = findViewById(R.id.question_create_content);
        radius = findViewById(R.id.question_create_radius);
        answers = findViewById(R.id.question_create_answers);
        ImageButton add_answer = findViewById(R.id.add_answer);
        remove_answer = findViewById(R.id.remove_answer);
        Button create_question = findViewById(R.id.create_question);

        ((ViewGroup) findViewById(R.id.create_question_root)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);
        for (int i = 0; i < answers.getChildCount(); i++) {
            ((TextInputLayout) answers.getChildAt(i)).setHint(String.format(getString(R.string.question_answer_hint), i + 1));
        }

        create_question.setOnClickListener(v -> {
            String titleString = title.getText().toString();
            String contentString = content.getText().toString();
            double radiusDouble = Double.valueOf(radius.getText().toString());
            List<String> answerStrings = new ArrayList<>();
            for (int i = 0; i < answers.getChildCount(); i++) {
                TextInputLayout textInputLayout = (TextInputLayout) answers.getChildAt(i);
                TextInputEditText textInputEditText = (TextInputEditText) textInputLayout.getEditText();
                if (textInputEditText != null) {
                    answerStrings.add(Objects.requireNonNull(textInputEditText.getText()).toString());
                }
            }
            assert user != null;
            create(user.getUsername(), user.getPassword(), titleString, contentString, radiusDouble, answerStrings);
        });

        add_answer.setOnClickListener(v -> {
            TextInputLayout answer_layout = new TextInputLayout(this);
            answer_layout.setHint(String.format(getString(R.string.question_answer_hint), answers.getChildCount() + 1));
            answer_layout.addView(new TextInputEditText(this));
            answers.addView(answer_layout);
            remove_answer.animate()
                    .alpha(1f)
                    .setDuration(250);
        });

        remove_answer.setOnClickListener(v -> {
            if (answers.getChildCount() > 2)
                answers.removeViewAt(answers.getChildCount() - 1);
            if (answers.getChildCount() == 2) remove_answer.animate()
                    .alpha(0.0f)
                    .setDuration(250);
        });

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

    }


    private void create(String username, String password, String title, String content, double radius, List<String> answers) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService(username, password);
        QuestionService questionService = retrofit.create(QuestionService.class);
        try {
            if (locationManager != null) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
                    Call<Question> createApiCall = questionService
                            .create(new Question(title, content, radius, answers, latitude, longitude));
                    createApiCall.enqueue(new Callback<Question>() {

                        @Override
                        public void onResponse(@NotNull Call<Question> call, @NotNull Response<Question> response) {
                            if (response.isSuccessful()) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("QUESTION", response.body());
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<Question> call, @NotNull Throwable t) {
                            Snackbar.make(findViewById(R.id.landing_root), getString(R.string.fail), Snackbar.LENGTH_LONG).show();
                        }


                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
                }
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }

    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}


