package com.alura.literatura.principal;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.alura.literatura.model.ContenedorResultados;
import com.alura.literatura.model.DatosLibro;
import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books?search=";
    private ConvierteDatos conversor = new ConvierteDatos();

    private final LibroRepository repositorio;

    // Constructor con inyección de dependencias explícita
    @Autowired
    public Principal(LibroRepository repositorio) {
        this.repositorio = repositorio;
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
            --------------------------------------------
            --------------------------------------------
            1 - Buscar libro por titulo 
            2 - Listar libros registrados
            3 - Listar autores registrados
            4 - Listar autores vivos en un determinado año
            5 - Listar libros por idioma     
            0 - Salir
            --------------------------------------------
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    teclado.close();
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DatosLibro getDatosLibro(String titulo) {
        String url = URL_BASE + titulo.replace(" ", "%20");
        var json = consumoApi.obtenerDatos(url);
    
        System.out.println("Respuesta de la API: " + json);
    
        if (json == null || json.isEmpty()) {
            System.out.println("No se encontraron resultados para el libro: " + titulo);
            return null;
        }
    
        try {
            // Conversión de la respuesta completa a un contenedor de datos
            var resultados = conversor.obtenerDatos(json, ContenedorResultados.class);
            if (resultados.results().isEmpty()) {
                System.out.println("No se encontraron libros con ese título.");
                return null;
            }
    
            // Retorna el primer libro de los resultados
            return resultados.results().get(0);
        } catch (Exception e) {
            System.out.println("Error al procesar los datos del libro.");
            e.printStackTrace();
            return null;
        }
    }
    

    private void buscarLibroWeb() {
    System.out.print("Ingrese el título del libro: ");
    String titulo = teclado.nextLine();

    // Validar si el libro ya está registrado en la base de datos
    Optional<Libro> libroExistente = Optional.ofNullable(repositorio.findByTitulo(titulo));
    if (libroExistente.isPresent()) {
        System.out.println("El libro ya está registrado: " + libroExistente.get().getTitulo());
        return;
    }

    // Obtener los datos del libro desde la API
    DatosLibro datos = getDatosLibro(titulo);
    if (datos != null) {
        try {
            // Intentar guardar el libro en la base de datos
            Libro libro = new Libro(datos);
            repositorio.save(libro);
            System.out.println("Libro registrado exitosamente: " + libro.getTitulo());
        } catch (DataIntegrityViolationException e) {
            // Manejar la excepción de llave duplicada
            System.out.println("El libro ya está registrado en la base de datos.");
        }
    } else {
        System.out.println("No se pudo registrar el libro.");
    }
}


    private void listarLibrosRegistrados() {
        List<Object[]> resultados = repositorio.findLibroWithActorDetails();
        if (resultados.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            resultados.forEach(result -> {
                String titulo = (String) result[0];
                String actores = (String) result[1];  // Cambiado de 'autor' a 'actores'
                String idiomas = (String) result[2];
                int descargas = (Integer) result[3]; // Asumiendo que es un entero
                // Imprimir título y actores en líneas separadas
                System.out.println("------------LIBRO------------");
                System.out.println("Título: " + titulo);
                System.out.println("Actores: " + actores); // Mostramos los actores
                System.out.println("Idiomas: " + idiomas);
                System.out.println("Descargas: " + descargas);
                System.out.println("-----------------------------");
                System.out.println(); // Línea en blanco para separar cada libro
            });
        }
    }
    

    private void listarAutores() {
        // Obtenemos la lista de autores y sus libros
        List<Object[]> resultados = repositorio.findActorWithBooks();
        
        if (resultados.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            // Iteramos sobre los resultados
            resultados.forEach(result -> {
                String nombre = (String) result[0];  // Nombre del autor
                Integer anoNacimiento = (Integer) result[1];  // Año de nacimiento
                Integer anoFallecimiento = (Integer) result[2];  // Año de fallecimiento
                String titulos = (String) result[3];  // Libros en los que ha participado
                
                // Imprimimos los resultados
                System.out.println("------------AUTOR------------");
                System.out.println("Autor: " + nombre);
                System.out.println("Año de Nacimiento: " + anoNacimiento);
                System.out.println("Año de Fallecimiento: " + (anoFallecimiento != null ? anoFallecimiento : "Vivo"));
                System.out.println("Títulos: " + titulos);
                System.out.println("----------------------------");
                System.out.println();  // Línea en blanco para separar cada autor
            });
        }
    }
    

    private void listarLibrosPorIdioma() {
        // Obtener la lista de idiomas únicos del repositorio
        List<String> idiomasDisponibles = repositorio.findDistinctIdiomas();
    
        if (idiomasDisponibles.isEmpty()) {
            System.out.println("No hay idiomas registrados en la base de datos.");
            return;
        }
    
        // Mostrar los idiomas disponibles
        System.out.println("Idiomas disponibles:");
        for (int i = 0; i < idiomasDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + idiomasDisponibles.get(i));
        }
    
        // Crear un objeto Scanner para leer la entrada del usuario
        Scanner scanner = new Scanner(System.in);
    
        // Solicitar al usuario que seleccione un idioma de la lista
        System.out.print("Seleccione un idioma (ingrese el número correspondiente): ");
        int opcion = scanner.nextInt();
    
        // Validar la opción ingresada
        if (opcion < 1 || opcion > idiomasDisponibles.size()) {
            System.out.println("Opción inválida. Inténtelo de nuevo.");
            return;
        }
    
        // Obtener el idioma seleccionado
        String idiomaSeleccionado = idiomasDisponibles.get(opcion - 1);
    
        // Llamar al método del repositorio con el idioma seleccionado
        List<Object[]> resultados = repositorio.findLibroWithActorDetailsByIdioma(idiomaSeleccionado);
    
        if (resultados.isEmpty()) {
            System.out.println("No hay libros registrados para el idioma: " + idiomaSeleccionado);
        } else {
            resultados.forEach(result -> {
                String titulo = (String) result[0];
                String actores = (String) result[1];
                String idiomas = (String) result[2];
                int descargas = (Integer) result[3];
                // Imprimir título y autor en líneas separadas
                System.out.println("------------LIBRO------------");
                System.out.println("Título: " + titulo);
                System.out.println("Autores: " + actores);
                System.out.println("Idiomas: " + idiomas);
                System.out.println("Descargas: " + descargas);
                System.out.println("-----------------------------");
                System.out.println(); // Línea en blanco para separar cada libro
            });
        }
    }
    
    
    private void listarAutoresVivosEnAnio() {
        // Solicitar al usuario que ingrese el año
        System.out.print("Ingrese el año para verificar los autores vivos: ");
        int anio = teclado.nextInt();
        teclado.nextLine();  // Consumir la línea de salto
    
        // Llamar al método del repositorio para obtener los autores vivos en ese año
        List<Object[]> resultados = repositorio.findAutoresVivosEnAnio(anio);
        
        if (resultados.isEmpty()) {
            System.out.println("No hay autores vivos en el año: " + anio);
        } else {
            // Iterar sobre los resultados y mostrarlos
            resultados.forEach(result -> {
                String nombre = (String) result[0];  // Nombre del autor
                Integer anoNacimiento = (Integer) result[1];  // Año de nacimiento
                Integer anoFallecimiento = (Integer) result[2];  // Año de fallecimiento
                String titulos = (String) result[3];  // Títulos de libros
    
                // Imprimir los resultados
                System.out.println("------------AUTOR VIVO------------");
                System.out.println("Autor: " + nombre);
                System.out.println("Año de Nacimiento: " + anoNacimiento);
                System.out.println("Año de Fallecimiento: " + (anoFallecimiento != null ? anoFallecimiento : "Vivo"));
                System.out.println("Títulos: " + titulos);
                System.out.println("------------------------------");
                System.out.println();  // Línea en blanco para separar cada autor
            });
        }
    }
}
