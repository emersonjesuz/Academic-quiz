package br.com.academicquiz.question.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.academicquiz.alternatives.dtos.AlternativeRequestDto.AlternativeRequest;
import br.com.academicquiz.alternatives.dtos.AlternativeRequestDto.AlternativeRequestUpdate;
import br.com.academicquiz.alternatives.models.AlternativesModel;
import br.com.academicquiz.alternatives.services.AlternativeService;
import br.com.academicquiz.alternatives.utils.AlternativeStructore;
import br.com.academicquiz.helpers.ErrorMessage;
import br.com.academicquiz.question.dtos.QuestionRequestDto.QuestionRequest;
import br.com.academicquiz.question.dtos.QuestionRequestDto.QuestionRequestUpdate;
import br.com.academicquiz.question.models.QuestionModel;
import br.com.academicquiz.question.services.QuestionService;
import br.com.academicquiz.question.utils.QuestionStructure;
import br.com.academicquiz.theme.models.ThemeModel;
import br.com.academicquiz.theme.services.ThemeService;

@RestController()
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private QuestionService questionService;

    @PostMapping()
    public ResponseEntity<Object> createQuestion(@RequestBody QuestionRequest request) {
        try {

            if (request.getStatement() == null || request.getStatement().trim().isEmpty()) {
                return ResponseEntity.status(400).body(new ErrorMessage("Statement cannot be empty"));
            }

            if (request.getThemeId() == null) {
                return ResponseEntity.status(400).body(new ErrorMessage("Theme id cannot be empty"));
            }

            if (request.getAlternatives().isEmpty()) {
                return ResponseEntity.status(400).body(new ErrorMessage("Alternatives cannot be empty"));
            }

            ThemeModel theme = themeService.getThemeId(request.getThemeId());
            if (theme == null) {
                return ResponseEntity.status(404).body(new ErrorMessage("Theme not found"));
            }

            int countIsCorrect = 0;
            for (AlternativeRequest alternative : request.getAlternatives()) {
                if (alternative.getDescription().trim().isEmpty()) {
                    ResponseEntity.status(400).body(new ErrorMessage("Alternative cannot be empty"));
                    break;
                }

                if (alternative.getIsCorrect() == false) {
                    countIsCorrect++;
                }
            }

            if (countIsCorrect == request.getAlternatives().size()) {
                return ResponseEntity.status(400)
                        .body(new ErrorMessage("There must be at least one correct alternative"));
            }

            QuestionModel createdQuestion = this.questionService.createQuestion(theme, request.getStatement());

            for (AlternativeRequest alternative : request.getAlternatives()) {
                alternativeService.createAlternative(createdQuestion, alternative.getDescription(),
                        alternative.getIsCorrect());
            }
            return ResponseEntity.status(200).body(new ErrorMessage("Question created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getQuestion(@PathVariable Long id) {
        try {
            if (id == null) {
                return ResponseEntity.status(400).body(new ErrorMessage("obrigatory question"));
            }

            QuestionModel question = questionService.getQuestion(id);

            if (question == null) {
                return ResponseEntity.status(404).body(new ErrorMessage("Question not found"));
            }

            List<AlternativeStructore> alternativesStructores = question.getAlternatives().stream()
                    .map(alternative -> {
                        return new AlternativeStructore(alternative.getId(),
                                alternative.getDescription(), alternative.getQuestion().getId());
                    }).toList();

            QuestionStructure questionStructure = new QuestionStructure(
                    question.getId(), question.getStatement(), question.getTheme().getId(), alternativesStructores);

            return ResponseEntity.status(200).body(questionStructure);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateQuestion(@PathVariable Long id, @RequestBody QuestionRequestUpdate request) {

        try {

            if (request.getStatement() == null || request.getStatement().trim().isEmpty()) {
                return ResponseEntity.status(400).body(new ErrorMessage("Statement cannot be empty"));
            }

            if (request.getAlternatives().isEmpty()) {
                return ResponseEntity.status(400).body(new ErrorMessage("Alternatives cannot be empty"));
            }

            QuestionModel question = questionService.getQuestion(id);
            if (question == null) {
                return ResponseEntity.status(404).body(new ErrorMessage("Question not found"));
            }

            List<AlternativesModel> saveValidAlternative = new ArrayList<>();
            int countIsCorrect = 0;

            for (AlternativeRequestUpdate alternative : request.getAlternatives()) {

                if (alternative.getDescription().trim().isEmpty()) {
                    ResponseEntity.status(400).body(new ErrorMessage("Alternative cannot be empty"));
                    break;
                }

                AlternativesModel verifyAlterativeExist = question.getAlternatives().stream()
                        .filter(a -> a.getId().equals(alternative.getId())).findFirst().orElse(null);

                if (verifyAlterativeExist == null) {
                    ResponseEntity.status(404).body(new ErrorMessage("Alternative not found"));
                    break;
                }

                verifyAlterativeExist.setDescription(alternative.getDescription());
                verifyAlterativeExist.setIsCorrect(alternative.getIsCorrect());
                saveValidAlternative.add(verifyAlterativeExist);

                if (alternative.getIsCorrect() == false) {
                    countIsCorrect++;
                }
            }

            if (countIsCorrect == request.getAlternatives().size()) {
                return ResponseEntity.status(400)
                        .body(new ErrorMessage("There must be at least one correct alternative"));
            }

            if (saveValidAlternative.size() != request.getAlternatives().size()) {
                return ResponseEntity.status(400).body(new ErrorMessage("Alternative not found"));
            }

            saveValidAlternative.forEach(alternative -> {
                alternativeService.update(alternative);
            });

            question.setStatement(request.getStatement());
            questionService.updateQuestion(question);
            return ResponseEntity.status(200).body(new ErrorMessage("Question updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteQuestion(@PathVariable Long id) {
        try {
            if (id == null) {
                return ResponseEntity.status(400).body(new ErrorMessage("obrigatory question"));
            }

            QuestionModel question = questionService.getQuestion(id);

            if (question == null) {
                return ResponseEntity.status(404).body(new ErrorMessage("Question not found"));
            }
            questionService.deleteQuestion(question);
            return ResponseEntity.status(200).body(new ErrorMessage("Question deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorMessage(e.getMessage()));
        }
    }
}
