package backend.PrestaBanco.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documento")
@Getter
@Setter
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreDocumento;  // Nombre del archivo (ej. "Comprobante de Ingresos")

    private String tipoDocumento;  // Tipo de archivo (ej. "PDF", "JPEG")

    @Lob
    private byte[] archivo;  // Contenido binario del archivo

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;  // Relación con Solicitud
}
