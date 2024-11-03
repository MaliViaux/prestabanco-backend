package backend.PrestaBanco.entities.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SolicitudDTO {
    private Long solicitudId;
    private String rutCliente;
    private String tipoCredito;
    private Integer monto;
    private Integer valorPropiedad;
    private Double interes;
    private Integer plazo;
    private boolean impagas;
    private Integer deudaMensual;
    private Integer antiguedadLaboral; // Antigüedad laboral en años o nulo para independientes
    private Integer sueldoPromedio; // Sueldo mensual promedio
    private Integer saldoAhorro;
    private Integer retiroMax;
    private Map<Integer, Integer> depositos; // Mes como clave y monto como valor
    private Integer antiguedadAhorro;
}
