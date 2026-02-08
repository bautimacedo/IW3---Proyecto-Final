package project.iw3.iw3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistorialCargaDTO {
    private String fechaHora;
    private Double temperatura;
    private Double masa;
    private Double densidad;
    private Double caudal;
}
