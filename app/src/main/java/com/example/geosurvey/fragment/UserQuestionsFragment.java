package com.example.geosurvey.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosurvey.R;
import com.example.geosurvey.activity.CreateQuestionActivity;
import com.example.geosurvey.model.Question;
import com.example.geosurvey.model.User;
import com.example.geosurvey.service.QuestionService;
import com.example.geosurvey.service.RetrofitService;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserQuestionsFragment extends Fragment {
    private View rootView;
    private User user;
    private RecyclerView recyclerView;


    public UserQuestionsFragment() {
    }

    public static Fragment newInstance() {
        return new UserQuestionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.question_list, container, false);

        Intent intent = Objects.requireNonNull(getActivity()).getIntent();
        user = intent.getParcelableExtra("user");
        recyclerView = rootView.findViewById(R.id.recyclerview);
        Button newQuestion = rootView.findViewById(R.id.new_question);

        newQuestion.setOnClickListener(v -> {
            Intent newQuestionIntent = new Intent(getActivity(), CreateQuestionActivity.class);
            newQuestionIntent.putExtra("user", user);
            startActivity(newQuestionIntent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            fetchUserQuestionData(user.getUsername(), user.getPassword());
        }
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> {
        public Set<Question> getQuestions() {
            return questions;
        }

        private final Set<Question> questions = new LinkedHashSet<>();

        @NonNull
        @Override
        public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
            if (questions.size() > 0) {
                List<Question> questionList = new ArrayList<>(questions);
                Question question = questionList.get(position);
                holder.bind(question);
            }
        }

        @Override
        public int getItemCount() {
            if (questions.size() > 0)
                return questions.size();
            return 0;
        }

    }

    private class QuestionHolder extends RecyclerView.ViewHolder {
        TextView questionTitle;
        TextView questionContent;
        TextView createdAt;
        RadioGroup answers;

        public QuestionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.question_list_item, parent, false));
            questionTitle = itemView.findViewById(R.id.question_title);
            questionContent = itemView.findViewById(R.id.question_content);
            createdAt = itemView.findViewById(R.id.question_createdAt);
            answers = itemView.findViewById(R.id.question_answers);
        }

        public void bind(Question question) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            createdAt.setText(simpleDateFormat.format(question.getCreatedAt()));
            questionTitle.setText(question.getTitle());
            questionContent.setText(question.getContent());
            question.getAnswers().forEach(answer -> {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(answer.getText());
                answers.addView(radioButton);
            });
        }

    }

    private void fetchUserQuestionData(String username, String password) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService(username, password);
        QuestionService questionService = retrofit.create(QuestionService.class);
        Call<List<Question>> booksApiCall = questionService.getUsersQuestions();
        booksApiCall.enqueue(new Callback<List<Question>>() {

            @Override
            public void onResponse(@NotNull Call<List<Question>> call, @NotNull Response<List<Question>> response) {
                if (response.body() != null) {
                    setupQuestionListView(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Question>> call, @NotNull Throwable t) {
                Snackbar.make(rootView.findViewById(R.id.main_view), "Something went wrong... Please try later!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupQuestionListView(List<Question> questions) {
        QuestionAdapter adapter = new QuestionAdapter();
        questions.forEach(question -> adapter.getQuestions().add(question));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
