package com.example.geosurvey.service;

import com.example.geosurvey.model.Answer;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AnswerService {
    @PUT("/questions/{questionId}/answers/vote/{answerId}")
    Call<Answer> sendAnswer(@Path("questionId") Long questionId, @Path("answerId") Long answerId);
}
