package com.example.geosurvey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosurvey.model.Question;
import com.example.geosurvey.service.QuestionService;
import com.example.geosurvey.service.RetrofitService;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    private QuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new QuestionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchUserQuestionData();
    }

    private void fetchUserQuestionData() {
        Retrofit retrofit;
        retrofit = RetrofitService.createService("dawid", "123");
        QuestionService questionService = retrofit.create(QuestionService.class);
        Call<List<Question>> booksApiCall = questionService.getUsersQuestions();
        booksApiCall.enqueue(new Callback<List<Question>>() {

            @Override
            public void onResponse(@NotNull Call<List<Question>> call, @NotNull Response<List<Question>> response) {
                setupQuestionListView(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<List<Question>> call, @NotNull Throwable t) {
                Snackbar.make(findViewById(R.id.main_view), "Something went wrong... Please try later!", Snackbar.LENGTH_LONG).show();
                System.out.println(t.getMessage());
                System.out.println(t.getLocalizedMessage());
                System.out.println(t.toString());
            }
        });
    }

    private void setupQuestionListView(List<Question> questions) {
        adapter.setQuestions(questions);
        adapter.notifyDataSetChanged();
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> {
        private List<Question> questions;

        @NonNull
        @Override
        public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
            if (questions != null) {
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

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }
    }

    private class QuestionHolder extends RecyclerView.ViewHolder {
        TextView questionTitle;
        TextView questionContent;

        public QuestionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.question_list_item, parent, false));
            questionTitle = itemView.findViewById(R.id.question_title);
            questionContent = itemView.findViewById(R.id.question_content);

        }

        public void bind(Question question) {

            questionTitle.setText(question.getTitle());
            questionContent.setText(question.getContent());
        }

    }
}
