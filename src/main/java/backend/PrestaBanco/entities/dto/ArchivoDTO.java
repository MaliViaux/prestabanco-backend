package backend.PrestaBanco.entities.dto;

import lombok.Data;

@Data
public class ArchivoDTO {
    private String tipoDocumento;
    private String contenidoBase64;

    public ArchivoDTO(String tipoDocumento, String contenidoBase64) {
        this.tipoDocumento = tipoDocumento;
        this.contenidoBase64 = contenidoBase64;
    }

    // Getters y setters
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getContenidoBase64() {
        return contenidoBase64;
    }

    public void setContenidoBase64(String contenidoBase64) {
        this.contenidoBase64 = contenidoBase64;
    }
}
