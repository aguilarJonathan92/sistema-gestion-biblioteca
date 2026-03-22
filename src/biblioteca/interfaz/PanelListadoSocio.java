package biblioteca.interfaz;

import biblioteca.gestion.GestionBiblioteca;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class PanelListadoSocio extends JPanel {
    // ******* ATRIBUTOS *******
    private JTable tablaSocios;
    private JTextArea areaTextoConteo;
    private JComboBox<String> cmbFiltroTipo;
    private JButton btnEliminarSocio;
    private JButton btnActualizar;
    private JButton btnModificarDias;
    private GestionBiblioteca controlador;

    // ******* CONSTRUCTOR *******
    public PanelListadoSocio(GestionBiblioteca p_controlador) {
        this.setControlador(p_controlador);
        this.setVistaListadoSocio();
        this.configurarEventos();
    }

    // ******* SETTERS *******
    private void setControlador(GestionBiblioteca p_controlador){
        this.controlador = p_controlador;
    }

    private void setVistaListadoSocio(){
        setLayout(new BorderLayout(20, 20));
        setBackground(PaletaColores.FONDO_GENERAL);
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // --- Título Superior ---
        JLabel lblTitulo = new JLabel("LISTADO DE SOCIOS REGISTRADOS", JLabel.CENTER);
        lblTitulo.setFont(Estilos.FUENTE_TITULO_PRINCIPAL);
        lblTitulo.setForeground(PaletaColores.COLOR_PRIMARIO);
        add(lblTitulo, BorderLayout.NORTH);

        // --- Área Central: Filtro y Tabla ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setOpaque(false);

        // 1. Panel de Filtro (Arriba)
        panelCentral.add(crearPanelFiltro(), BorderLayout.NORTH);

        // 2. Tabla de Listado (Centro - Dentro de una Tarjeta)
        panelCentral.add(crearTarjetaTabla(), BorderLayout.CENTER);

        // 3. Panel de Botones (Abajo)
        panelCentral.add(crearPanelBotones(), BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // Inicializa la tabla con todos los socios
        cargarDatosTabla("Todos");
    }

    // ******* OTROS MÉTODOS *******
    // ===============================================
    //               MÉTODOS AUXILIARES
    // ===============================================

    private JPanel crearPanelFiltro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        JLabel lblFiltro = new JLabel("Mostrar Tipo de Socio:");
        lblFiltro.setFont(Estilos.FUENTE_ETIQUETA);
        panel.add(lblFiltro);

        String[] tipos = {"Todos", "Estudiante", "Docente"};
        cmbFiltroTipo = new JComboBox<>(tipos);
        cmbFiltroTipo.setFont(Estilos.FUENTE_ETIQUETA);
        panel.add(cmbFiltroTipo);

        // Agrega el listener para filtrar al seleccionar una opción
        cmbFiltroTipo.addActionListener(e -> {
            String tipoSeleccionado = (String) cmbFiltroTipo.getSelectedItem();
            cargarDatosTabla(tipoSeleccionado);
        });

        return panel;
    }

    private PanelRedondeado crearTarjetaTabla() {
        PanelRedondeado tarjeta = new PanelRedondeado(
                20, PaletaColores.FONDO_BLANCO, PaletaColores.COLOR_SECUNDARIO, 3
        );
        tarjeta.setLayout(new BorderLayout(10, 10));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- 1. JTable y JScrollPane (CENTER) ---
        String[] columnas = {"D.N.I.", "Nombre y Apellido", "Tipo", "Libros Prestados"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaSocios = new JTable(modelo);
        tablaSocios.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tablaSocios.setRowHeight(25);
        tablaSocios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSocios.getTableHeader().setFont(Estilos.FUENTE_ETIQUETA);
        tablaSocios.getTableHeader().setBackground(PaletaColores.FONDO_BLANCO);
        tablaSocios.setGridColor(new Color(230, 230, 230));

        // Centrar contenido de las celdas
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaSocios.getColumnCount(); i++) {
            tablaSocios.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }

        // Centrar encabezados
        DefaultTableCellRenderer encabezadoCentrado =
                (DefaultTableCellRenderer) tablaSocios.getTableHeader().getDefaultRenderer();
        encabezadoCentrado.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(tablaSocios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tarjeta.add(scrollPane, BorderLayout.CENTER);

        // --- 2. JTextArea (SOUTH) ---
        areaTextoConteo = new JTextArea(5, 50);
        areaTextoConteo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        areaTextoConteo.setEditable(false);
        areaTextoConteo.setBackground(PaletaColores.FONDO_GENERAL);
        areaTextoConteo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollTexto = new JScrollPane(areaTextoConteo);
        scrollTexto.setPreferredSize(new Dimension(500, 150));
        tarjeta.add(scrollTexto, BorderLayout.SOUTH);

        return tarjeta;
    }

    // ******* PANEL DE BOTONES *******
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setOpaque(false);

        // Botón Actualizar
        btnActualizar = new JButton("🔄 ACTUALIZAR LISTA");
        btnActualizar.setFont(Estilos.FUENTE_BOTON);
        btnActualizar.setBackground(PaletaColores.COLOR_SECUNDARIO);
        btnActualizar.setForeground(PaletaColores.TEXTO_CLARO);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnActualizar.setPreferredSize(new Dimension(200, 40));

        // 👇 NUEVO BOTÓN MODIFICAR DÍAS
        btnModificarDias = new JButton("⏱️ MODIFICAR DÍAS PRÉSTAMO");
        btnModificarDias.setFont(Estilos.FUENTE_BOTON);
        btnModificarDias.setBackground(new Color(0, 123, 255)); // Azul
        btnModificarDias.setForeground(Color.WHITE);
        btnModificarDias.setFocusPainted(false);
        btnModificarDias.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnModificarDias.setPreferredSize(new Dimension(240, 40));
        btnModificarDias.setEnabled(false); // Deshabilitado inicialmente

        // Botón Eliminar (Rojo/Advertencia)
        btnEliminarSocio = new JButton("🗑️ ELIMINAR SOCIO");
        btnEliminarSocio.setFont(Estilos.FUENTE_BOTON);
        btnEliminarSocio.setBackground(new Color(220, 53, 69)); // Rojo Bootstrap
        btnEliminarSocio.setForeground(Color.WHITE);
        btnEliminarSocio.setFocusPainted(false);
        btnEliminarSocio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminarSocio.setPreferredSize(new Dimension(200, 40));
        btnEliminarSocio.setEnabled(false); // Deshabilitado hasta que se seleccione una fila

        panel.add(btnActualizar);
        panel.add(btnEliminarSocio);

        return panel;
    }

    // ******* CONFIGURAR EVENTOS *******
    private void configurarEventos() {
        // Evento del botón Actualizar
        btnActualizar.addActionListener(e -> {
            String tipoSeleccionado = (String) cmbFiltroTipo.getSelectedItem();
            cargarDatosTabla(tipoSeleccionado);
        });

        // 👇 EVENTO DEL BOTÓN MODIFICAR DÍAS
        btnModificarDias.addActionListener(e -> modificarDiasPrestamoResponsable());

        // Evento del botón Eliminar
        btnEliminarSocio.addActionListener(e -> eliminarSocioSeleccionado());

        // Habilitar/deshabilitar botón según selección
        tablaSocios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaSocios.getSelectedRow();
                if(filaSeleccionada != -1){
                    btnEliminarSocio.setEnabled(true);

                    // Habilitar botón de modificar días solo si es Docente Responsable
                    DefaultTableModel modelo = (DefaultTableModel) tablaSocios.getModel();
                    String dniStr = (String) modelo.getValueAt(filaSeleccionada, 0);
                    String tipo = (String) modelo.getValueAt(filaSeleccionada, 2);

                    try {
                        int dni = Integer.parseInt(dniStr.trim());
                        boolean esDocenteResponsable = tipo.equalsIgnoreCase("Docente")
                                && this.controlador.esDocenteResponsable(dni);
                        btnModificarDias.setEnabled(esDocenteResponsable);
                    } catch (NumberFormatException ex) {
                        btnModificarDias.setEnabled(false);
                    }
                }else{
                    btnEliminarSocio.setEnabled(false);
                    btnModificarDias.setEnabled(false);
                }

            }
        });
    }

    // 👇 NUEVO MÉTODO PARA MODIFICAR DÍAS DE PRÉSTAMO
    private void modificarDiasPrestamoResponsable() {
        int filaSeleccionada = tablaSocios.getSelectedRow();

        if (filaSeleccionada == -1) {
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) tablaSocios.getModel();
        String dniStr = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);

        try {
            int dni = Integer.parseInt(dniStr.trim());

            // Verificar que sea docente responsable
            if (!controlador.esDocenteResponsable(dni)) {
                JOptionPane.showMessageDialog(this,
                        "El docente seleccionado no es un responsable.\n" +
                                "Solo se pueden modificar días de docentes responsables.",
                        "No es Responsable",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener días actuales
            int diasActuales = controlador.obtenerDiasPrestamoDocente(dni);

            // Crear panel del diálogo
            JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel lblTitulo = new JLabel("📋 Modificar Días de Préstamo");
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel lblInfo = new JLabel("Días de préstamo actuales: " + diasActuales + " días");
            lblInfo.setFont(Estilos.FUENTE_ETIQUETA);

            JLabel lblNuevos = new JLabel("¿Cuantos dias desea agregarle?:");
            lblNuevos.setFont(Estilos.FUENTE_ETIQUETA);

            JSpinner spinnerDias = new JSpinner(new SpinnerNumberModel(diasActuales, 1, 365, 1));
            spinnerDias.setFont(new Font("SansSerif", Font.PLAIN, 16));
            ((JSpinner.DefaultEditor) spinnerDias.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);

            panel.add(lblTitulo);
            panel.add(lblInfo);
            panel.add(lblNuevos);
            panel.add(spinnerDias);

            // Mostrar diálogo
            int resultado = JOptionPane.showConfirmDialog(this,
                    panel,
                    "Modificar Días - " + nombre,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (resultado == JOptionPane.OK_OPTION) {
                int nuevosDias = (int) spinnerDias.getValue();

                // Confirmar cambio
                int confirmar = JOptionPane.showConfirmDialog(this,
                        "¿Confirma el cambio de días de préstamo?\n\n" +
                                "Docente: " + nombre + "\n" +
                                "Días actuales: " + diasActuales + " días\n" +
                                "Días nuevos: " + (diasActuales + nuevosDias) + " días",
                        "Confirmar Modificación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirmar == JOptionPane.YES_OPTION) {
                    // Realizar la modificación
                    boolean actualizado = controlador.modificarDiasPrestamoResponsable(dni, nuevosDias);

                    if (actualizado) {
                        JOptionPane.showMessageDialog(this,
                                "✓ Días de préstamo actualizados correctamente\n\n" +
                                        "Docente: " + nombre + "\n" +
                                        "DNI: " + dni + "\n" +
                                        "Días anteriores: " + diasActuales + " días\n" +
                                        "Días nuevos: " + (diasActuales + nuevosDias) + " días",
                                "Actualización Exitosa",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Refrescar tabla
                        refrescarTabla();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "❌ No se pudieron actualizar los días de préstamo.\n" +
                                        "Verifique que el docente sea responsable.",
                                "Error en la Actualización",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al leer el DNI del docente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Carga los datos en la tabla, filtrando por tipo de socio.
     */
    private void cargarDatosTabla(String tipo) {
        DefaultTableModel modelo = (DefaultTableModel) tablaSocios.getModel();
        modelo.setRowCount(0);

        try{
            // Obtener el String COMPLETO para el JTextArea
            String listaCompleta = this.controlador.obtenerResumenConteo();
            areaTextoConteo.setText(listaCompleta);
            areaTextoConteo.setCaretPosition(0);

            ArrayList<String[]> datosSocios = this.controlador.obtenerListaSociosParaTabla(tipo);

            for (String[] filaSocio : datosSocios) {
                modelo.addRow(filaSocio);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error de la aplicación al cargar datos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ******* MÉTODO PARA ELIMINAR SOCIO *******
    private void eliminarSocioSeleccionado() {
        int filaSeleccionada = tablaSocios.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un socio de la tabla",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener datos de la fila seleccionada
        DefaultTableModel modelo = (DefaultTableModel) tablaSocios.getModel();
        String dniStr = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        String tipo = (String) modelo.getValueAt(filaSeleccionada, 2);
        String prestamosStr = (String) modelo.getValueAt(filaSeleccionada, 3);

        // Validar que no tenga préstamos activos
        try {
            int cantidadPrestamos = Integer.parseInt(prestamosStr.trim());

            if (cantidadPrestamos > 0) {
                JOptionPane.showMessageDialog(this,
                        "❌ No se puede eliminar el socio:\n\n" +
                                nombre + " (" + tipo + ")\n" +
                                "DNI: " + dniStr + "\n\n" +
                                "Tiene " + cantidadPrestamos + " préstamo(s) activo(s)\n\n" +
                                "Debe devolver todos los libros antes de eliminar el socio",
                        "Eliminación no permitida",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al leer la cantidad de préstamos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmación antes de eliminar
        int confirmar = JOptionPane.showConfirmDialog(this,
                "⚠️ ¿Está SEGURO de eliminar al socio?\n\n" +
                        "Nombre: " + nombre + "\n" +
                        "Tipo: " + tipo + "\n" +
                        "DNI: " + dniStr + "\n\n" +
                        "⚠️ ESTA ACCIÓN NO SE PUEDE DESHACER ⚠️",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION) {
            return; // Usuario canceló
        }

        try {
            // Convertir DNI a entero
            int dni = Integer.parseInt(dniStr.trim());

            // Procesar eliminación a través del controlador
            boolean eliminado = controlador.eliminarSocio(dni);

            if (eliminado) {
                // Mensaje de éxito
                JOptionPane.showMessageDialog(this,
                        "✓ Socio eliminado exitosamente\n\n" +
                                "Nombre: " + nombre + "\n" +
                                "DNI: " + dniStr,
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                // Recargar la tabla
                String tipoSeleccionado = (String) cmbFiltroTipo.getSelectedItem();
                cargarDatosTabla(tipoSeleccionado);

            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el socio.\n" +
                                "Es posible que ya no exista en el sistema.",
                        "Error en la eliminación",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: El DNI no tiene un formato válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al eliminar el socio:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ******* MÉTODO PÚBLICO PARA REFRESCAR *******
    public void refrescarTabla() {
        String tipoSeleccionado = (String) cmbFiltroTipo.getSelectedItem();
        cargarDatosTabla(tipoSeleccionado);
    }
}
