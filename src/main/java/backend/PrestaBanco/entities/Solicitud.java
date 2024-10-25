package backend.PrestaBanco.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "solicitud")
@Getter
@Setter
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement
    @Column(unique = true, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name="usuario_rut", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Documento> documentos;  // Lista de documentos

    @Column(name = "tipoCredito")
    private String tipoCredito;

    @Column(name = "monto")
    private Integer monto;

    @Column(name = "años")
    private Integer años;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha")
    private Date fecha;
}
