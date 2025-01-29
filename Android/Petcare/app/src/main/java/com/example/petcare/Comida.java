package com.example.petcare;

public class Comida {
    private double porcentaje;

    public Comida(){}

    public Comida(double porcentaje){
        this.porcentaje = porcentaje;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public String toString() {
        return "Comida{" +
                "porcentaje=" + porcentaje +
                '}';
    }
}
