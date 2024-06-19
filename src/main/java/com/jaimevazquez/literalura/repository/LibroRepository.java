package com.jaimevazquez.literalura.repository;

import com.jaimevazquez.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String tituloLibro);
    List<Libro> findAll();
    @Query(value = "SELECT * FROM libros WHERE :idioma = ANY (libros.idioma)", nativeQuery = true)
    List<Libro> listarLibrosPorIdioma(String idioma);
}
