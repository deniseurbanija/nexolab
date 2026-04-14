package service;

import dao.UsuarioDAO;
import model.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Autentica al usuario comparando el hash SHA-256 del password ingresado
     * contra el hash almacenado en la base de datos.
     *
     * @return el Usuario si las credenciales son válidas, null en caso contrario.
     */
    public Usuario login(String username, String password) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario == null) return null;

        String hashIngresado = hashSHA256(password);
        if (hashIngresado.equals(usuario.getPasswordHash())) {
            return usuario;
        }
        return null;
    }

    public static String hashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible en esta JVM", e);
        }
    }
}
