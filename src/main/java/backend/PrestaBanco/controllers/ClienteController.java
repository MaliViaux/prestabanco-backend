package backend.PrestaBanco.controllers;

import backend.PrestaBanco.entities.Cliente;
import backend.PrestaBanco.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cliente")
@CrossOrigin("*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Obtener cliente por RUT
    @GetMapping("/{rut}")
    public ResponseEntity<Cliente> getClientePorRut(@PathVariable String rut) {
        Optional<Cliente> cliente = clienteService.getClienteByRut(rut);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Validar el login del cliente
    @PostMapping("/login")
    public ResponseEntity<Boolean> validarLogin(@RequestBody Map<String, String> loginData) {
        String rut = loginData.get("rut");
        String contraseña = loginData.get("contraseña");

        boolean isValid = clienteService.validarLogin(rut, contraseña);

        return ResponseEntity.ok(isValid);
    }

    // Obtener cliente por nombre y apellido
    @GetMapping("/buscar")
    public ResponseEntity<Cliente> getClientePorNombreYApellido(@RequestParam String nombre, @RequestParam String apellido) {
        Optional<Cliente> cliente = clienteService.getClienteByNombreApellido(nombre, apellido);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Guardar un nuevo cliente
    @PostMapping("/")
    public ResponseEntity<Cliente> guardarCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.saveCliente(cliente);
        return ResponseEntity.ok(nuevoCliente);
    }
}
