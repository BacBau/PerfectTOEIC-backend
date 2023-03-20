package org.example.service;

import org.example.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CrawJob {
    private final PartService partService;
    private final QuestionRepository questionRepository;
    private final ExamService examService;
    private final MiniTestService miniTestService;

    public CrawJob(PartService partService,
                   QuestionRepository questionRepository,
                   ExamService examService,
                   MiniTestService miniTestService) {
        this.partService = partService;
        this.questionRepository = questionRepository;
        this.examService = examService;
        this.miniTestService = miniTestService;
    }

    @PostConstruct
    public void crawAllDataPart() {
        boolean isCraw = true;

        if (questionRepository.countAll() != 0) return;
        partService.crawPart("614be60365d71f3a51f67196", 1);
        partService.crawPart("614be60765d71f3a51f67197", 1);
        partService.crawPart("614be60d65d71f3a51f67198", 1);
        partService.crawPart("614be61765d71f3a51f67199", 1);
        partService.crawPart("614be61f65d71f3a51f6719a", 1);
        partService.crawPart("614be62465d71f3a51f6719b", 1);
        partService.crawPart("614be62965d71f3a51f6719c", 1);
        partService.crawPart("62e9f9865948b5402da82368", 1);
        partService.crawPart("614be63565d71f3a51f6719e", 1);
        partService.crawPart("614be60365d71f3a51f67196", 2);
        partService.crawPart("614be60765d71f3a51f67197", 2);
        partService.crawPart("614be60d65d71f3a51f67198", 2);
        partService.crawPart("614be61765d71f3a51f67199", 2);
        examService.crawExam("62b69492bbc57b27fe10f7ac");
        examService.crawExam("62b6b89bbbc57b27fe10fc20");
        examService.crawExam("62b6be4fbbc57b27fe10fee2");
        miniTestService.crawMiniTest("63b7e86fe1ae0763714f09cc");
        miniTestService.crawMiniTest("63b7e86fe1ae0763714f09cb");
        miniTestService.crawMiniTest("62b3dee2bbc57b27fe103b72");
        miniTestService.crawMiniTest("62b3e5c1bbc57b27fe103dbe");
        miniTestService.crawMiniTest("62b421a9bbc57b27fe1054c8");
        miniTestService.crawMiniTest("62b43624bbc57b27fe105bbf");
        miniTestService.crawMiniTest("62b516bfbbc57b27fe108f64");

    }
}
