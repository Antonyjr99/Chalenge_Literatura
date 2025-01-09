package com.alura.literatura.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContenedorResultados(
    @JsonAlias("results") List<DatosLibro> results
) {
    public List<DatosLibro> results() {
        return results != null ? results : List.of(); // Manejo de nulos
    }
}
