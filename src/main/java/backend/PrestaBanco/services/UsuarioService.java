package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.Usuario;
import backend.PrestaBanco.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método para validar el login
    public boolean validarLogin(String rut, String contraseña) {
        // Busca el usuario por RUT
        Optional<Usuario> usuarioOpt = usuarioRepository.findByRut(rut);

        // Si el usuario existe y la contraseña coincide, devuelve true
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return usuario.getContraseña().equals(contraseña);
        }

        // Si no existe el usuario o la contraseña no coincide, devuelve false
        return false;
    }

    public Optional<Usuario> getUsuarioByRut(String rut) {
        return usuarioRepository.findByRut(rut);
    }

    public Optional<Usuario> getUsuarioByNombreApellido(String nombre, String apellido) {
        return usuarioRepository.findByNombreAndApellido(nombre, apellido);
    }

    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuarioPorRut(String rut) {
        usuarioRepository.deleteById(rut);
    }
}
