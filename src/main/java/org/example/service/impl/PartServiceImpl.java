package org.example.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.example.http.HttpSender;
import org.example.model.entity.Question;
import org.example.model.entity.TestPart;
import org.example.model.request.CrawExamRequest;
import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.QuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.repository.PartRepository;
import org.example.repository.QuestionRepository;
import org.example.service.PartService;
import org.example.service.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.service.impl.ExamServiceImpl.jsonElementToObject;

@Service
public class PartServiceImpl implements PartService {

    @Autowired
    PartRepository partRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionMapper questionMapper;

    @Override
    public void crawPart(String id, int partNumber) {
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
        TestPart part = new TestPart();
        part.setCreatedBy("admin");
        part.setPublicId(id);
        part.setPart(partNumber);
        partRepository.save(part);
        questionRepository.saveAll(questionList);
    }

    @Override
    public ListExamResponse getListPartResponse(int part, int page, int size) {
        if (page < 0 || size < 0) return null;
        List<String> partId = partRepository.findAllPartIdByPartNumber(part);
        return ListExamResponse.builder().id(partId).build();
    }

    @Override
    public ListQuestionResponse getPartById(String id) {
        Optional<TestPart> part = partRepository.findById(id);
        if (part.isEmpty()) return null;
        List<Question> questionList = questionRepository.findAllByParentIdOrderByQuestionNumber(part.get().getPublicId());
        List<QuestionResponse> questionResponses = new ArrayList<>();
        for (Question question : questionList) {
            questionResponses.add(questionMapper.questionToQuestionResponse(question));
        }
        return ListQuestionResponse.builder().questionList(questionResponses).build();
    }

    @Override
    public SubmitExamResponse submit(SubmitExamRequest request) {
        return null;
    }
}
