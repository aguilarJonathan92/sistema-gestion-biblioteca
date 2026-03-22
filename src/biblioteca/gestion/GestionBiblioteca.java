package biblioteca.gestion;

import biblioteca.persistencia.Persistencia;
import biblioteca.modelo.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // Metodos para Estudiantes
    public void registrarEstudiante(int p_dniSocio,String p_nombre,String p_carrera){
        bibliotecaActual.nuevoSocioEstudiante(p_dniSocio, p_nombre, p_carrera);
    }


    // Metodos para docentes
    public void registrarDocente(int p_dniSocio,String p_nombre,String p_area){
        bibliotecaActual.nuevoSocioDocente(p_dniSocio,p_nombre,p_area);
    }

    public boolean esDocenteResponsable(int dni) {
        ArrayList<Socio> socios = bibliotecaActual.getSocios();
        for (Socio socio : socios) {
            if (socio instanceof Docente && socio.getDniSocio() == dni) {
                Docente docente = (Docente) socio;
                return docente.esResponsable(); // Método que debe existir en Docente
            }
        }
        return false;
    }
    /**
     * Obtiene los días de préstamo de un docente
     */
    public int obtenerDiasPrestamoDocente(int dni) {
        ArrayList<Socio> socios = bibliotecaActual.getSocios();
        for (Socio socio : socios) {
            if (socio instanceof Docente && socio.getDniSocio() == dni) {
                return socio.getDiasPrestamos();
            }
        }
        return 0;
    }

    /**
     * Modifica los días de préstamo de un docente responsable
     */
    public boolean modificarDiasPrestamoResponsable(int dni, int nuevosDias) {
        ArrayList<Socio> socios = bibliotecaActual.getSocios();
        for (Socio socio : socios) {
            if (socio instanceof Docente && socio.getDniSocio() == dni) {
                Docente docente = (Docente) socio;
                // Solo permitir si es responsable
                if (docente.esResponsable()) {
                    docente.cambiarDiasDePrestamo(nuevosDias); // Método que debe existir en Docente
                    return true;
                }
            }
        }
        return false;
    }

    //metodos para libros

    public void registrarLibro(String p_titulo,int p_edicion,String p_editorial,int  p_anio){
        bibliotecaActual.nuevoLibro(p_titulo, p_edicion, p_editorial, p_anio);
    }




    public void prestarLibro(Calendar p_fechaRetiro, Socio p_socio,Libro p_libro){

    }

    public void devolverLibro(Libro p_libro){

    }

    //Metodos para tablas

    /**
     * Obtiene el String formateado, lo parsea y lo convierte en una ArrayList de
     * String arrays (filas) para la JTable.
     * @param tipoFiltro El filtro a aplicar ("Todos", "Estudiante", "Docente").
     * @return ArrayList<String[]> lista de socios lista para JTable.
     */
    public ArrayList<String[]> obtenerListaSociosParaTabla(String tipoFiltro) {
        // Obtener el String obligatorio de la capa de Negocio
        String listaCompleta = this.bibliotecaActual.listaDeSocios();
        // Parsear el String para obtener una lista de String[]
        ArrayList<String[]> datosTabla = parsearStringSocios(listaCompleta);
        // Aplicar el filtro final y devolver
        return aplicarFiltroTabla(datosTabla, tipoFiltro);
    }
    private ArrayList<String[]> parsearStringSocios(String listaCompleta) {
        ArrayList<String[]> datosTabla = new ArrayList<>();
        String[] lineas = listaCompleta.split("\n");
        for (String linea : lineas) {
            String lineaLimpia = linea.trim();
            // 🛑 LÍNEA DE CONTROL CLAVE: Detener el parseo cuando se encuentra el separador
            // Esto asegura que NO se intenten parsear las líneas de conteo.
            if (linea.contains("*" + "*".repeat(37))) {
                break;
            }
            // Criterio de identificación: Comienza con índice seguido de ')'
            if (lineaLimpia.matches("^\\d+\\).*")) {
                try {
                    // 1. Quitar índice: "1) D.N.I.:..." -> "D.N.I.:..."
                    String datos = lineaLimpia.substring(lineaLimpia.indexOf(")") + 2);
                    String[] partes = datos.split(" \\|\\| ");
                    if (partes.length >= 3) {
                        // Extracción de campos... (Lógica de parseo que ya tenías)
                        String dni = partes[0].substring(partes[0].indexOf(":") + 2).trim();
                        String tipoNombre = partes[1];
                        String nombreCompleto = tipoNombre.substring(0, tipoNombre.lastIndexOf("(")).trim();
                        String tipo = tipoNombre.substring(tipoNombre.lastIndexOf("(") + 1, tipoNombre.lastIndexOf(")")).trim();
                        String cantPrestados = partes[2].substring(partes[2].indexOf(":") + 2).trim();

                        datosTabla.add(new String[]{dni, nombreCompleto, tipo, cantPrestados});
                    }
                } catch (Exception e) {
                    System.err.println("Advertencia: No se pudo parsear la línea: " + lineaLimpia);
                }
            }
        }
        return datosTabla;
    }

    private ArrayList<String[]> aplicarFiltroTabla(ArrayList<String[]> datosCompletos, String tipoFiltro) {
        if (tipoFiltro.equals("Todos")) {
            return datosCompletos;
        }
        ArrayList<String[]> filtrados = new ArrayList<>();

        for (String[] fila : datosCompletos) {
            if (fila[2].equals(tipoFiltro)) { // fila[2] es el String del tipo de socio
                filtrados.add(fila);
            }
        }
        return filtrados;
    }

    /**
     * Llama al String obligatorio y extrae únicamente la sección de conteo y resumen.
     * @return String con solo los conteos de Estudiantes y Docentes.
     */
    public String obtenerResumenConteo() {
        String listaCompleta = this.bibliotecaActual.listaDeSocios();

        // Definir el patrón para extraer la sección de conteo
        // Patrón busca: Línea de asteriscos, seguidos de líneas de "Cantidad de Socios...",
        // y finaliza con otra línea de asteriscos.
        // Utiliza '(?s)' para que el punto (.) coincida con saltos de línea.
        String regex = "(?s)(\\*+\\s*Cantidad de Socios del tipo Estudiante:.*?\\*+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(listaCompleta);
        if (matcher.find()) {
            // Devuelve el grupo capturado (todo el bloque de conteo)
            return matcher.group(1).trim();
        } else {
            // Si el patrón no coincide (por ejemplo, lista vacía o formato cambiado)
            return "No hay datos de resumen disponibles.";
        }
    }
}