package org.example.service.impl;

import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.service.MiniTestService;
import org.springframework.stereotype.Service;


@Service
public class MiniTestServiceImpl implements MiniTestService {
    @Override
    public void crawMiniTest(String id) {

    }

    @Override
    public ListExamResponse getListMiniTestResponse(int page, int size) {
        return null;
    }

    @Override
    public ListQuestionResponse getMiniTestById(String id) {
        return null;
    }

    @Override
    public SubmitExamResponse submit(SubmitExamRequest request) {
        return null;
    }
}
