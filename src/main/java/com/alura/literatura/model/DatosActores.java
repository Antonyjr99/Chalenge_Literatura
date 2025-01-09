package com.alura.literatura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosActores(
    @JsonAlias("birth_year") Integer anoNacimiento,
    @JsonAlias("death_year") Integer anoFallecimiento,
    @JsonAlias("name") String nombre
) {
}
