/*
Proyecto: CaféUTM
Clase: ConexionBD
Descripción: Clase encargada de establecer la conexión con la base de
datos "cafe_utm" alojada en el servidor local de MySQL (XAMPP), mediante
el método estático conectar().
Fecha: Agosto 2026
Equipo: Eder David Vieyra Valenzuela, Emiliano Asdrubal Pacheco Gonzalez,
        Sergio Ortiz Ruiz, Dante Jazmanni Gonzalez Arreola
*/
package conexion;

// Clases java.sql para la conexión a la base de datos, el driver y el manejo de errores
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    // Constantes de conexión al servidor local de MySQL administrado por XAMPP.
    // IMPORTANTE: si el puerto o el nombre de la base de datos cambian en el
    // servidor local, solo es necesario actualizar esta línea.
    private static final String URL = "jdbc:mysql://localhost:3306/cafe_utm"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP por defecto no asigna contraseña a root

    /**
     * Método que realiza la conexión a la base de datos cafe_utm.
     * Además de abrir la conexión, se fuerza explícitamente el juego de
     * caracteres de la sesión a utf8mb4 mediante "SET NAMES". Esto es
     * necesario porque algunos servidores MySQL/MariaDB abren la sesión
     * en latin1 por defecto, lo que provoca que las tildes y la "ñ" se
     * trunquen al guardar datos (ej. "Alérgenos", "En preparación").
     * @return Objeto Connection si la conexión fue exitosa; null si ocurrió un error.
     */
    public static Connection conectar() {
        Connection conn = null; // Objeto de conexión que se retornará
        try {
            // Se carga el driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Se realiza la conexión con los datos definidos arriba
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Se fuerza el charset de la sesión a utf8mb4 para evitar
            // truncamientos en campos con acentos o eñes (ENUM, VARCHAR, etc.)
            try (Statement st = conn.createStatement()) {
                st.execute("SET NAMES utf8mb4");
            }

            System.out.println("Conexión exitosa a la base de datos cafe_utm");

        } catch (ClassNotFoundException | SQLException e) {
            // Si ocurre un error de driver o de conexión, se muestra en consola
            System.out.println("Error de conexión: " + e.getMessage());
        }

        return conn;
    }
}
