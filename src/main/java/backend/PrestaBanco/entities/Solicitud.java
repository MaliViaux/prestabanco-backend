package backend.PrestaBanco.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "solicitud")
@Getter
@Setter
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="cliente_rut", referencedColumnName = "rut", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "tipoCredito")
    private String tipoCredito;

    @Column(name = "monto")
    private Integer monto;

    @Column(name = "valorPropiedad")
    private Integer valorPropiedad;

    @Column(name = "interes")
    private Double interes;

    @Column(name = "plazo")
    private Integer plazo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha")
    private Date fecha;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Archivo> archivos = new ArrayList<>();

    @Column(name = "impagas")
    private boolean impagas; // tiene o no deudas impagas

    @Column(name = "deuda mensual")
    private Integer deudaMensual; // suma de las deudas actuales (monto mensual)

    @Column(name = "antiguedad laboral")
    private Integer antiguedadLaboral; // antiguedad laboral en años, si no se ingresa es trabajador independiente

    @Column(name = "sueldo promedio")
    private Integer sueldoPromedio; // sueldo mensual promedio, en caso de trabajadores
    // independientes es el promedio de los ultimos 2 años

    @Column(name = "saldo ahorro")
    private Integer saldoAhorro; // saldo en la cuenta de ahorro

    @Column(name = "retiro max")
    private Integer retiroMax; // monto maximo de los retiros ultimos 12 meses

    @ElementCollection
    @CollectionTable(name = "depositos", joinColumns = @JoinColumn(name = "solicitud_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "mes")
    @Column(name = "monto")
    private Map<Integer, Integer> depositos = new HashMap<>();

    @Column(name = "antiguedad ahorro")
    private Integer antiguedadAhorro; // antiguedad en años de la cuenta de ahorro

    @ElementCollection
    @CollectionTable(name = "reglas_cumplidas", joinColumns = @JoinColumn(name = "solicitud_id"))
    @Column(name = "regla")
    private List<Boolean> reglasAhorro = new ArrayList<>();

    @Column(name = "costo mensual")
    private Integer costoMensual; // costo total mensual, cuota mensual + seguros

    @ElementCollection
    @CollectionTable(name = "reglas_generales", joinColumns = @JoinColumn(name = "solicitud_id"))
    @Column(name = "regla general")
    private List<Boolean> reglasGenerales = new ArrayList<>();

    @Column(name = "comentario")
    private String comentario;
}
