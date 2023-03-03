package org.example.controller;

import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    ExamService examService;

    @GetMapping("/craw")
    public void crawExam(@RequestParam String id) {
        examService.crawExam(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ListExamResponse getListExam(@RequestParam int page, @RequestParam int size) {
        return examService.getListExamResponse(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ListQuestionResponse getExamById(@PathVariable("id") String id) {
        return examService.getExamById(id);
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SubmitExamResponse submit(@RequestBody SubmitExamRequest request) {
        return examService.submit(request);
    }
}
