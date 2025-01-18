    package com.example.petcare;

    public class Mascota {
        private String nombre;
        private int comida;
        private int agua;
        private String urlfoto;
        private String hora;

        public Mascota() {
        }


        public Mascota(String nombre, int comida, int agua,String hora, String urlfoto) {
            this.nombre = nombre;
            this.comida = comida;
            this.agua = agua;
            this.urlfoto = urlfoto;
            this.hora = hora;
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

        public int getAgua() {
            return agua;
        }

        public void setAgua(int agua) {
            this.agua = agua;
        }

        public String getUrlfoto() {
            return urlfoto;
        }

        public void setUrlfoto(String urlfoto) {
            this.urlfoto = urlfoto;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }
    }
