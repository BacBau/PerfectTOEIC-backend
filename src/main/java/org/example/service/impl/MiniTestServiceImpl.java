package org.example.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.example.exception.EnglishExamException;
import org.example.http.HttpSender;
import org.example.model.entity.Exam;
import org.example.model.entity.MiniTest;
import org.example.model.entity.Question;
import org.example.model.error.ErrorCode;
import org.example.model.request.CrawExamRequest;
import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.QuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.repository.MiniTestRepository;
import org.example.repository.QuestionRepository;
import org.example.service.MiniTestService;
import org.example.service.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.example.service.impl.ExamServiceImpl.jsonElementToObject;

@Service
public class MiniTestServiceImpl implements MiniTestService {

    private final MiniTestRepository miniTestRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;


    public MiniTestServiceImpl(MiniTestRepository miniTestRepository,
                               QuestionRepository questionRepository,
                               QuestionMapper questionMapper) {
        this.miniTestRepository = miniTestRepository;
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }
    @Override
    public void crawMiniTest(String id) {
        HttpSender sender = new HttpSender();
        CrawExamRequest request = new CrawExamRequest();
        request.setTopicId(id);
        JsonArray response = sender.postJson3("https://estudyme.test-toeic.online/api/get-card-by-topic-id", null, new Gson().toJsonTree(request).toString());
        List<Question> questionList = new ArrayList<>();
        int i = 1;
        for (JsonElement element : response) {
            element.getAsJsonObject().addProperty("questionNumber", i);
            questionList.add(jsonElementToObject(element, id));
            for (JsonElement element1 : element.getAsJsonObject().getAsJsonArray("childCards")) {
                element1.getAsJsonObject().addProperty("questionNumber", i++);
                questionList.add(jsonElementToObject(element1, id));
            }
            if (element.getAsJsonObject().get("hasChild").getAsInt() == 0) {
                i++;
            }
        }
        MiniTest miniTest = new MiniTest();
        miniTest.setCreatedBy("admin");
        miniTest.setPublicId(id);
        miniTestRepository.save(miniTest);
        questionRepository.saveAll(questionList);
    }

    @Override
    public ListExamResponse getListMiniTestResponse() {
        List<String> all = miniTestRepository.getAllPublicId();
        return ListExamResponse.builder().id(all).build();
    }

    @Override
    public ListQuestionResponse getMiniTestById(String id) {
        MiniTest miniTest = miniTestRepository.findById(id).orElseThrow(() ->
                new EnglishExamException(ErrorCode.MINI_TEST_NOT_FOUND));
        List<Question> questionList = questionRepository.findAllByExamIdOrderByQuestionNumber(miniTest.getPublicId());
        List<QuestionResponse> questionResponses = questionList.stream()
                .map(question -> questionMapper.questionToQuestionResponse(question))
                .collect(Collectors.toList());
        return ListQuestionResponse.builder().questionList(questionResponses).build();
    }

    @Override
    public SubmitExamResponse submit(SubmitExamRequest request) {
        return null;
    }
}
