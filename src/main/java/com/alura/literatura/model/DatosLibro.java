package com.alura.literatura.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
    @JsonAlias("title") String titulo,
    @JsonAlias("authors") List<DatosActores> autores,
    @JsonAlias("languages") List<String> idiomas,
    @JsonAlias("download_count") Integer descargas
) {
    public List<DatosActores> autores() {
        return autores != null ? autores : List.of(); // Manejo de nulos
    }

    public List<String> idiomas() {
        return idiomas != null ? idiomas : List.of();
    }

    public Integer descargas() {
        return descargas != null ? descargas : 0;
    }
}
