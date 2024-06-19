package com.jaimevazquez.literalura.repository;

import com.jaimevazquez.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor,Long> {
    Optional<Autor> findByNombreContainsIgnoreCase(String nombre);
    List<Autor> findAll();
    @Query("SELECT a FROM Autor a WHERE a.anoNacimiento <= :ano AND a.anoFallecimiento >= :ano")
    List<Autor> listarAutoresVivosPorAno(Integer ano);
}
