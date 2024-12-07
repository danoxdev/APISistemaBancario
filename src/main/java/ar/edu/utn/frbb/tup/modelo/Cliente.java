package ar.edu.utn.frbb.tup.modelo;

import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Cliente extends Persona{

    private TipoPersona tipoPersona;
    private String banco;
    private LocalDate fechaAlta;
    private Set<Cuenta> cuentas = new HashSet<>();

    public Cliente(){
        super();
        this.fechaAlta = LocalDate.now();
    }


    public Cliente(ClienteDto clienteDto) {
        super(
                clienteDto.getNombre(),
                clienteDto.getApellido(),
                clienteDto.getDni(),
                clienteDto.getFechaNacimiento(),
                clienteDto.getDomicilio()
        );
        this.banco = clienteDto.getBanco();
        this.tipoPersona = TipoPersona.valueOf(clienteDto.getTipoPersona());
        this.fechaAlta = LocalDate.now();
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Set<Cuenta> getCuentas() {
        return cuentas;
    }

    public void addCuenta(Cuenta cuenta) {
        this.cuentas.add(cuenta);
    }

    public void removeCuenta(Cuenta cuenta){
        this.cuentas.remove(cuenta);
    }

}
