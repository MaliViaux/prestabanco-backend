package backend.PrestaBanco.controllers;

import backend.PrestaBanco.entities.Solicitud;
import backend.PrestaBanco.entities.dto.SolicitudDTO;
import backend.PrestaBanco.services.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/solicitud")
@CrossOrigin("*")
public class SolicitudController {
    @Autowired
    private SolicitudService solicitudService;

    // Endpoint para crear una nueva solicitud
    @PostMapping("/crear")
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody SolicitudDTO solicitudDTO) {
        try {
            Solicitud solicitud = solicitudService.guardarSolicitud(solicitudDTO);
            return new ResponseEntity<>(solicitud, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{solicitudId}")
    public ResponseEntity<Solicitud> getSolicitudById(@PathVariable Long solicitudId) {
        Solicitud solicitud = solicitudService.getSolicitudById(solicitudId);
        return ResponseEntity.ok(solicitud);
    }

    @GetMapping("/{id}/cliente")
    public ResponseEntity<Map<String, String>> obtenerDatosClientePorSolicitud(@PathVariable Long id) {
        Map<String, String> datosCliente = solicitudService.obtenerDatosClientePorSolicitud(id);
        return ResponseEntity.ok(datosCliente);
    }

    // Endpoint para obtener todas las solicitudes
    @GetMapping("/filtrar-por-estado")
    public ResponseEntity<List<Solicitud>> filtrarPorEstado() {
        List<Solicitud> solicitudes = solicitudService.getAllPending();
        return ResponseEntity.ok(solicitudes);
    }

    // Endpoint para obtener solicitudes por RUT del cliente
    @GetMapping("/cliente/{rut}")
    public ResponseEntity<List<Solicitud>> getSolicitudesByRut(@PathVariable String rut) {
        List<Solicitud> solicitudes = solicitudService.getSolicitudesByRut(rut);
        if (solicitudes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(solicitudes);
    }

    @PutMapping("/credito")
    public ResponseEntity<Solicitud> datosCredito(@RequestBody SolicitudDTO solicitudDTO) {
        Solicitud solicitudActualizado = solicitudService.actualizarDatos(solicitudDTO);
        return ResponseEntity.ok(solicitudActualizado);
    }

    @PostMapping("/{id}/upload-pdf")
    public ResponseEntity<String> uploadPdf(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam("tipoDocumento") String tipoDocumento) {
        try {
            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solo se permiten archivos PDF.");
            }

            byte[] pdfData = file.getBytes();
            solicitudService.guardarArchivoPdf(id, pdfData, tipoDocumento);

            return ResponseEntity.ok("Archivo PDF subido con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo.");
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarSolicitud(@PathVariable Long id) {
        try {
            solicitudService.cancelar(id);
            return ResponseEntity.ok("La solicitud ha sido cancelada exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Solicitud no encontrada.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cancelar la solicitud.");
        }
    }
}
