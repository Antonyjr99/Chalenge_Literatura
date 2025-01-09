package com.alura.literatura.model;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private String idiomas;
    private int descargas;

    // Cambiar a FetchType.LAZY si no siempre necesitas los autores al consultar el libro
    //@OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "libro",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Actor> autores;

    public Libro() {}

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.idiomas = String.join(",", datosLibro.idiomas());
        this.descargas = datosLibro.descargas();

        this.autores = datosLibro.autores().stream()
                                .map(autor -> {
                                    Actor actor = new Actor(autor);
                                    actor.setLibro(this); // Relación con el libro
                                    return actor;
                                }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        String autoresStr = autores != null ? autores.stream().map(Actor::getNombre).collect(Collectors.joining(", ")) : "No hay autores";
        return "Libro [titulo=" + titulo + ", idiomas=" + idiomas + ", descargas=" + descargas + ", autores=" + autoresStr + "]";
    }

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public List<Actor> getAutores() {
        return autores;
    }

    public void setAutores(List<Actor> autores) {
        this.autores = autores;
    }
}
