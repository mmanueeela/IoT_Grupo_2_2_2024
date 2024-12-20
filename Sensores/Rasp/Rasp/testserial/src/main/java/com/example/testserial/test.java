package com.example.testserial;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

public class test {
    public static void main(String[] args) {
        try {
            // Inicializar el contexto Pi4J
            Context pi4j = Pi4J.newAutoContext();

            // Verificar si Pi4J está funcionando
            System.out.println("Pi4J funciona correctamente!");

            // Aquí puedes realizar más pruebas, como acceder a un GPIO si deseas
        } catch (Exception e) {
            System.out.println("Error al inicializar Pi4J: " + e.getMessage());
        }
    }
}