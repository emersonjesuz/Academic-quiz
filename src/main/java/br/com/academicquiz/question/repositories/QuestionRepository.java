package br.com.academicquiz.question.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.academicquiz.question.models.QuestionModel;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionModel, Long> {
    // get all questions by theme id
    @Query("SELECT q FROM QuestionModel q WHERE q.theme.id = ?1")
    List<QuestionModel> findAll(Long themeId);
}
