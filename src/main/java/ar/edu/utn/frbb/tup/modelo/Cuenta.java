package ar.edu.utn.frbb.tup.modelo;

import java.time.LocalDate;


public class Cuenta {
    private Long dniTitular;
    private Long cbu;
    private TipoCuenta tipoCuenta;
    private TipoMoneda tipoMoneda;
    private String alias;
    private LocalDate fechaCreacion;
    private double saldo;

    public Long getDniTitular() {
        return dniTitular;
    }

    public void setDniTitular(Long dniTitular) {
        this.dniTitular = dniTitular;
    }

    public String getAlias() {
        return alias;
    }

    public Cuenta setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public Cuenta setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cuenta setSaldo(double saldo) {
        this.saldo = saldo;
        return this;
    }

    public Long getCbu() {
        return cbu;
    }

    public Cuenta setCbu(Long cbu) {
        this.cbu = cbu;
        return this;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

}
