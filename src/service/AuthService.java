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
        System.out.println("[AUTH] Intento de login username='" + username + "'");
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario == null) {
            System.out.println("[AUTH] Usuario no encontrado o inactivo para username='" + username + "'");
            return null;
        }

        String hashIngresado = hashSHA256(password);
        String hashEnDb = usuario.getPasswordHash();
        System.out.println("[AUTH] Usuario encontrado id=" + usuario.getId() + " rol=" + usuario.getRol());
        System.out.println("[AUTH] hashIngresado=" + resumirHash(hashIngresado) + " hashDB=" + resumirHash(hashEnDb));
        if (hashIngresado.equals(usuario.getPasswordHash())) {
            System.out.println("[AUTH] OK credenciales válidas username='" + username + "'");
            return usuario;
        }
        System.out.println("[AUTH] FAIL hash no coincide username='" + username + "'");
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

    private static String resumirHash(String hash) {
        if (hash == null) return "null";
        int len = hash.length();
        String inicio = hash.substring(0, Math.min(8, len));
        String fin = hash.substring(Math.max(0, len - 4));
        return inicio + "..." + fin + " (len=" + len + ")";
    }
}
