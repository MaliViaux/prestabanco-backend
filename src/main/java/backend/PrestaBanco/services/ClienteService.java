package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.Cliente;
import backend.PrestaBanco.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Método para validar el login
    public boolean validarLogin(String rut, String contraseña) {
        // Busca el cliente por RUT
        Optional<Cliente> clienteOpt = clienteRepository.findByRut(rut);

        // Si el cliente existe y la contraseña coincide, devuelve true
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return cliente.getContraseña().equals(contraseña);
        }

        // Si no existe el cliente o la contraseña no coincide, devuelve false
        return false;
    }

    public Optional<Cliente> getClienteByRut(String rut) {
        return clienteRepository.findByRut(rut);
    }

    public Optional<Cliente> getClienteByNombreApellido(String nombre, String apellido) {
        return clienteRepository.findByNombreAndApellido(nombre, apellido);
    }

    public Cliente saveCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
}
