package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.dto.ArchivoDTO;
import backend.PrestaBanco.entities.Archivo;
import backend.PrestaBanco.entities.Solicitud;
import backend.PrestaBanco.repositories.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EjecutivoServiceTest {

    @Autowired
    private EjecutivoService ejecutivoService;

    @MockBean
    private SolicitudRepository solicitudRepository;

    @Test
    public void testValidarLoginExec_Successful() {
        // Contraseña correcta
        String contraseña = "2024";

        // Ejecución
        boolean result = ejecutivoService.validarLoginExec(contraseña);

        // Verificación
        assertThat(result).isTrue();
    }

    @Test
    public void testValidarLoginExec_Failed() {
        // Contraseña incorrecta
        String contraseña = "incorrecta";

        // Ejecución
        boolean result = ejecutivoService.validarLoginExec(contraseña);

        // Verificación
        assertThat(result).isFalse();
    }

    @Test
    public void testCambiarEstado_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        String nuevoEstado = "Aprobada";
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

        // Ejecución
        ejecutivoService.cambiarEstado(solicitudId, nuevoEstado);

        // Verificación
        assertThat(solicitud.getEstado()).isEqualTo(nuevoEstado);
        verify(solicitudRepository, times(1)).save(solicitud);
    }

    @Test
    public void testCambiarEstado_SolicitudNotFound() {
        // Datos de prueba
        Long solicitudId = 1L;
        String nuevoEstado = "Aprobada";

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> ejecutivoService.cambiarEstado(solicitudId, nuevoEstado));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    public void testObtenerArchivosDeSolicitud_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);

        Archivo archivo = new Archivo();
        archivo.setTipoDocumento("Comprobante");
        archivo.setPdfData("PDF data".getBytes());
        solicitud.setArchivos(Collections.singletonList(archivo));

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

        // Ejecución
        List<ArchivoDTO> archivosDTO = ejecutivoService.obtenerArchivosDeSolicitud(solicitudId);

        // Verificación
        assertThat(archivosDTO).hasSize(1);
        assertThat(archivosDTO.get(0).getTipoDocumento()).isEqualTo("Comprobante");
        assertThat(archivosDTO.get(0).getContenidoBase64()).isEqualTo(Base64.getEncoder().encodeToString("PDF data".getBytes()));
        verify(solicitudRepository, times(1)).findById(solicitudId);
    }

    @Test
    public void testObtenerArchivosDeSolicitud_SolicitudNotFound() {
        // Datos de prueba
        Long solicitudId = 1L;

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> ejecutivoService.obtenerArchivosDeSolicitud(solicitudId));
        verify(solicitudRepository, times(1)).findById(solicitudId);
    }

    @Test
    public void testAñadirComentario_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        String comentarioExistente = "Comentario inicial.";
        String nuevoComentario = "Comentario adicional.";
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);
        solicitud.setComentario(comentarioExistente);

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

        // Ejecución
        ejecutivoService.añadirComentario(solicitudId, nuevoComentario);

        // Verificación
        assertThat(solicitud.getComentario()).isEqualTo(comentarioExistente + " " + nuevoComentario);
        verify(solicitudRepository, times(1)).save(solicitud);
    }

    @Test
    public void testAñadirComentario_SolicitudNotFound() {
        // Datos de prueba
        Long solicitudId = 1L;
        String nuevoComentario = "Comentario adicional.";

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> ejecutivoService.añadirComentario(solicitudId, nuevoComentario));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }
}

