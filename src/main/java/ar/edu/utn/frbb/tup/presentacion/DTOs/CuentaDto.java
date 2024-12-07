package ar.edu.utn.frbb.tup.presentacion.DTOs;

import ar.edu.utn.frbb.tup.modelo.TipoCuenta;
import ar.edu.utn.frbb.tup.modelo.TipoMoneda;

public class CuentaDto {
    private Long dniTitular;
    private Long cbu;
    private TipoCuenta tipoCuenta;
    private TipoMoneda tipoMoneda;
    private String alias;

    public Long getDniTitular() {
        return dniTitular;
    }

    public void setDniTitular(Long dniTitular) {
        this.dniTitular = dniTitular;
    }

    public Long getCbu() {
        return cbu;
    }

    public void setCbu(Long cbu) {
        this.cbu = cbu;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
