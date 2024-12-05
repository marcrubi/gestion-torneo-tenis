// src/main/java/com/ISII/gestion_torneos_tenis/model/JugadorRanking.java

package com.ISII.gestion_torneos_tenis.model;

/**
 * Clase auxiliar para almacenar estadísticas de un jugador y facilitar el cálculo del ranking.
 */
public class JugadorRanking implements Comparable<JugadorRanking> {

    private Jugador jugador;
    private int partidosGanados;
    private int setsGanados;
    private int setsPerdidos;
    private int juegosGanados;
    private int juegosPerdidos;
    private int posicion;

    public JugadorRanking(Jugador jugador) {
        this.jugador = jugador;
        this.partidosGanados = 0;
        this.setsGanados = 0;
        this.setsPerdidos = 0;
        this.juegosGanados = 0;
        this.juegosPerdidos = 0;
    }

    // Getters y Setters

    public Jugador getJugador() {
        return jugador;
    }

    public int getPartidosGanados() {
        return partidosGanados;
    }

    public void incrementPartidosGanados() {
        this.partidosGanados++;
    }

    public int getSetsGanados() {
        return setsGanados;
    }

    public void incrementSetsGanados(int cantidad) {
        this.setsGanados += cantidad;
    }

    public int getSetsPerdidos() {
        return setsPerdidos;
    }

    public void incrementSetsPerdidos(int cantidad) {
        this.setsPerdidos += cantidad;
    }

    public int getJuegosGanados() {
        return juegosGanados;
    }

    public void incrementJuegosGanados(int cantidad) {
        this.juegosGanados += cantidad;
    }

    public int getJuegosPerdidos() {
        return juegosPerdidos;
    }

    public void incrementJuegosPerdidos(int cantidad) {
        this.juegosPerdidos += cantidad;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    /**
     * Define el criterio de comparación para ordenar los jugadores.
     * Primero por partidos ganados, luego por sets ganados y finalmente por juegos ganados.
     */
    @Override
    public int compareTo(JugadorRanking other) {
        if (this.partidosGanados != other.partidosGanados) {
            return Integer.compare(other.partidosGanados, this.partidosGanados);
        }
        if (this.setsGanados != other.setsGanados) {
            return Integer.compare(other.setsGanados, this.setsGanados);
        }
        return Integer.compare(other.juegosGanados, this.juegosGanados);
    }
}
