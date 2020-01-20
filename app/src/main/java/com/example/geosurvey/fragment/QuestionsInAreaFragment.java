package com.example.geosurvey.fragment;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.geosurvey.R;
import com.example.geosurvey.model.Answer;
import com.example.geosurvey.model.Question;
import com.example.geosurvey.model.User;
import com.example.geosurvey.service.AnswerService;
import com.example.geosurvey.service.QuestionService;
import com.example.geosurvey.service.RetrofitService;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QuestionsInAreaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View rootView;
    private User user;
    private QuestionAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public QuestionsInAreaFragment() {
        // Required empty public constructor
    }

    public static QuestionsInAreaFragment newInstance() {
        return new QuestionsInAreaFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_questions_in_area, container, false);
        user = Objects.requireNonNull(getActivity()).getIntent().getParcelableExtra("user");
        RecyclerView recyclerView = rootView.findViewById(R.id.in_area_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new QuestionAdapter();
        recyclerView.setAdapter(adapter);

        locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        ((ViewGroup) rootView.findViewById(R.id.in_area_root)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);
        mSwipeRefreshLayout = rootView.findViewById(R.id.in_area_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(() -> {

            mSwipeRefreshLayout.setRefreshing(true);
            fetchQuestionsInArea(user.getUsername(), user.getPassword());
        });


        return rootView;
    }


    @Override
    public void onRefresh() {
        fetchQuestionsInArea(user.getUsername(), user.getPassword());
    }

    private void fetchQuestionsInArea(String username, String password) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService(username, password);
        QuestionService questionService = retrofit.create(QuestionService.class);
        try {
            if (locationManager != null) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 5000, 1, locationListener);


                    Call<List<Question>> getQuestionsInAreCall = questionService
                            .getQuestionsInArea(latitude, longitude);
                    getQuestionsInAreCall.enqueue(new Callback<List<Question>>() {

                        @Override
                        public void onResponse(@NotNull Call<List<Question>> call, @NotNull Response<List<Question>> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().size() > 0)
                                    setupQuestionListView(response.body());
                                else
                                    Snackbar.make(rootView.findViewById(R.id.in_area_root), "Nie ma żadnych pytań w twojej okolicy!", Snackbar.LENGTH_LONG).show();

                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(@NotNull Call<List<Question>> call, @NotNull Throwable t) {
                            Snackbar.make(rootView.findViewById(R.id.in_area_root), getString(R.string.fail), Snackbar.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }

    }

    private void commitAnswer(String username, String password, Question question, Long answerId, int adapterPosition) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService(username, password);
        AnswerService answerService = retrofit.create(AnswerService.class);
        Call<Answer> getQuestionsInAreCall = answerService
                .sendAnswer(question.getId(), answerId);
        getQuestionsInAreCall.enqueue(new Callback<Answer>() {

            @Override
            public void onResponse(@NotNull Call<Answer> call, @NotNull Response<Answer> response) {
                if (response.isSuccessful()) {
                    adapter.getQuestions().remove(adapterPosition);
                    adapter.notifyItemRemoved(adapterPosition);
                    adapter.notifyItemRangeChanged(adapterPosition, 1);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Answer> call, @NotNull Throwable t) {
                Snackbar.make(rootView.findViewById(R.id.in_area_root), getString(R.string.fail), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void setupQuestionListView(List<Question> questions) {
        adapter.setQuestions(questions);
        adapter.notifyDataSetChanged();
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> {
        private List<Question> questions;

        public QuestionAdapter() {
            super();
            setHasStableIds(true);
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        @NonNull
        @Override
        public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
            if (questions.size() > 0) {
                Question question = questions.get(position);
                holder.bind(question);
            }
        }

        @Override
        public int getItemCount() {
            if (questions != null)
                return questions.size();
            return 0;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    private class QuestionHolder extends RecyclerView.ViewHolder {
        private TextView questionTitle;
        private TextView questionContent;
        private TextView createdAt;
        private RadioGroup answers;
        private Question question;
        private AppCompatButton sendAnswer;

        public QuestionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.in_area_list_item, parent, false));
            questionTitle = itemView.findViewById(R.id.in_area_question_title);
            questionContent = itemView.findViewById(R.id.in_area_question_content);
            createdAt = itemView.findViewById(R.id.in_area_created_at);
            answers = itemView.findViewById(R.id.in_area_question_answers);
            sendAnswer = itemView.findViewById(R.id.send_answer);
            sendAnswer.setEnabled(false);

            answers.setOnCheckedChangeListener((v, x) ->
                    sendAnswer.setEnabled(true));


            sendAnswer.setOnClickListener(v -> {
                RadioButton btn = itemView.findViewById(answers.getCheckedRadioButtonId());
                commitAnswer(
                        user.getUsername(),
                        user.getPassword(),
                        question, Long.valueOf(btn.getContentDescription().toString()), getAdapterPosition());
            });
        }


        public void bind(Question question) {
            this.question = question;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.ENGLISH);
            createdAt.setText(simpleDateFormat.format(question.getCreatedAt()));
            questionTitle.setText(question.getTitle());
            questionContent.setText(question.getContent());
            sendAnswer.setEnabled(false);

            answers.removeAllViews();
            question.getAnswers().forEach(answer -> {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(answer.getText());
                radioButton.setContentDescription(Long.toString(answer.getId()));
                answers.addView(radioButton);
            });
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
