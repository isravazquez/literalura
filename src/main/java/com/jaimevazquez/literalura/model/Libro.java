package com.jaimevazquez.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Autor> autores = new ArrayList<>();
    private List<String> idioma = new ArrayList<>();
    private Integer numeroDescargas;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro){
        this.titulo = datosLibro.titulo();
        this.idioma = datosLibro.idioma();
        this.numeroDescargas = datosLibro.numeroDescargas();
    }

    @Override
    public String toString() {
        List<String> autores = this.getAutores().stream().map(Autor::getNombre).toList();
        return "----- Libro -----\n"+
                "Titulo: " + titulo + "\n" +
                "Autores: " + autores + "\n" +
                "Idioma: " + idioma + "\n" +
                "Numero de Descargas:" + numeroDescargas + "\n" +
                "-----------------\n";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = new ArrayList<>();
        autores.forEach(a -> {
            this.autores.add(a);
        });
    }

    public List<String> getIdioma() {
        return idioma;
    }

    public void setIdioma(List<String> idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }
}
