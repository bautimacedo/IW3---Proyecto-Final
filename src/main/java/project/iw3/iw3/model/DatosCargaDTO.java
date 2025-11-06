package project.iw3.iw3.model;

public class DatosCargaDTO {
    private Long orderId;
    private Integer password;
    private Double masa;
    private Double densidad;
    private Double temperatura;
    private Double caudal;

    // Getters y setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Integer getPassword() { return password; }
    public void setPassword(Integer password) { this.password = password; }

    public Double getMasa() { return masa; }
    public void setMasa(Double masa) { this.masa = masa; }

    public Double getDensidad() { return densidad; }
    public void setDensidad(Double densidad) { this.densidad = densidad; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public Double getCaudal() { return caudal; }
    public void setCaudal(Double caudal) { this.caudal = caudal; }
}
