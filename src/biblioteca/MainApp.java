package biblioteca;

import javax.swing.SwingUtilities;

import biblioteca.gestion.GestionBiblioteca;
import interfaz.VentanaPrincipal;

public class MainApp {
    public static void main(String[] args) {

        // Crear controlador del sistema
        GestionBiblioteca controlador = new GestionBiblioteca();

        // Iniciar interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal(controlador);
        });

    }
}
