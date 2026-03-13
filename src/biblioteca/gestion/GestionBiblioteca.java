package biblioteca.gestion;

import biblioteca.modelo.*;
import java.util.Calendar;

public class GestionBiblioteca {
    private Biblioteca biblioteca;

    public GestionBiblioteca() {
        this.biblioteca = new Biblioteca("Hola");
    }

    public void registrarLibro(String p_titulo,int p_edicion,String p_editorial,int  p_anio){
        biblioteca.nuevoLibro(p_titulo, p_edicion, p_editorial, p_anio);
    }

    public void registrarEstudiante(int p_dniSocio,String p_nombre,String p_carrera){
        biblioteca.nuevoSocioEstudiante(p_dniSocio, p_nombre, p_carrera);
    }

    public void registrarDocente(int p_dniSocio,String p_nombre,String p_area){
        biblioteca.nuevoSocioDocente(p_dniSocio,p_nombre,p_area);
    }

    public void prestarLibro(Calendar p_fechaRetiro, Socio p_socio,Libro p_libro){

    }

    public void devolverLibro(Libro p_libro){

    }
}