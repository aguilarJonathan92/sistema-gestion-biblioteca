package biblioteca.interfaz;

import biblioteca.gestion.GestionBiblioteca;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class PanelDevolucion extends JPanel {
    // ******* ATRIBUTOS *******
    private JTable tablaLibrosPrestados;
    private DefaultTableModel modeloTabla;
    private JButton btnDevolver;
    private JButton btnActualizar;
    private GestionBiblioteca controlador;

    // ******* CONSTRUCTOR *******
    public PanelDevolucion(GestionBiblioteca p_controlador) {
        this.controlador = p_controlador;
        this.inicializarComponentes();
        this.configurarEventos();
    }

    // ******* MÉTODOS PRINCIPALES *******
    private void inicializarComponentes(){
        setLayout(new BorderLayout(20, 20));
        setBackground(PaletaColores.FONDO_GENERAL);
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // --- Título Superior ---
        JLabel lblTitulo = new JLabel("DEVOLUCIÓN DE LIBROS", JLabel.CENTER);
        lblTitulo.setFont(Estilos.FUENTE_TITULO_PRINCIPAL);
        lblTitulo.setForeground(PaletaColores.COLOR_PRIMARIO);
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel central (instrucciones + tabla + botones) ---
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setOpaque(false);
        panelCentral.add(crearPanelInstrucciones(), BorderLayout.NORTH);
        panelCentral.add(crearTarjetaTabla(), BorderLayout.CENTER);
        panelCentral.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);
    }

    // ******* PANEL DE INSTRUCCIONES *******
    private JPanel crearPanelInstrucciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        JLabel lblInstruccion = new JLabel("📚 Seleccione un libro de la tabla para procesar su devolución");
        lblInstruccion.setFont(Estilos.FUENTE_ETIQUETA);
        lblInstruccion.setForeground(PaletaColores.COLOR_SECUNDARIO);

        panel.add(lblInstruccion);

        return panel;
    }

    // ******* TARJETA CON TABLA *******
    private PanelRedondeado crearTarjetaTabla() {
        PanelRedondeado tarjeta = new PanelRedondeado(
                20,
                PaletaColores.FONDO_BLANCO,
                PaletaColores.COLOR_SECUNDARIO,
                3
        );
        tarjeta.setLayout(new BorderLayout(10,10));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Definición del Modelo de la Tabla
        String[] columnas = {"Nro", "Título del Libro", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaLibrosPrestados = new JTable(modeloTabla);
        tablaLibrosPrestados.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tablaLibrosPrestados.setRowHeight(30);
        tablaLibrosPrestados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaLibrosPrestados.setShowHorizontalLines(true);
        tablaLibrosPrestados.getTableHeader().setFont(Estilos.FUENTE_ETIQUETA);
        tablaLibrosPrestados.getTableHeader().setBackground(PaletaColores.FONDO_BLANCO);
        tablaLibrosPrestados.getTableHeader().setReorderingAllowed(false);
        tablaLibrosPrestados.setGridColor(new Color(230, 230, 230));

        // Centrar contenido de las celdas
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaLibrosPrestados.getColumnCount(); i++) {
            tablaLibrosPrestados.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }

        // Centrar encabezados
        DefaultTableCellRenderer encabezadoCentrado =
                (DefaultTableCellRenderer) tablaLibrosPrestados.getTableHeader().getDefaultRenderer();
        encabezadoCentrado.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(tablaLibrosPrestados);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tarjeta.add(scrollPane, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarDatosTabla();

        return tarjeta;
    }

    // ******* PANEL DE BOTONES *******
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        // Botón Devolver
        btnDevolver = new JButton("✓ DEVOLVER LIBRO SELECCIONADO");
        btnDevolver.setFont(Estilos.FUENTE_BOTON);
        btnDevolver.setBackground(PaletaColores.COLOR_PRIMARIO);
        btnDevolver.setForeground(PaletaColores.TEXTO_CLARO);
        btnDevolver.setFocusPainted(false);
        btnDevolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDevolver.setPreferredSize(new Dimension(300, 40));

        // Botón Actualizar
        btnActualizar = new JButton("🔄 ACTUALIZAR LISTA");
        btnActualizar.setFont(Estilos.FUENTE_BOTON);
        btnActualizar.setBackground(PaletaColores.COLOR_SECUNDARIO);
        btnActualizar.setForeground(PaletaColores.TEXTO_CLARO);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnActualizar.setPreferredSize(new Dimension(200, 40));

        panel.add(btnDevolver);
        panel.add(btnActualizar);

        return panel;
    }

    // ******* CONFIGURAR EVENTOS *******
    private void configurarEventos() {
        // Evento del botón Devolver
        btnDevolver.addActionListener(e -> procesarDevolucion());

        // Evento del botón Actualizar
        btnActualizar.addActionListener(e -> cargarDatosTabla());
    }

    // ******* MÉTODO DE CARGA INICIAL *******
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla

        try {
            // Obtener solo los libros prestados desde el controlador
            ArrayList<String[]> librosPrestados = controlador.obtenerLibrosPrestados();

            if (librosPrestados != null && !librosPrestados.isEmpty()) {
                for (String[] libro : librosPrestados) {
                    modeloTabla.addRow(libro);
                }
            } else {
                // Mensaje si no hay libros prestados
                modeloTabla.addRow(new Object[]{"", "No hay libros prestados actualmente", ""});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los libros: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ******* MÉTODO PARA PROCESAR LA DEVOLUCIÓN *******
    private void procesarDevolucion() {
        // Verificar que hay una fila seleccionada
        int filaSeleccionada = tablaLibrosPrestados.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un libro de la tabla para devolver",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que la tabla no esté vacía con el mensaje de "No hay libros"
        String titulo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        if (titulo.contains("No hay libros prestados")) {
            return;
        }

        // Confirmación antes de procesar
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Confirma la devolución del libro:\n\"" + titulo + "\"?",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION) {
            return; // Usuario canceló
        }

        try {
            // Procesar la devolución a través del controlador
            controlador.procesarDevolucion(titulo);

            // Mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "✓ Devolución registrada exitosamente\n\nLibro: " + titulo,
                    "Devolución Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            // Recargar la tabla para reflejar los cambios
            cargarDatosTabla();

        } catch (IllegalArgumentException ex) {
            // Mostrar error específico
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error en la Devolución",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Error inesperado
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ******* MÉTODO PÚBLICO PARA REFRESCAR DESDE FUERA *******
    public void refrescarTabla() {
        cargarDatosTabla();
    }
}
