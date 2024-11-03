package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.Archivo;
import backend.PrestaBanco.entities.Cliente;
import backend.PrestaBanco.entities.Solicitud;
import backend.PrestaBanco.entities.dto.SolicitudDTO;
import backend.PrestaBanco.repositories.ClienteRepository;
import backend.PrestaBanco.repositories.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SolicitudServiceTest {

    @Autowired
    private SolicitudService solicitudService;

    @MockBean
    private SolicitudRepository solicitudRepository;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private CalculosService calculosService;

    @Test
    public void testGetSolicitudById_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

        // Ejecución
        Solicitud result = solicitudService.getSolicitudById(solicitudId);

        // Verificación
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(solicitudId);
        verify(solicitudRepository, times(1)).findById(solicitudId);
    }

    @Test
    public void testGetSolicitudById_NotFound() {
        // Datos de prueba
        Long solicitudId = 1L;

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> solicitudService.getSolicitudById(solicitudId));
        verify(solicitudRepository, times(1)).findById(solicitudId);
    }

    @Test
    public void testGuardarSolicitud_Successful() {
        // Datos de prueba
        String rutCliente = "12345678-9";
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setRutCliente(rutCliente);

        Cliente cliente = new Cliente();
        cliente.setRut(rutCliente);

        Solicitud solicitud = new Solicitud();
        solicitud.setCliente(cliente);

        // Mock del repositorio
        when(clienteRepository.findByRut(rutCliente)).thenReturn(Optional.of(cliente));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        // Ejecución
        Solicitud result = solicitudService.guardarSolicitud(solicitudDTO);

        // Verificación
        assertThat(result).isNotNull();
        assertThat(result.getCliente().getRut()).isEqualTo(rutCliente);
        verify(clienteRepository, times(1)).findByRut(rutCliente);
        verify(solicitudRepository, times(1)).save(any(Solicitud.class));
    }

    @Test
    public void testGuardarSolicitud_ClienteNotFound() {
        // Datos de prueba
        String rutCliente = "12345678-9";
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setRutCliente(rutCliente);

        // Mock del repositorio para devolver vacío
        when(clienteRepository.findByRut(rutCliente)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> solicitudService.guardarSolicitud(solicitudDTO));
        verify(clienteRepository, times(1)).findByRut(rutCliente);
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    public void testGetAllPending_Successful() {
        // Datos de prueba
        Solicitud solicitud1 = new Solicitud();
        solicitud1.setId(1L);
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);

        // Mock del repositorio
        when(solicitudRepository.findAllExcludingEstados()).thenReturn(List.of(solicitud1, solicitud2));

        // Ejecución
        List<Solicitud> result = solicitudService.getAllPending();

        // Verificación
        assertThat(result).hasSize(2);
        verify(solicitudRepository, times(1)).findAllExcludingEstados();
    }

    @Test
    public void testGetAllPending_NoResults() {
        // Mock del repositorio para devolver una lista vacía
        when(solicitudRepository.findAllExcludingEstados()).thenReturn(Collections.emptyList());

        // Ejecución
        List<Solicitud> result = solicitudService.getAllPending();

        // Verificación
        assertThat(result).isEmpty();
        verify(solicitudRepository, times(1)).findAllExcludingEstados();
    }

    @Test
    public void testGetSolicitudesByRut_Successful() {
        // Datos de prueba
        String rutCliente = "12345678-9";
        Solicitud solicitud1 = new Solicitud();
        solicitud1.setId(1L);
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);

        // Mock del repositorio
        when(solicitudRepository.findByClienteRut(rutCliente)).thenReturn(List.of(solicitud1, solicitud2));

        // Ejecución
        List<Solicitud> result = solicitudService.getSolicitudesByRut(rutCliente);

        // Verificación
        assertThat(result).hasSize(2);
        verify(solicitudRepository, times(1)).findByClienteRut(rutCliente);
    }

    @Test
    public void testGetSolicitudesByRut_NoResults() {
        // Datos de prueba
        String rutCliente = "12345678-9";

        // Mock del repositorio para devolver una lista vacía
        when(solicitudRepository.findByClienteRut(rutCliente)).thenReturn(Collections.emptyList());

        // Ejecución
        List<Solicitud> result = solicitudService.getSolicitudesByRut(rutCliente);

        // Verificación
        assertThat(result).isEmpty();
        verify(solicitudRepository, times(1)).findByClienteRut(rutCliente);
    }

    @Test
    public void testGuardarArchivoPdf_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        byte[] pdfData = "PDF data".getBytes();
        String tipoDocumento = "Comprobante";

        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenReturn(solicitud);

        // Ejecución
        solicitudService.guardarArchivoPdf(solicitudId, pdfData, tipoDocumento);

        // Verificación
        assertThat(solicitud.getArchivos()).hasSize(1);
        Archivo archivo = solicitud.getArchivos().get(0);
        assertThat(archivo.getTipoDocumento()).isEqualTo(tipoDocumento);
        assertThat(archivo.getPdfData()).isEqualTo(pdfData);
        verify(solicitudRepository, times(1)).save(solicitud);
    }

    @Test
    public void testGuardarArchivoPdf_SolicitudNotFound() {
        // Datos de prueba
        Long solicitudId = 1L;
        byte[] pdfData = "PDF data".getBytes();
        String tipoDocumento = "Comprobante";

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> solicitudService.guardarArchivoPdf(solicitudId, pdfData, tipoDocumento));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    public void testActualizarDatos_Successful() {
        // Datos de prueba para el DTO
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setSolicitudId(1L);
        solicitudDTO.setRutCliente("12345678-9");
        solicitudDTO.setTipoCredito("Primera Vivienda");
        solicitudDTO.setMonto(1000000);
        solicitudDTO.setValorPropiedad(1500000);
        solicitudDTO.setInteres(4.5);
        solicitudDTO.setPlazo(20);
        solicitudDTO.setImpagas(false);
        solicitudDTO.setDeudaMensual(0);
        solicitudDTO.setAntiguedadLaboral(2);
        solicitudDTO.setSueldoPromedio(500000);
        solicitudDTO.setSaldoAhorro(200000);
        solicitudDTO.setRetiroMax(50000);
        solicitudDTO.setAntiguedadAhorro(1);
        solicitudDTO.setDepositos(Map.of(1, 30000, 6, 30000, 11, 30000)); // Ejemplo de depósitos

        // Crear un cliente de prueba
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombre("John");
        cliente.setApellido("Doe");
        cliente.setEdad(35); // Edad necesaria para evaluar la regla de edad

        // Crear la solicitud inicial y configurar todos los atributos, incluyendo el cliente
        Solicitud solicitud = new Solicitud();
        solicitud.setId(1L);
        solicitud.setSaldoAhorro(200000);
        solicitud.setRetiroMax(50000);
        solicitud.setSueldoPromedio(500000);
        solicitud.setAntiguedadAhorro(1);
        solicitud.setDepositos(solicitudDTO.getDepositos());
        solicitud.setDeudaMensual(0);
        solicitud.setTipoCredito("Primera Vivienda");
        solicitud.setEstado("Inicial");
        solicitud.setCliente(cliente); // Asocia el cliente a la solicitud

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudDTO.getSolicitudId())).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        // Ejecución
        Solicitud updatedSolicitud = solicitudService.actualizarDatos(solicitudDTO);

        // Verificación
        assertThat(updatedSolicitud.getMonto()).isEqualTo(solicitudDTO.getMonto());
        assertThat(updatedSolicitud.getValorPropiedad()).isEqualTo(solicitudDTO.getValorPropiedad());
        assertThat(updatedSolicitud.getInteres()).isEqualTo(solicitudDTO.getInteres());
        assertThat(updatedSolicitud.getPlazo()).isEqualTo(solicitudDTO.getPlazo());
        assertThat(updatedSolicitud.getEstado()).isEqualTo("En Evaluacion");
        verify(solicitudRepository, times(1)).save(solicitud);
    }

    @Test
    public void testActualizarDatos_SolicitudNotFound() {
        // Datos de prueba
        SolicitudDTO solicitudDTO = new SolicitudDTO();
        solicitudDTO.setSolicitudId(1L);

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudDTO.getSolicitudId())).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> solicitudService.actualizarDatos(solicitudDTO));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    public void testObtenerDatosClientePorSolicitud_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        Solicitud solicitud = new Solicitud();
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        solicitud.setCliente(cliente);

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

        // Ejecución
        Map<String, String> datosCliente = solicitudService.obtenerDatosClientePorSolicitud(solicitudId);

        // Verificación
        assertThat(datosCliente.get("rut")).isEqualTo("12345678-9");
        assertThat(datosCliente.get("nombre")).isEqualTo("Juan Pérez");
    }

    @Test
    public void testObtenerDatosClientePorSolicitud_SolicitudNotFound() {
        // Datos de prueba
        Long solicitudId = 1L;

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> solicitudService.obtenerDatosClientePorSolicitud(solicitudId));
    }

    @Test
    public void testCancelar_Successful() {
        // Datos de prueba
        Long solicitudId = 1L;
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);

        // Mock del repositorio
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));

        // Ejecución
        solicitudService.cancelar(solicitudId);

        // Verificación
        assertThat(solicitud.getEstado()).isEqualTo("Cancelada por el Cliente");
        verify(solicitudRepository, times(1)).save(solicitud);
    }

    @Test
    public void testCancelar_SolicitudNotFound() {
        // Datos de prueba
        Long solicitudId = 1L;

        // Mock del repositorio para devolver vacío
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        // Ejecución y verificación de excepción
        assertThrows(IllegalArgumentException.class, () -> solicitudService.cancelar(solicitudId));
        verify(solicitudRepository, never()).save(any(Solicitud.class));
    }

    @Test
    public void testEvaluarReglasAhorro_CapacidadAhorroInsuficiente() {
        // Configurar la solicitud de prueba
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(1000000); // Monto del crédito
        solicitud.setSaldoAhorro(50000); // Saldo de ahorro bajo
        solicitud.setRetiroMax(30000); // Retiro máximo
        solicitud.setSueldoPromedio(500000); // Sueldo promedio mensual
        solicitud.setAntiguedadAhorro(1); // Antigüedad en años

        // Map para depósitos
        Map<Integer, Integer> depositos = new HashMap<>();
        for (int i = 1; i <= 12; i++) depositos.put(i, 10000); // Depósitos bajos
        solicitud.setDepositos(depositos);

        // Ejecutar el método a probar
        solicitudService.evaluarReglasAhorro(solicitud);

        // Verificar que la solicitud se rechaza debido a capacidad de ahorro insuficiente
        assertThat(solicitud.getEstado()).isEqualTo("Rechazada");
        assertThat(solicitud.getComentario()).contains("Capacidad de ahorro insuficiente");
    }

    @Test
    public void testEvaluarReglasAhorro_CapacidadAhorroSuficiente() {
        // Configurar la solicitud de prueba
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(1000000);
        solicitud.setSaldoAhorro(150000); // Saldo de ahorro suficiente
        solicitud.setRetiroMax(20000); // Retiro máximo bajo
        solicitud.setSueldoPromedio(500000); // Sueldo promedio
        solicitud.setAntiguedadAhorro(3); // Antigüedad suficiente

        Map<Integer, Integer> depositos = new HashMap<>();
        for (int i = 1; i <= 12; i++) depositos.put(i, 30000); // Depósitos altos
        solicitud.setDepositos(depositos);

        // Ejecutar el método a probar
        solicitudService.evaluarReglasAhorro(solicitud);

        // Verificar que el estado no es rechazado y cumple suficientes reglas
        assertThat(solicitud.getEstado()).isNotEqualTo("Rechazada");
        long reglasCumplidas = solicitud.getReglasAhorro().stream().filter(Boolean::booleanValue).count();
        assertThat(reglasCumplidas).isGreaterThan(2);
    }

    @Test
    public void testTieneDepositosPeriodicos_True() {
        // Configurar depósitos suficientes en cada trimestre
        Map<Integer, Integer> depositos = new HashMap<>();
        depositos.put(1, 30000);  // Primer periodo
        depositos.put(6, 30000);  // Segundo periodo
        depositos.put(10, 30000); // Tercer periodo
        int sueldoPromedio = 500000;

        // Ejecutar el método y verificar que devuelve true
        boolean result = solicitudService.tieneDepositosPeriodicos(depositos, sueldoPromedio);
        assertThat(result).isTrue();
    }

    @Test
    public void testTieneDepositosPeriodicos_False() {
        // Configurar depósitos insuficientes en algunos trimestres
        Map<Integer, Integer> depositos = new HashMap<>();
        depositos.put(1, 30000); // Solo primer periodo tiene depósitos
        int sueldoPromedio = 500000;

        // Ejecutar el método y verificar que devuelve false
        boolean result = solicitudService.tieneDepositosPeriodicos(depositos, sueldoPromedio);
        assertThat(result).isFalse();
    }

    @Test
    public void testEvaluarReglasAhorro_ReglasEspecificas() {
        // Configurar la solicitud de prueba
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(1000000);
        solicitud.setSaldoAhorro(200000); // Saldo suficiente para cumplir R71 y R74
        solicitud.setRetiroMax(40000); // Retiro dentro del límite para R72 y R75
        solicitud.setSueldoPromedio(500000);
        solicitud.setAntiguedadAhorro(1);

        // Configurar depósitos periódicos suficientes
        Map<Integer, Integer> depositos = new HashMap<>();
        depositos.put(2, 30000); // Primer periodo
        depositos.put(6, 30000); // Segundo periodo
        depositos.put(11, 30000); // Tercer periodo
        solicitud.setDepositos(depositos);

        // Ejecutar el método a probar
        solicitudService.evaluarReglasAhorro(solicitud);

        // Verificar cada regla cumplida en reglas de ahorro
        List<Boolean> reglasCumplidas = solicitud.getReglasAhorro();
        assertThat(reglasCumplidas.get(0)).isTrue(); // R71: Saldo Mínimo Requerido
        assertThat(reglasCumplidas.get(1)).isTrue(); // R72: Historial de Ahorro Consistente
        assertThat(reglasCumplidas.get(2)).isTrue(); // R73: Depósitos Periódicos
        assertThat(reglasCumplidas.get(3)).isTrue(); // R74: Relación Saldo/Años de Antigüedad
        assertThat(reglasCumplidas.get(4)).isTrue(); // R75: Retiros Recientes
    }

    @Test
    public void testCostoMensual() {
        // Configurar la solicitud de prueba
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(1000000);
        solicitud.setPlazo(20);
        solicitud.setInteres(4.5);

        // Simular el retorno de los cálculos
        when(calculosService.calcularCuotaMensual(1000000, 20, 4.5)).thenReturn(6326);
        when(calculosService.calcularCostoMensual(6326, 1000000)).thenReturn(26327);

        // Ejecutar el método a probar
        solicitudService.costoMensual(solicitud);

        // Verificar que el costo mensual se ha calculado y establecido correctamente
        assertThat(solicitud.getCostoMensual()).isEqualTo(26327);
    }

    @Test
    public void testEsRelacionCuotaIngresoExcedida_CuotaMenor() {
        // Configurar la solicitud de prueba con cuota mensual dentro del límite
        Solicitud solicitud = new Solicitud();
        solicitud.setCostoMensual(100000);
        solicitud.setSueldoPromedio(400000);

        // Ejecutar el método y verificar que la relación cuota/ingreso no excede el límite
        boolean excedida = solicitudService.esRelacionCuotaIngresoExcedida(solicitud);
        assertThat(excedida).isFalse();
    }

    @Test
    public void testEsRelacionCuotaIngresoExcedida_CuotaMayor() {
        // Configurar la solicitud de prueba con cuota mensual que excede el límite
        Solicitud solicitud = new Solicitud();
        solicitud.setCostoMensual(200000);
        solicitud.setSueldoPromedio(500000);

        // Ejecutar el método y verificar que la relación cuota/ingreso excede el límite
        boolean excedida = solicitudService.esRelacionCuotaIngresoExcedida(solicitud);
        assertThat(excedida).isTrue();
    }

    @Test
    public void testEvaluarReglasGenerales_TodasCumplen() {
        // Crear y configurar el cliente de prueba
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombre("John");
        cliente.setApellido("Doe");
        cliente.setEdad(35); // Edad requerida para la regla de edad

        // Configurar una solicitud con condiciones para cumplir todas las reglas
        Solicitud solicitud = crearSolicitudConValores(1000000, 2000000, 4.5, 20, 400000, false, 2);
        solicitud.setCliente(cliente); // Asociar el cliente a la solicitud
        solicitud.setTipoCredito("Primera Vivienda"); // Configurar explícitamente el tipo de crédito
        solicitud.setEstado("Inicial"); // Configurar estado inicial para evitar null

        // Simular el cálculo de la cuota mensual y costos en CalculosService
        when(calculosService.calcularCuotaMensual(1000000, 20, 4.5)).thenReturn(6326);
        when(calculosService.calcularCostoMensual(6326, 1000000)).thenReturn(26327);

        // Calcula el costo mensual antes de evaluar las reglas
        solicitudService.costoMensual(solicitud);

        // Ejecutar el método a probar
        solicitudService.evaluarReglasGenerales(solicitud);

        // Verificar que el estado de la solicitud es "En Evaluacion" y todas las reglas se cumplen
        assertThat(solicitud.getEstado()).isEqualTo("En Evaluacion");
        assertThat(solicitud.getReglasGenerales()).containsOnly(true);
    }


    @Test
    public void testEvaluarReglasGenerales_AlgunasNoCumplen() {
        // Crear y configurar el cliente de prueba
        Cliente cliente = new Cliente();
        cliente.setRut("12345678-9");
        cliente.setNombre("John");
        cliente.setApellido("Doe");
        cliente.setEdad(35); // Edad necesaria para evaluar la regla de edad

        // Configurar una solicitud que incumple la regla de cuota/ingreso y otras reglas
        Solicitud solicitud = crearSolicitudConValores(1000000, 800000, 4.5, 20, 50000, true, 0);
        solicitud.setCliente(cliente); // Asociar el cliente a la solicitud
        solicitud.setTipoCredito("Primera Vivienda"); // Asegurar el tipo de crédito
        solicitud.setEstado("Inicial"); // Estado inicial para evitar NullPointerException

        // Simular el cálculo de la cuota mensual y costos en CalculosService
        when(calculosService.calcularCuotaMensual(1000000, 20, 4.5)).thenReturn(6326);
        when(calculosService.calcularCostoMensual(6326, 1000000)).thenReturn(26327);

        // Calcular el costo mensual antes de evaluar las reglas
        solicitudService.costoMensual(solicitud);

        // Ejecutar el método a probar
        solicitudService.evaluarReglasGenerales(solicitud);

        // Verificar que el estado de la solicitud es "Rechazada" y que se generan los comentarios esperados
        assertThat(solicitud.getEstado()).isEqualTo("Rechazada");
        assertThat(solicitud.getComentario()).contains("Relación cuota/ingreso excede el límite permitido");
        assertThat(solicitud.getReglasGenerales()).contains(false);
    }


    private Solicitud crearSolicitudConValores(int monto, int valorPropiedad, double interes, int plazo,
                                               int sueldoPromedio, boolean impagas, int antiguedadLaboral) {
        Solicitud solicitud = new Solicitud();
        Cliente cliente = new Cliente();
        cliente.setEdad(30);
        solicitud.setCliente(cliente);
        solicitud.setMonto(monto);
        solicitud.setValorPropiedad(valorPropiedad);
        solicitud.setInteres(interes);
        solicitud.setPlazo(plazo);
        solicitud.setSueldoPromedio(sueldoPromedio);
        solicitud.setImpagas(impagas);
        solicitud.setAntiguedadLaboral(antiguedadLaboral);
        solicitud.setDeudaMensual(50000); // deuda dentro del límite permitido para algunos tests
        return solicitud;
    }
}
