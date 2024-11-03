package backend.PrestaBanco.repositories;

import backend.PrestaBanco.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    // Busca un cliente por su RUT
    Optional<Cliente> findByRut(String rut);

    // Método para encontrar clientes por nombre y apellido
    Optional<Cliente> findByNombreAndApellido(String nombre, String apellido);
}
