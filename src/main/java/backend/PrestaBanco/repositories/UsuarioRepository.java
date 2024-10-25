package backend.PrestaBanco.repositories;

import backend.PrestaBanco.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // Busca un usuario por su RUT
    Optional<Usuario> findByRut(String rut);

    // Método para encontrar usuarios por nombre y apellido
    Optional<Usuario> findByNombreAndApellido(String nombre, String apellido);
}
