package biblioteca.gestion;

import biblioteca.persistencia.Persistencia;
import biblioteca.modelo.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
    /**
     * Elimina un socio del sistema.
     * @param dni DNI del socio a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     * @throws IllegalArgumentException Si el socio tiene préstamos activos
     */
    public boolean eliminarSocio(int dni) throws IllegalArgumentException {
        Socio socio = buscarSocioPorDni(dni);
        if (socio == null) {
            return false; // No existe el socio
        }
        // Verificar que no tenga préstamos activos
        if (socio.cantLibrosPrestados() > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el socio " + socio.getNombre() +
                            " porque tiene " + socio.cantLibrosPrestados() + " préstamo(s) activo(s)"
            );
        }
        return this.bibliotecaActual.quitarSocio(socio);
    }
    public Socio buscarSocioPorDni(int dni){
        return this.bibliotecaActual.buscarSocio(dni);
    }
    public int obtenerSociosPorTipo(String p_tipo){
        return this.bibliotecaActual.cantidadDeSociosPorTipo(p_tipo);
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

    public String[] listaDeDocentesResponsables(){
        String listaCompleta = this.bibliotecaActual.listaDeDocentesResponsables();
        String[] lineas = listaCompleta.split("\n");
        if (lineas.length > 1) {
            // Crear un nuevo array que comienza desde el índice 2 (ahi comienza la lista de docentes)
            // lineas.length es el índice del final (exclusivo)
            String[] lineasFiltradas = Arrays.copyOfRange(lineas, 2, lineas.length);
            return lineasFiltradas;
        }

        // Si solo hay una línea o está vacío, devolver un array vacío o el original (según la necesidad)
        // En este caso, si solo hay una línea (que asumimos es el encabezado), devolvemos vacío.
        return new String[0];
    }
    //metodos para libros

    public void registrarLibro(String p_titulo,int p_edicion,String p_editorial,int  p_anio){
        bibliotecaActual.nuevoLibro(p_titulo, p_edicion, p_editorial, p_anio);
    }

    public String obtenerDetallesLibro(String titulo) {
        try {
            ArrayList<Libro> libros = this.bibliotecaActual.getLibros();

            Libro libroEncontrado = null;
            for (Libro libro : libros) {
                if (libro.getTitulo().equalsIgnoreCase(titulo.trim())) {
                    libroEncontrado = libro;
                    break;
                }
            }

            if (libroEncontrado == null) {
                return "Libro no encontrado";
            }

            StringBuilder detalles = new StringBuilder();
            detalles.append("📚 Título: ").append(libroEncontrado.getTitulo()).append("\n");
            detalles.append("📖 Edición: ").append(libroEncontrado.getEdicion()).append("\n");
            detalles.append("🏢 Editorial: ").append(libroEncontrado.getEditorial()).append("\n");
            detalles.append("📅 Año: ").append(libroEncontrado.getAnio()).append("\n\n");

            if (libroEncontrado.prestado()) {
                Prestamo prestamo = libroEncontrado.ultimoPrestamo();

                if (prestamo != null && prestamo.getSocio() != null) {
                    Socio socio = prestamo.getSocio();

                    detalles.append("📌 ESTADO: PRESTADO\n\n");
                    detalles.append("👤 Prestado a:\n");
                    detalles.append("   • Nombre: ").append(socio.getNombre()).append("\n");
                    detalles.append("   • DNI: ").append(socio.getDniSocio()).append("\n");
                    detalles.append("   • Días prestado: ").append(socio.getDiasPrestamos()).append("\n\n");

                    // 👇 USAR MÉTODO AUXILIAR
                    detalles.append("📅 Fecha de préstamo: ")
                            .append(formatearFecha(prestamo.getFechaRetiro()))
                            .append("\n");
                    detalles.append("📅 Fecha de devolución: ")
                            .append(formatearFecha(prestamo.getFechaDevolucion()));
                } else {
                    detalles.append("📌 ESTADO: PRESTADO\n");
                    detalles.append("⚠️ No se encontró información del préstamo actual");
                }
            } else {
                detalles.append("📌 ESTADO: DISPONIBLE EN BIBLIOTECA ✅");
            }

            return detalles.toString();

        } catch (Exception e) {
            return "Error al obtener detalles: " + e.getMessage();
        }
    }
    public Libro buscarLibroPorTitulo(String titulo) {
        // Obtenemos la lista maestra de libros de la capa de Negocio.
        ArrayList<Libro> libros = this.bibliotecaActual.getLibros();
        if (libros == null) {
            return null; // No hay inventario.
        }
        // Iteramos sobre los objetos Libro para encontrar la coincidencia por título
        for (Libro libro : libros) {
            if (libro.getTitulo().equalsIgnoreCase(titulo.trim())) {
                return libro; // Devuelve el objeto Libro encontrado
            }
        }
        return null;
    }
    public void registrarNuevoPrestamo(Date p_fecha, Socio socio, Libro libro) throws IllegalArgumentException {
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(p_fecha);
        this.bibliotecaActual.prestarLibro(fecha, socio, libro);
    }

    public ArrayList<String[]> listaDeLibros(){
        String listaLibros = this.bibliotecaActual.listaDeLibros();
        ArrayList<String[]> datosLibros = new ArrayList<String[]>();
        String[]lineas = listaLibros.split("\n"); //divido String por lineas
        int numeroFila = 1;

        for (String linea : lineas) {
            // Ejemplo de línea: "1) Titulo: Java. Como Programar || Prestado: (No)"
            if (linea.contains("Titulo:") && linea.contains("Prestado:")) {
                // Extraer el título
                String titulo = linea.substring(linea.indexOf("Titulo:") + 8, linea.indexOf("||")).trim();
                // Extraer el estado de préstamo
                String prestado = linea.substring(linea.indexOf("Prestado: (") + 11, linea.lastIndexOf(")")).trim();
                // 👇 Convertir "Si"/"No" a formato visual con emojis
                String estadoFormateado;
                if (prestado.equalsIgnoreCase("Si")) {
                    estadoFormateado = "PRESTADO 🚫";
                } else {
                    estadoFormateado = "DISPONIBLE ✅";
                }
                // Guardar el resultado como array
                datosLibros.add(new String[]{
                        String.valueOf(numeroFila),
                        titulo,
                        estadoFormateado
                });
                numeroFila++;
            }
        }
        return datosLibros;
    }
    public int obtenerCantidadLibrosRegistrados(){
        return this.bibliotecaActual.getLibros().size();
    }
    public String[] listaDeTitulos(){
        String listaCompleta = this.bibliotecaActual.listaDeTitulos();
        String[] lineas =listaCompleta.split("\n");
        return lineas;
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
    private String formatearFecha(Calendar fecha) {
        if (fecha == null) {
            return "No devolvió hasta la fecha";
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(fecha.getTime());
    }
}