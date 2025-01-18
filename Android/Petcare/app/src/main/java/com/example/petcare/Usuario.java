package com.example.petcare;
import java.util.Date;


public class Usuario {
    private String nombre;
    private String apellidos;
    private String correo;
    private String urlfoto;

    public Usuario() {
    }


    public Usuario(String nombre, String apellidos, String correo, String urlfoto) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.urlfoto = urlfoto;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }
}
