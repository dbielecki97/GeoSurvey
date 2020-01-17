package com.example.geosurvey.service;

import com.example.geosurvey.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuestionService {
    @GET("user/questions")
    Call<List<Question>> getUsersQuestions();
}
