package org.example.service;

import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;

public interface PartService {
    void crawPart(String id, int partNumber);

    ListExamResponse getListPartResponse(int part, int page, int size);

    ListQuestionResponse getPartById(String id);

    SubmitExamResponse submit(SubmitExamRequest request);
}
