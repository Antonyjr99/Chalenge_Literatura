package com.alura.literatura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos {
    // Instancia de ObjectMapper reutilizable
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            // Convierte el JSON a la clase especificada
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            // Lanzar una excepción más clara y con el mensaje adecuado
            throw new RuntimeException("Error al convertir el JSON a la clase: " + clase.getName(), e);
        }
    }
}
