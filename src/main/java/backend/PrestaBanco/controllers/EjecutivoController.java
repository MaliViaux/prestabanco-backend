package backend.PrestaBanco.controllers;

import backend.PrestaBanco.entities.dto.ArchivoDTO;
import backend.PrestaBanco.repositories.SolicitudRepository;
import backend.PrestaBanco.services.EjecutivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ejecutivo")
@CrossOrigin("*")
public class EjecutivoController {
    @Autowired
    private EjecutivoService ejecutivoService;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @PostMapping("/login")
    public ResponseEntity<Boolean> validarLogin(@RequestBody String contraseña) {
        boolean isValid = ejecutivoService.validarLoginExec(contraseña);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/{solicitudId}/archivos")
    public ResponseEntity<List<ArchivoDTO>> obtenerArchivosDeSolicitud(@PathVariable Long solicitudId) {
        List<ArchivoDTO> archivos = ejecutivoService.obtenerArchivosDeSolicitud(solicitudId);
        return ResponseEntity.ok(archivos);
    }

    @PutMapping("/{solicitudId}/accion")
    public ResponseEntity<String> cambiarEstado(@PathVariable Long solicitudId, @RequestBody String estado) {
        try {
            ejecutivoService.cambiarEstado(solicitudId, estado);
            return ResponseEntity.ok("Estado de la solicitud actualizado a: " + estado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint para añadir un comentario a una solicitud
    @PutMapping("/{solicitudId}/comentario")
    public ResponseEntity<String> añadirComentario(@PathVariable Long solicitudId,
                                                   @RequestBody String nuevoComentario) {
        ejecutivoService.añadirComentario(solicitudId, nuevoComentario);
        return ResponseEntity.ok("Comentario añadido con éxito");
    }
}
