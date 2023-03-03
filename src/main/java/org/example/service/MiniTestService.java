package org.example.service;

import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;

public interface MiniTestService {
    void crawMiniTest(String id);

    ListExamResponse getListMiniTestResponse(int page, int size);

    ListQuestionResponse getMiniTestById(String id);

    SubmitExamResponse submit(SubmitExamRequest request);
}
