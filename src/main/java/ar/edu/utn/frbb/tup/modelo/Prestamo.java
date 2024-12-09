package ar.edu.utn.frbb.tup.modelo;

public class Prestamo {
    private int idPrestamo;       // Identificador único del préstamo
    private Long dniCliente;       // DNI del cliente que solicita el préstamo
    private Double monto;          // Monto total del préstamo
    private Integer plazoMeses;    // Plazo en meses para pagar el préstamo
    private Integer pagosRealizados; // Cantidad de pagos realizados hasta la fecha
    private Double saldoRestante;  // Saldo pendiente del préstamo

    // Constructor vacío
    public Prestamo() {}

    // Constructor con parámetros
    public Prestamo(int idPrestamo, Long dniCliente, Double monto, Integer plazoMeses, Integer pagosRealizados, Double saldoRestante) {
        this.idPrestamo = idPrestamo;
        this.dniCliente = dniCliente;
        this.monto = monto;
        this.plazoMeses = plazoMeses;
        this.pagosRealizados = pagosRealizados;
        this.saldoRestante = saldoRestante;
    }

    // Getters y Setters
    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Long getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(Long dniCliente) {
        this.dniCliente = dniCliente;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public Integer getPagosRealizados() {
        return pagosRealizados;
    }

    public void setPagosRealizados(Integer pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }

    public Double getSaldoRestante() {
        return saldoRestante;
    }

    public void setSaldoRestante(Double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

}
