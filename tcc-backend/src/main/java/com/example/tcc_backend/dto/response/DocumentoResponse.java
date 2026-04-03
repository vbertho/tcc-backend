package com.example.tcc_backend.dto.response;

import com.example.tcc_backend.model.Documento;
import com.example.tcc_backend.model.TipoDocumento;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentoResponse {
    private Integer id;
    private TipoDocumento tipo;
    private String nomeArquivo;
    private LocalDateTime dataEnvio;

    public static DocumentoResponse fromEntity(Documento documento) {
        return DocumentoResponse.builder()
                .id(documento.getId())
                .tipo(documento.getTipo())
                .nomeArquivo(Path.of(documento.getCaminho()).getFileName().toString())
                .dataEnvio(documento.getDataEnvio())
                .build();
    }
}
