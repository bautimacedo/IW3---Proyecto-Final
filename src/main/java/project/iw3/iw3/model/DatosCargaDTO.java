package project.iw3.iw3.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DatosCargaDTO {
    private Integer numeroOrden; 
    private Integer password;
    private Double masa;
    private Double densidad;
    private Double temperatura;
    private Double caudal;

}
