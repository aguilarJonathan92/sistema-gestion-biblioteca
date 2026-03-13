package biblioteca.persistencia;

import biblioteca.gestion.Biblioteca;
import java.io.*;

public class Persistencia {
    //ruta del archivo binario
    private static final String NOMBRE_ARCHIVO = "datos_biblioteca.dat";

    private Persistencia() {}
    /**
     * Guarda el objeto Biblioteca en un archivo binario
     */
    public static void guardar(Biblioteca p_biblioteca){
        try (ObjectOutputStream objectOut =
                     new ObjectOutputStream(new FileOutputStream(NOMBRE_ARCHIVO))) {

            objectOut.writeObject(p_biblioteca);
            System.out.println("Biblioteca guardada en " + NOMBRE_ARCHIVO);

        } catch (IOException e) {
            System.out.println("Error al guardar la Biblioteca.");
            e.printStackTrace();
        }
    }

    public static Biblioteca cargar(){
        try (ObjectInputStream objectIn =
                     new ObjectInputStream(new FileInputStream(NOMBRE_ARCHIVO))) {

            Biblioteca biblioteca = (Biblioteca) objectIn.readObject();
            System.out.println("Biblioteca cargada desde " + NOMBRE_ARCHIVO);
            return biblioteca;

        } catch (FileNotFoundException e) {
            System.out.println("Archivo de datos no encontrado. Se crea una nueva Biblioteca.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar datos. Se inicializa una Biblioteca nueva.");
            e.printStackTrace();
        }

        return new Biblioteca("Biblioteca App");
    }
}
