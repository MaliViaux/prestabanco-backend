package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.Solicitud;
import backend.PrestaBanco.entities.dto.ArchivoDTO;
import backend.PrestaBanco.repositories.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EjecutivoService {
    @Autowired
    private SolicitudService solicitudService;
    @Autowired
    private SolicitudRepository solicitudRepository;

    String contraseñaEjecutivos = "2024";
    public boolean validarLoginExec(String contraseña) {
        // Si la contrasenia es correcta
        if (contraseña.equals(contraseñaEjecutivos)) {
            return true;
        }

        // Si la contraseña no coincide, devuelve false
        return false;
    }

    public void cambiarEstado(Long solicitudId, String estado){
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        solicitud.setEstado(estado);

        solicitudRepository.save(solicitud);
    }

    public List<ArchivoDTO> obtenerArchivosDeSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Convierte cada archivo en ArchivoDTO para enviar al frontend
        return solicitud.getArchivos().stream()
                .map(archivo -> new ArchivoDTO(
                        archivo.getTipoDocumento(),
                        Base64.getEncoder().encodeToString(archivo.getPdfData()) // Convierte a base64
                ))
                .collect(Collectors.toList());
    }

    public void añadirComentario(Long solicitudId, String nuevoComentario) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        // Concatenar el nuevo comentario al existente, separándolo con un espacio
        String comentarioExistente = solicitud.getComentario() != null ? solicitud.getComentario() + " " : "";
        solicitud.setComentario(comentarioExistente + nuevoComentario);

        // Guardar la solicitud con el comentario actualizado
        solicitudRepository.save(solicitud);
    }
}
