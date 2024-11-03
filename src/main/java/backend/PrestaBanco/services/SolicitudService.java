package backend.PrestaBanco.services;

import backend.PrestaBanco.entities.Archivo;
import backend.PrestaBanco.entities.Cliente;
import backend.PrestaBanco.entities.Solicitud;
import backend.PrestaBanco.entities.dto.SolicitudDTO;
import backend.PrestaBanco.repositories.ClienteRepository;
import backend.PrestaBanco.repositories.SolicitudRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SolicitudService {
    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CalculosService calculosService;

    public Solicitud getSolicitudById(Long solicitudId) {
        return solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada con ID: " + solicitudId));
    }

    @Transactional
    public Solicitud guardarSolicitud(SolicitudDTO solicitudDTO) {
        // Buscar el cliente usando el RUT
        Cliente cliente = clienteRepository.findByRut(solicitudDTO.getRutCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente con RUT " + solicitudDTO.getRutCliente() + " no encontrado."));

        // Crear la solicitud y establecer los datos
        Solicitud solicitud = new Solicitud();
        solicitud.setCliente(cliente);
        solicitud.setTipoCredito(solicitudDTO.getTipoCredito());
        solicitud.setImpagas(solicitudDTO.isImpagas());
        solicitud.setDeudaMensual(solicitudDTO.getDeudaMensual());
        solicitud.setAntiguedadLaboral(solicitudDTO.getAntiguedadLaboral());
        solicitud.setSueldoPromedio(solicitudDTO.getSueldoPromedio());
        solicitud.setSaldoAhorro(solicitudDTO.getSaldoAhorro());
        solicitud.setRetiroMax(solicitudDTO.getRetiroMax());
        solicitud.setDepositos(solicitudDTO.getDepositos());
        solicitud.setAntiguedadAhorro(solicitudDTO.getAntiguedadAhorro());

        // Guardar la solicitud en la base de datos
        return solicitudRepository.save(solicitud);
    }

    public List<Solicitud> getAllPending() {
        return solicitudRepository.findAllExcludingEstados();
    }

    public List<Solicitud> getSolicitudesByRut(String rut) {
        return solicitudRepository.findByClienteRut(rut);
    }

    @Transactional
    public void guardarArchivoPdf(Long solicitudId, byte[] pdfData, String tipoDocumento) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        Archivo archivo = new Archivo();
        archivo.setSolicitud(solicitud);
        archivo.setPdfData(pdfData);
        archivo.setTipoDocumento(tipoDocumento);

        solicitud.getArchivos().add(archivo);
        solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarDatos(SolicitudDTO solicitudDTO) {
        Solicitud solicitud = solicitudRepository.findById(solicitudDTO.getSolicitudId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrado"));

        solicitud.setMonto(solicitudDTO.getMonto());
        solicitud.setValorPropiedad(solicitudDTO.getValorPropiedad());
        solicitud.setInteres(solicitudDTO.getInteres());
        solicitud.setPlazo(solicitudDTO.getPlazo());
        solicitud.setEstado("En Revisión Inicial");
        solicitud.setFecha(new Date()); // Establece la fecha actual como fecha de la solicitud
        costoMensual(solicitud);
        evaluarReglasAhorro(solicitud);
        solicitud.setDeudaMensual(solicitud.getDeudaMensual() + solicitud.getCostoMensual());
        evaluarReglasGenerales(solicitud);

        // Guardar la solicitud en la base de datos
        return solicitudRepository.save(solicitud);
    }

    public Map<String, String> obtenerDatosClientePorSolicitud(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        Cliente cliente = solicitud.getCliente();

        Map<String, String> datosCliente = new HashMap<>();
        datosCliente.put("rut", cliente.getRut());
        datosCliente.put("nombre", cliente.getNombre() + " " + cliente.getApellido());

        return datosCliente;
    }

    @Transactional
    public void cancelar(Long solicitudId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
        solicitud.setEstado("Cancelada por el Cliente");
        solicitudRepository.save(solicitud);
    }

    public void evaluarReglasAhorro(Solicitud solicitud) {
        List<Boolean> reglasCumplidas = solicitud.getReglasAhorro();
        reglasCumplidas.clear(); // Limpiamos la lista para asegurarnos de no duplicar resultados anteriores.

        // Regla R71: Saldo Mínimo Requerido
        reglasCumplidas.add(solicitud.getSaldoAhorro() >= 0.1 * solicitud.getMonto());

        // Regla R72: Historial de Ahorro Consistente
        reglasCumplidas.add(solicitud.getRetiroMax() <= 0.5 * solicitud.getSaldoAhorro());

        // Regla R73: Depósitos Periódicos
        reglasCumplidas.add(tieneDepositosPeriodicos(solicitud.getDepositos(), solicitud.getSueldoPromedio()));

        // Regla R74: Relación Saldo/Años de Antigüedad
        reglasCumplidas.add((solicitud.getAntiguedadAhorro() < 2 && solicitud.getSaldoAhorro() >= 0.2 * solicitud.getMonto()) ||
                (solicitud.getAntiguedadAhorro() >= 2 && solicitud.getSaldoAhorro() >= 0.1 * solicitud.getMonto()));

        // Regla R75: Retiros Recientes
        reglasCumplidas.add(solicitud.getRetiroMax() <= 0.3 * solicitud.getSaldoAhorro());

        // Guardar reglas cumplidas en la solicitud
        solicitud.setReglasAhorro(reglasCumplidas);

        // Contar el número de reglas cumplidas
        long countReglasCumplidas = reglasCumplidas.stream().filter(Boolean::booleanValue).count();

        // Verificar si cumple con 2 o menos reglas y rechazar si es el caso
        if (countReglasCumplidas <= 2) {
            solicitud.setEstado("Rechazada");

            // Añadir el comentario "capacidad de ahorro insuficiente" al comentario existente
            String comentarioExistente = solicitud.getComentario() != null ? solicitud.getComentario() + " " : "";
            solicitud.setComentario(comentarioExistente + "Capacidad de ahorro insuficiente. ");
        }
    }

    public boolean tieneDepositosPeriodicos(Map<Integer, Integer> depositos, Integer sueldoPromedio) {
        boolean primerPeriodo = false;
        boolean segundoPeriodo = false;
        boolean tercerPeriodo = false;
        // Map para acumular la suma de depósitos de cada mes
        Map<Integer, Integer> sumaDepositosPorMes = new HashMap<>();

        // Calcular la suma de depósitos por cada mes
        for (Map.Entry<Integer, Integer> deposito : depositos.entrySet()) {
            int mes = deposito.getKey();
            int monto = deposito.getValue();

            sumaDepositosPorMes.put(mes, sumaDepositosPorMes.getOrDefault(mes, 0) + monto);
        }

        // Verificar que la suma de depósitos de cada mes sea al menos el 5% del sueldo promedio
        double umbralDeposito = 0.05 * sueldoPromedio;
        for (Map.Entry<Integer, Integer> entry : sumaDepositosPorMes.entrySet()) {
            int mes = entry.getKey();
            int totalDepositosMes = entry.getValue();

            if (totalDepositosMes >= umbralDeposito) {
                if (mes >= 1 && mes <= 4) {
                    primerPeriodo = true;
                } else if (mes >= 5 && mes <= 8) {
                    segundoPeriodo = true;
                } else if (mes >= 9 && mes <= 12) {
                    tercerPeriodo = true;
                }}}

        return primerPeriodo && segundoPeriodo && tercerPeriodo;
    }

    public void costoMensual(Solicitud solicitud) {
        int cuotaMensual = calculosService.calcularCuotaMensual(solicitud.getMonto(), solicitud.getPlazo(), solicitud.getInteres());
        int costoMensual = calculosService.calcularCostoMensual(cuotaMensual, solicitud.getMonto());
        solicitud.setCostoMensual(costoMensual);}

    public boolean esRelacionCuotaIngresoExcedida(Solicitud solicitud) {
        double relacionCuotaIngreso = (solicitud.getCostoMensual() / (double) solicitud.getSueldoPromedio()) * 100;
        return relacionCuotaIngreso > 35;
    }

    public void evaluarReglasGenerales(Solicitud solicitud) {
        List<Boolean> reglasCumplidas = new ArrayList<>();
        boolean estadoSolicitud = true;
        StringBuilder comentario = new StringBuilder(); // StringBuilder para construir los comentarios

        // R1. Relación Cuota/Ingreso
        boolean reglaCuotaIngreso = !esRelacionCuotaIngresoExcedida(solicitud);
        reglasCumplidas.add(reglaCuotaIngreso);
        if (!reglaCuotaIngreso) {
            estadoSolicitud = false;
            comentario.append("Relación cuota/ingreso excede el límite permitido. ");}

        // R2. Historial Crediticio del Cliente
        boolean reglaHistorialCrediticio = !solicitud.isImpagas();
        reglasCumplidas.add(reglaHistorialCrediticio);
        if (!reglaHistorialCrediticio) {
            estadoSolicitud = false;
            comentario.append("Tiene deudas impagas. ");}

        // R3. Antigüedad Laboral y Estabilidad
        boolean reglaAntiguedadLaboral;
        if (solicitud.getAntiguedadLaboral() == null) {
            reglaAntiguedadLaboral = true;
        } else {
            reglaAntiguedadLaboral = solicitud.getAntiguedadLaboral() >= 1;
        }
        reglasCumplidas.add(reglaAntiguedadLaboral);
        if (!reglaAntiguedadLaboral) {
            estadoSolicitud = false;
            comentario.append("Antigüedad laboral insuficiente. ");}

        // R4. Relación Deuda/Ingreso
        boolean reglaRelacionDeudaIngreso = solicitud.getDeudaMensual() <= 0.5 * solicitud.getSueldoPromedio();
        reglasCumplidas.add(reglaRelacionDeudaIngreso);
        if (!reglaRelacionDeudaIngreso) {
            estadoSolicitud = false;
            comentario.append("Relación deuda/ingreso excede el 50%. ");}

        // R5. Monto Máximo de Financiamiento
        double porcentajeMaximo = switch (solicitud.getTipoCredito()) {
            case "Primera Vivienda" -> 0.8;
            case "Segunda Vivienda" -> 0.7;
            case "Propiedades Comerciales" -> 0.6;
            case "Remodelacion" -> 0.5;
            default -> 1.0;}; // Asumimos sin límite si no es un tipo conocido

        boolean reglaMontoMaximoFinanciamiento = solicitud.getMonto() <= porcentajeMaximo * solicitud.getValorPropiedad();
        reglasCumplidas.add(reglaMontoMaximoFinanciamiento);
        if (!reglaMontoMaximoFinanciamiento) {
            estadoSolicitud = false;
            comentario.append("Monto solicitado excede el financiamiento permitido. ");}
        // R6. Edad del Solicitante
        int edadCliente = solicitud.getCliente().getEdad();
        int edadAlFinalizar = edadCliente + solicitud.getPlazo();
        boolean reglaEdadSolicitante = edadAlFinalizar <= 70; // margen de 5 años antes de 75
        reglasCumplidas.add(reglaEdadSolicitante);
        if (!reglaEdadSolicitante) {
            estadoSolicitud = false;
            comentario.append("El plazo del préstamo excede el límite de edad permitido. ");}
        // Almacenar las reglas generales cumplidas en la solicitud
        solicitud.setReglasGenerales(reglasCumplidas);


        if (!estadoSolicitud) { // Almacenar comentarios si hay reglas incumplidas
            String comentarioExistente = solicitud.getComentario() != null ? solicitud.getComentario() + " " : "";
            solicitud.setComentario(comentarioExistente + comentario.toString().trim());}
        // Si alguna regla no se cumple, se rechaza la solicitud, pero si ya está en "Rechazada", no cambiar el estado
        if (!solicitud.getEstado().equals("Rechazada")) {
            solicitud.setEstado(estadoSolicitud ? "En Evaluacion" : "Rechazada");
        }}
}
