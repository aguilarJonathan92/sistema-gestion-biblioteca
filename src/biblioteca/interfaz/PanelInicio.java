package biblioteca.interfaz;

import biblioteca.gestion.GestionBiblioteca;

import javax.swing.*;
import java.awt.*;

public class PanelInicio extends JPanel {

    public PanelInicio(GestionBiblioteca controlador) {
        setLayout(new BorderLayout());
        add(new JLabel("Panel de Inicio", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
