package com.example.geosurvey.fragment;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.geosurvey.R;
import com.example.geosurvey.activity.CreateQuestionActivity;
import com.example.geosurvey.activity.QuestionDetailActivity;
import com.example.geosurvey.model.Answer;
import com.example.geosurvey.model.Question;
import com.example.geosurvey.model.User;
import com.example.geosurvey.service.QuestionService;
import com.example.geosurvey.service.RetrofitService;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class UserQuestionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int QUESTION = 0;
    private View rootView;
    private User user;
    private QuestionAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


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
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        Button newQuestion = rootView.findViewById(R.id.new_question);

        newQuestion.setOnClickListener(v -> {
            Intent newQuestionIntent = new Intent(getActivity(), CreateQuestionActivity.class);
            newQuestionIntent.putExtra("user", user);
            startActivityForResult(newQuestionIntent, QUESTION);
        });
        adapter = new QuestionAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((ViewGroup) rootView.findViewById(R.id.question_list_root)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        mSwipeRefreshLayout = rootView.findViewById(R.id.my_question_list_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            fetchUserQuestionData(user.getUsername(), user.getPassword());
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == QUESTION) {
            if (data != null) {
                Question q = data.getParcelableExtra("QUESTION");
                adapter.getQuestions().add(q);
                adapter.setQuestions(adapter.getQuestions().stream().sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt())).collect(Collectors.toCollection(LinkedList::new)));
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
            }

        }
    }

    @Override
    public void onRefresh() {
        fetchUserQuestionData(user.getUsername(), user.getPassword());
    }

    private void fetchUserQuestionData(String username, String password) {
        Retrofit retrofit;
        retrofit = RetrofitService.createService(username, password);
        QuestionService questionService = retrofit.create(QuestionService.class);
        Call<List<Question>> booksApiCall = questionService.getUsersQuestions();
        booksApiCall.enqueue(new Callback<List<Question>>() {

            @Override
            public void onResponse(@NotNull Call<List<Question>> call, @NotNull Response<List<Question>> response) {
                if (response.isSuccessful())
                    if (response.body() != null) {
                        setupQuestionListView(response.body());
                    }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NotNull Call<List<Question>> call, @NotNull Throwable t) {
                Snackbar.make(rootView.findViewById(R.id.question_list_root), "Something went wrong... Please try later!", Snackbar.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
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
                List<Question> questionList = new ArrayList<>(questions);
                Question question = questionList.get(position);
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

    private class QuestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView questionTitle;
        TextView createdAt;
        TextView votesCount;
        private Question question;

        public QuestionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.my_list_item, parent, false));
            questionTitle = itemView.findViewById(R.id.my_title);
            createdAt = itemView.findViewById(R.id.my_created_at);
            votesCount = itemView.findViewById(R.id.my_votes_count);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Question question) {
            this.question = question;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.ENGLISH);
            createdAt.setText(simpleDateFormat.format(question.getCreatedAt()));
            questionTitle.setText(question.getTitle());
            int votes = question.getAnswers().stream().mapToInt(Answer::getCount).sum();
            votesCount.setText(String.format(Locale.ENGLISH, getString(R.string.number_of_votes), votes));

        }

        @Override
        public void onClick(View v) {
            Intent questionDetailIntent = new Intent(getActivity(), QuestionDetailActivity.class);
            questionDetailIntent.putExtra("QUESTION", question);
            startActivity(questionDetailIntent);
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("messege")
                    .setTitle("title");
            builder.setPositiveButton("ok", (dialog, id) -> {
                //TODO usuwanie z bazy
                adapter.getQuestions().remove(getAdapterPosition());
                adapter.notifyItemRemoved(getAdapterPosition());
                adapter.notifyItemRangeChanged(getAdapterPosition(), 1);
                Snackbar.make(
                        rootView.findViewById(R.id.question_list_root),
                        "Pytanie zostało usunięte",
                        Snackbar.LENGTH_LONG).show();
            });
            builder.setNeutralButton("Anuluj", (dialog, id) -> {
                // User cancelled the dialog
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }
}
