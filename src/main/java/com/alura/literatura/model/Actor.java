package com.alura.literatura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "actores")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int anoNacimiento;
    private int anoFallecimiento;
    private String nombre;

    @ManyToOne
    private Libro libro;

    public Actor() {
    }

    public Actor(DatosActores datosActores) {
        this.anoNacimiento = datosActores.anoNacimiento() != null ? datosActores.anoNacimiento() : 0; // Manejo de nulos
        this.anoFallecimiento = datosActores.anoFallecimiento() != null ? datosActores.anoFallecimiento() : 0;
        this.nombre = datosActores.nombre();
    }

    @Override
    public String toString() {
        return "Actor [anoNacimiento=" + anoNacimiento + ", anoFallecimiento=" + anoFallecimiento + ", nombre=" + nombre
                + ", libro=" + libro + "]";
    }

    // Getters y Setters
    public int getAnoNacimiento() {
        return anoNacimiento;
    }

    public void setAnoNacimiento(int anoNacimiento) {
        this.anoNacimiento = anoNacimiento;
    }

    public int getAnoFallecimiento() {
        return anoFallecimiento;
    }

    public void setAnoFallecimiento(int anoFallecimiento) {
        this.anoFallecimiento = anoFallecimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }
}
