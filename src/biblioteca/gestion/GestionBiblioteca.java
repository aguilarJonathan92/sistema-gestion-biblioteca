package biblioteca.gestion;

import biblioteca.persistencia.Persistencia;
import biblioteca.modelo.*;
import java.util.Calendar;

public class GestionBiblioteca {
    private Biblioteca bibliotecaActual;

    /**
     * Constructor GestionBiblioteca
     * Maneja la carga de datos por medio de la persistencia
     */
    public GestionBiblioteca() {
        this.bibliotecaActual = Persistencia.cargar();
    }

    /**
     * Metodo para Guardar los datos antes de dar por finalizado el programa
     */
    public void guardarYSalir(){
        Persistencia.guardar(this.bibliotecaActual);
    }

    public void registrarLibro(String p_titulo,int p_edicion,String p_editorial,int  p_anio){
        bibliotecaActual.nuevoLibro(p_titulo, p_edicion, p_editorial, p_anio);
    }

    public void registrarEstudiante(int p_dniSocio,String p_nombre,String p_carrera){
        bibliotecaActual.nuevoSocioEstudiante(p_dniSocio, p_nombre, p_carrera);
    }

    public void registrarDocente(int p_dniSocio,String p_nombre,String p_area){
        bibliotecaActual.nuevoSocioDocente(p_dniSocio,p_nombre,p_area);
    }

    public void prestarLibro(Calendar p_fechaRetiro, Socio p_socio,Libro p_libro){

    }

    public void devolverLibro(Libro p_libro){

    }
}