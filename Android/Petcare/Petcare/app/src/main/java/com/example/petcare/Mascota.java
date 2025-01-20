package com.example.petcare;

import java.util.List;

public class Mascota {
    private String nombre;
    private int comida;
    private String urlfoto;
    private List<String> hora; // Cambiado de String a List<String>

    public Mascota() {
    }

    public Mascota(String nombre, int comida, String urlfoto, List<String> hora) {
        this.nombre = nombre;
        this.comida = comida;
        this.urlfoto = urlfoto;
        this.hora = hora; // Ahora acepta una lista de horas
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getComida() {
        return comida;
    }

    public void setComida(int comida) {
        this.comida = comida;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }

    public List<String> getHora() {
        return hora;
    }

    public void setHora(List<String> hora) {
        this.hora = hora;
    }
}
