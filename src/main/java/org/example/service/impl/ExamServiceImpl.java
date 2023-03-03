package org.example.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.example.config.Constants;
import org.example.http.HttpSender;
import org.example.minio.MinioAdapter;
import org.example.model.entity.Exam;
import org.example.model.entity.Question;
import org.example.model.entity.UserExamStatus;
import org.example.model.request.CrawExamRequest;
import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.QuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.repository.ExamRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.UserExamStatusRepository;
import org.example.service.ExamService;
import org.example.service.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    MinioAdapter minioAdapter;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ExamRepository examRepository;

    @Autowired
    UserExamStatusRepository userExamStatusRepository;

    @Autowired
    QuestionMapper questionMapper;

    public static Question jsonElementToObject(JsonElement element, String examId) {
        Question questionEntity = new Question();
        JsonObject question = element.getAsJsonObject().getAsJsonObject("question");
        String text = question.get("text").getAsString();
        String image = "https://storage.googleapis.com/" + question.get("image").getAsString();
        String sound = question.get("sound").getAsString();
        JsonObject answer = element.getAsJsonObject().getAsJsonObject("answer");
        StringBuilder rs = new StringBuilder();
        answer.getAsJsonArray("texts").forEach(e -> rs.append(e.getAsString()).append("|"));
        answer.getAsJsonArray("choices").forEach(e -> rs.append(e.getAsString()).append("|"));
        questionEntity.setAnswer(rs.toString());
        questionEntity.setHint(answer.get("hint").getAsString());
        questionEntity.setText(text);
        questionEntity.setImage(image);
        questionEntity.setSound(sound);
        questionEntity.setParentId(element.getAsJsonObject().get("parentId").getAsString());
        questionEntity.setHasChild(element.getAsJsonObject().get("hasChild").getAsInt());
        questionEntity.setPublicId(element.getAsJsonObject().get("_id").getAsString());
        questionEntity.setExamId(examId);
        questionEntity.setQuestionNumber(element.getAsJsonObject().get("questionNumber").getAsInt());
        //for db
        questionEntity.setCreatedBy("admin");
        return questionEntity;
    }

    @Override
    public void crawExam(String id) {
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
        Exam exam = new Exam();
        exam.setCreatedBy("admin");
        exam.setPublicId(id);
        examRepository.save(exam);
        questionRepository.saveAll(questionList);
    }

    @Override
    public ListExamResponse getListExamResponse(int page, int size) {
        if (page < 0 || size < 0) return null;
        List<String> examIds = examRepository.findAllExamId();
        return ListExamResponse.builder().id(examIds).build();
    }

    @Override
    public ListQuestionResponse getExamById(String id) {
        Optional<Exam> exam = examRepository.findById(id);
        if (exam.isEmpty()) return null;
        List<Question> questionList = questionRepository.findAllByParentIdOrderByQuestionNumber(exam.get().getPublicId());
        List<QuestionResponse> questionResponses = new ArrayList<>();
        for (Question question : questionList) {
            questionResponses.add(questionMapper.questionToQuestionResponse(question));
        }
        return ListQuestionResponse.builder().questionList(questionResponses).build();
    }

    @Override
    public SubmitExamResponse submit(SubmitExamRequest request) {
        UserExamStatus userExamStatus = new UserExamStatus();
        userExamStatus.setUserId(Constants.getCurrentUser());
        userExamStatus.setExamId(request.getExamId());
        //calculate number
        userExamStatusRepository.save(userExamStatus);
        return null;
    }
}
