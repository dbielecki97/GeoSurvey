package com.example.geosurvey.service;

import com.example.geosurvey.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface QuestionService {
    @GET("user/questions")
    Call<List<Question>> getUsersQuestions();

    @POST("questions")
    Call<Question> create(@Body Question question);
}
