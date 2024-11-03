package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.Cliente;
import backend.PrestaBanco.repositories.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @MockBean
    private ClienteRepository clienteRepository;

    @Test
    public void testValidarLogin_Successful() {
        // Datos de prueba
        String rut = "12345678-9";
        String contraseña = "password123";
        Cliente cliente = new Cliente();
        cliente.setRut(rut);
        cliente.setContraseña(contraseña);

        // Mock del repositorio
        when(clienteRepository.findByRut(rut)).thenReturn(Optional.of(cliente));

        // Ejecución
        boolean result = clienteService.validarLogin(rut, contraseña);

        // Verificación
        assertThat(result).isTrue();
        verify(clienteRepository, times(1)).findByRut(rut);
    }

    @Test
    public void testValidarLogin_Failed_InvalidPassword() {
        // Datos de prueba
        String rut = "12345678-9";
        String contraseña = "wrongpassword";
        Cliente cliente = new Cliente();
        cliente.setRut(rut);
        cliente.setContraseña("password123");

        // Mock del repositorio
        when(clienteRepository.findByRut(rut)).thenReturn(Optional.of(cliente));

        // Ejecución
        boolean result = clienteService.validarLogin(rut, contraseña);

        // Verificación
        assertThat(result).isFalse();
        verify(clienteRepository, times(1)).findByRut(rut);
    }

    @Test
    public void testValidarLogin_Failed_ClientNotFound() {
        // Datos de prueba
        String rut = "12345678-9";
        String contraseña = "password123";

        // Mock del repositorio
        when(clienteRepository.findByRut(rut)).thenReturn(Optional.empty());

        // Ejecución
        boolean result = clienteService.validarLogin(rut, contraseña);

        // Verificación
        assertThat(result).isFalse();
        verify(clienteRepository, times(1)).findByRut(rut);
    }

    @Test
    public void testGetClienteByRut_Successful() {
        // Datos de prueba
        String rut = "12345678-9";
        Cliente cliente = new Cliente();
        cliente.setRut(rut);

        // Mock del repositorio
        when(clienteRepository.findByRut(rut)).thenReturn(Optional.of(cliente));

        // Ejecución
        Optional<Cliente> result = clienteService.getClienteByRut(rut);

        // Verificación
        assertThat(result).isPresent();
        assertThat(result.get().getRut()).isEqualTo(rut);
        verify(clienteRepository, times(1)).findByRut(rut);
    }

    @Test
    public void testGetClienteByRut_NotFound() {
        // Datos de prueba
        String rut = "12345678-9";

        // Mock del repositorio
        when(clienteRepository.findByRut(rut)).thenReturn(Optional.empty());

        // Ejecución
        Optional<Cliente> result = clienteService.getClienteByRut(rut);

        // Verificación
        assertThat(result).isNotPresent();
        verify(clienteRepository, times(1)).findByRut(rut);
    }

    @Test
    public void testGetClienteByNombreApellido_Successful() {
        // Datos de prueba
        String nombre = "Juan";
        String apellido = "Perez";
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);

        // Mock del repositorio
        when(clienteRepository.findByNombreAndApellido(nombre, apellido)).thenReturn(Optional.of(cliente));

        // Ejecución
        Optional<Cliente> result = clienteService.getClienteByNombreApellido(nombre, apellido);

        // Verificación
        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo(nombre);
        assertThat(result.get().getApellido()).isEqualTo(apellido);
        verify(clienteRepository, times(1)).findByNombreAndApellido(nombre, apellido);
    }

    @Test
    public void testGetClienteByNombreApellido_NotFound() {
        // Datos de prueba
        String nombre = "Juan";
        String apellido = "Perez";

        // Mock del repositorio
        when(clienteRepository.findByNombreAndApellido(nombre, apellido)).thenReturn(Optional.empty());

        // Ejecución
        Optional<Cliente> result = clienteService.getClienteByNombreApellido(nombre, apellido);

        // Verificación
        assertThat(result).isNotPresent();
        verify(clienteRepository, times(1)).findByNombreAndApellido(nombre, apellido);
    }

    @Test
    public void testSaveCliente() {
        // Datos de prueba
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");

        // Mock del repositorio
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // Ejecución
        Cliente result = clienteService.saveCliente(cliente);

        // Verificación
        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo(cliente.getRut());
        assertThat(result.getNombre()).isEqualTo(cliente.getNombre());
        assertThat(result.getApellido()).isEqualTo(cliente.getApellido());
        verify(clienteRepository, times(1)).save(cliente);
    }
}

