package backend.PrestaBanco.controllers;

import backend.PrestaBanco.entities.Usuario;
import backend.PrestaBanco.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/usuario")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener usuario por RUT
    @GetMapping("/{rut}")
    public ResponseEntity<Usuario> getUsuarioPorRut(@PathVariable String rut) {
        Optional<Usuario> usuario = usuarioService.getUsuarioByRut(rut);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Validar el login del usuario
    @PostMapping("/login")
    public ResponseEntity<Boolean> validarLogin(@RequestBody Map<String, String> loginData) {
        String rut = loginData.get("rut");
        String contraseña = loginData.get("contraseña");

        boolean isValid = usuarioService.validarLogin(rut, contraseña);

        return ResponseEntity.ok(isValid);
    }

    // Obtener usuario por nombre y apellido
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> getUsuarioPorNombreYApellido(@RequestParam String nombre, @RequestParam String apellido) {
        Optional<Usuario> usuario = usuarioService.getUsuarioByNombreApellido(nombre, apellido);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Guardar un nuevo usuario
    @PostMapping("/")
    public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Actualizar un usuario existente
    @PutMapping("/")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // Eliminar un usuario por RUT
    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarUsuarioPorRut(@PathVariable String rut) {
        usuarioService.deleteUsuarioPorRut(rut);
        return ResponseEntity.noContent().build();
    }
}
