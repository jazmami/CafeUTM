
package modelo;


import java.time.LocalDateTime;

/**
 * Clase modelo que representa a CUALQUIER persona que usa el sistema:
 * Administrador, Alumno o Docente. Corresponde a la tabla "usuarios" de
 * la base de datos.
 *
 * IMPORTANTE — Fusión de responsabilidades: en el diseño anterior de 9
 * tablas, "usuarios" (login) y "clientes" (matrícula, beca) eran dos
 * tablas distintas ligadas 1 a 1. En la reestructuración a 6 tablas se
 * fusionaron en una sola: esta clase ahora carga TANTO los datos de
 * acceso (nombreUsuario, contrasena, rol, activo) COMO los datos de la
 * persona (matricula, nombre, tipoUsuario, becado, porcentajeBeca).
 * @author Equipo CaféUTM
 */
public class Usuario {

    private int idUsuario;
    private String matricula;          // Matrícula del alumno o clave del docente/admin
    private String nombreUsuario;      // Usuario con el que inicia sesión
    private String contrasena;
    private String nombre;             // Nombre completo de la persona
    private String tipoUsuario;        // "Administrador", "Alumno" o "Docente" (según ENUM de la BD)
    private boolean becado;
    private double porcentajeBeca;
    private boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime fechaCreacion;

    // Relación con Rol
    private Rol rol;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(int idUsuario, String matricula, String nombreUsuario, String contrasena,
            String nombre, Rol rol, String tipoUsuario, boolean becado,
            double porcentajeBeca, boolean activo) {
        this.idUsuario = idUsuario;
        this.matricula = matricula;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.rol = rol;
        this.tipoUsuario = tipoUsuario;
        this.becado = becado;
        this.porcentajeBeca = porcentajeBeca;
        this.activo = activo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public boolean isBecado() {
        return becado;
    }

    public void setBecado(boolean becado) {
        this.becado = becado;
    }

    public double getPorcentajeBeca() {
        return porcentajeBeca;
    }

    public void setPorcentajeBeca(double porcentajeBeca) {
        this.porcentajeBeca = porcentajeBeca;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        // Se usa en los JComboBox de búsqueda de usuario dentro de Pedidos
        return matricula + " - " + nombre;
    }
}
