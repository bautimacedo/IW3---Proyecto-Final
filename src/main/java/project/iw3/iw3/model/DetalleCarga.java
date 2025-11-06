package project.iw3.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "detalle_cargas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetalleCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    private Orden orden;

    @Column(nullable = false)
    private Double masaAcumulada;

    @Column(nullable = false)
    private Double densidad;

    @Column(nullable = false)
    private Double temperatura;

    @Column(nullable = false)
    private Double caudal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date estampaTiempo;
}
