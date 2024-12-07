package ar.edu.utn.frbb.tup.presentacion.DTOs;

import ar.edu.utn.frbb.tup.modelo.TipoPersona;

public class ClienteDto extends PersonaDto{
    private String banco;
    private String tipoPersona;

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }
}


