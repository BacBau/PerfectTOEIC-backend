package org.example.controller;

import org.example.auth.AuthoritiesConstants;
import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.service.MiniTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/minitest")
public class MiniTestController {
    @Autowired
    MiniTestService miniTestService;

    @GetMapping("/craw")
    public void crawMiniTest(@RequestParam String id) {
        miniTestService.crawMiniTest(id);
    }

    @GetMapping
    public ListExamResponse getListMiniTest() {
        return miniTestService.getListMiniTestResponse();
    }

    @GetMapping("/{id}")
    public ListQuestionResponse getMiniTestById(@PathVariable("id") String id) {
        return miniTestService.getMiniTestById(id);
    }

    @PostMapping("/submit")
    public SubmitExamResponse submit(@RequestBody SubmitExamRequest request) {
        return miniTestService.submit(request);
    }
}
