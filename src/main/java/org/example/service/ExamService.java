package org.example.service;

import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;

public interface ExamService {
    void crawExam(String id);

    ListExamResponse getListExamResponse(int page, int size);

    ListQuestionResponse getExamById(String id);

    SubmitExamResponse submit(SubmitExamRequest request);
}
