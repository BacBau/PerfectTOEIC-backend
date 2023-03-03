package org.example.controller;

import org.example.model.request.SubmitExamRequest;
import org.example.model.response.ListExamResponse;
import org.example.model.response.ListQuestionResponse;
import org.example.model.response.SubmitExamResponse;
import org.example.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/part")
public class PartController {
    @Autowired
    PartService partService;

    @GetMapping("/craw")
    public void crawPart(@RequestParam String id, @RequestParam int partNumber) {
        partService.crawPart(id, partNumber);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ListExamResponse getListPart(@RequestParam int part, @RequestParam int page, @RequestParam int size) {
        return partService.getListPartResponse(part, page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ListQuestionResponse getPartById(@PathVariable("id") String id) {
        return partService.getPartById(id);
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SubmitExamResponse submit(@RequestBody SubmitExamRequest request) {
        return partService.submit(request);
    }
}
