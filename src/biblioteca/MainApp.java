package biblioteca;

import javax.swing.SwingUtilities;

import biblioteca.gestion.GestionBiblioteca;
import biblioteca.interfaz.PanelPrincipal;

public class MainApp {
    public static void main(String[] args) {

        // Crear controlador del sistema
        GestionBiblioteca controlador = new GestionBiblioteca();

        // Iniciar interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            new PanelPrincipal(controlador);
        });

    }
}
