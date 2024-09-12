package br.com.academicquiz.theme.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.academicquiz.theme.models.ThemeModel;

public interface ThemeRepository extends JpaRepository<ThemeModel, Long> {
    ThemeModel findByName(String name);
}
