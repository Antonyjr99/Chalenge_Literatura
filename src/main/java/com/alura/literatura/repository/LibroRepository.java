package com.alura.literatura.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alura.literatura.model.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Método para buscar por título
    Libro findByTitulo(String titulo);

    // Método para buscar por idiomas que contengan una cadena específica
    List<Libro> findByIdiomasContaining(String idioma);

    // Consulta personalizada que realiza el JOIN entre 'libros' y 'actores'
    @Query("SELECT a.titulo, " +
           "string_agg(b.nombre, ' || ') AS actores, " +
           "a.idiomas, a.descargas " +
           "FROM Libro a " +
           "INNER JOIN Actor b ON a.id = b.libro.id " +
           "GROUP BY a.titulo, a.idiomas, a.descargas")
    List<Object[]> findLibroWithActorDetails();

    // Consulta personalizada para obtener actores con los libros que han participado
    @Query("SELECT a.nombre, a.anoNacimiento, a.anoFallecimiento, string_agg(b.titulo, ' || ') AS titulos " +
    "FROM Actor a " +
    "INNER JOIN Libro b ON a.libro.id = b.id " +
    "GROUP BY a.nombre, a.anoNacimiento, a.anoFallecimiento")
    List<Object[]> findActorWithBooks();

    // Método para obtener libros con actores, idiomas y descargas filtrados por idioma
    @Query("SELECT a.titulo, " +
           "string_agg(b.nombre, ' || ') AS actores, " +
           "a.idiomas, a.descargas " +
           "FROM Libro a " +
           "INNER JOIN Actor b ON a.id = b.libro.id " +
           "WHERE a.idiomas = :idiomas " +
           "GROUP BY a.titulo, a.idiomas, a.descargas")
    List<Object[]> findLibroWithActorDetailsByIdioma(@Param("idiomas") String idioma);

    @Query("SELECT a.nombre, a.anoNacimiento, a.anoFallecimiento, string_agg(l.titulo, ' || ') AS titulos " +
    "FROM Actor a " +
    "INNER JOIN Libro l ON a.libro.id = l.id " +
    "WHERE a.anoNacimiento <= :anio AND (a.anoFallecimiento IS NULL OR a.anoFallecimiento > :anio) " +
    "GROUP BY a.nombre, a.anoNacimiento, a.anoFallecimiento")
    List<Object[]> findAutoresVivosEnAnio(@Param("anio") int anio);

}
