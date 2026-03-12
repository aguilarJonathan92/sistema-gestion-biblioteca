package biblioteca.gestion;

import biblioteca.modelo.*;
import java.util.Calendar;

public class GestionBiblioteca {

    public static void main(String[] args) {

        Biblioteca biblioteca = new Biblioteca("Biblioteca Central");

        // Crear socios
        biblioteca.nuevoSocioDocente(14524782, "Juan Perez", "Programación");
        biblioteca.nuevoSocioDocente(17982110, "Juan Fernández", "Matemática");

        biblioteca.nuevoSocioEstudiante(28987498, "Francisco Paenza", "Ingeniería");
        biblioteca.nuevoSocioEstudiante(31987123, "Cesar Milstein", "Química");

        // Crear libros
        biblioteca.nuevoLibro("JAVA. Como Programar", 1, "Pearson", 2010);
        biblioteca.nuevoLibro("Programando con JAVA", 2, "McGraw Hill", 2015);
        biblioteca.nuevoLibro("Vivir para contarla", 1, "Sudamericana", 2002);

        // Obtener socio
        Socio socio = biblioteca.buscarSocio(28987498);

        // Obtener libro
        Libro libro = biblioteca.obtenerLibroPorTitulo("Programando con JAVA");

        // Registrar préstamo
        Calendar fecha = Calendar.getInstance();
        biblioteca.prestarLibro(fecha, socio, libro);

        // Mostrar listados
        System.out.println(biblioteca.listaDeSocios());
        System.out.println(biblioteca.listaDeLibros());
        System.out.println(biblioteca.listaDeTitulos());

    }
}