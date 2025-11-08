package project.iw3.iw3.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConciliacionDTO {
    private Integer numeroOrden;
    private Double tara;  // pesaje inicial
    private Double pesoFinal;  // pesaje final
    private Double productoCargado; // ultimo valor de masa acumulada
    private Double netoPorBalanza; // pesoFinal - tara
    private Double diferencia; // netoPorBalanza - productoCargado
    private Double promedioTemperatura; // promedio de temperaturas registradas
    private Double promedioDensidad;
    private Double promedioCaudal;
    private Date fechaPesajeFinal;
}
