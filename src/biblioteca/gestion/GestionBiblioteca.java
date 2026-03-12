package biblioteca.gestion;

import biblioteca.modelo.*;
import java.util.Calendar;

public class GestionBiblioteca {

    public static void main(String[] args) {


        // Crear la biblioteca
        Biblioteca biblioteca = new Biblioteca("Biblioteca Universitaria");

        // Agregar algunos libros
        biblioteca.nuevoLibro("El Quijote", 1, "Planeta", 2005);
        biblioteca.nuevoLibro("Clean Code", 1, "Prentice Hall", 2008);
        biblioteca.nuevoLibro("Estructuras de Datos", 2, "Pearson", 2015);

        // Agregar socios
        try {
            biblioteca.nuevoSocioEstudiante(12345678, "Juan Perez", "Ingeniería en Sistemas");
            biblioteca.nuevoSocioEstudiante(23456789, "Maria Gomez", "Lic. en Matemática");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        biblioteca.nuevoSocioDocente(34567890, "Carlos Lopez", "Programación");

        // Mostrar lista de socios
        System.out.println("===== LISTA DE SOCIOS =====");
        System.out.println(biblioteca.listaDeSocios());

        // Mostrar lista de libros
        System.out.println("===== LISTA DE LIBROS =====");
        System.out.println(biblioteca.listaDeLibros());

        // Simular un préstamo
        try {
            Socio socio = biblioteca.buscarSocio(12345678);
            Libro libro = biblioteca.getLibros().get(0);

            Calendar fechaRetiro = Calendar.getInstance();
            biblioteca.prestarLibro(fechaRetiro, socio, libro);

            System.out.println("\nSe realizó el préstamo del libro: " + libro.getTitulo());

        } catch (Exception e) {
            System.out.println("Error en el préstamo: " + e.getMessage());
        }

        // Mostrar libros nuevamente
        System.out.println("\n===== ESTADO DE LIBROS =====");
        System.out.println(biblioteca.listaDeLibros());

        // Simular devolución
        try {
            Libro libro = biblioteca.getLibros().get(0);
            biblioteca.devolverLibro(libro);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Consultar préstamos vencidos
        System.out.println("\n===== PRÉSTAMOS VENCIDOS =====");
        if (biblioteca.prestamosVencidos().isEmpty()) {
            System.out.println("No hay préstamos vencidos.");
        } else {
            biblioteca.prestamosVencidos().forEach(p ->
                    System.out.println(p.getLibro().getTitulo()));
        }

        // Mostrar docentes responsables
        System.out.println(biblioteca.listaDeDocentesResponsables());

        System.out.println("\nSistema de biblioteca finalizado correctamente.");
    }

}