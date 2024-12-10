package ar.edu.utn.frbb.tup.servicios;
import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelo.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.PrestamoDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Service
public class ValidacionesServicios {

    // VALIDACIONES DE CLIENTES //

    public void esMayordeEdad(LocalDate fecha) throws ClienteMenorDeEdadException {
        int edad = LocalDate.now().getYear() - fecha.getYear();
        if (edad < 18) {
            throw new ClienteMenorDeEdadException("El cliente no es mayor de edad");
        }
    }

    public void validarDni(Long dni) {
        try {
            if (dni == 0 || dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error: El dni debe tener entre 7 y 8 digitos");
            }
        } catch(NumberFormatException e){
            throw new IllegalArgumentException("Error: El dni debe ser un numero");
        }
    }

    public void validarClienteExistente(ClienteDto clienteDto) throws ClienteExistenteException {
        ClienteDao clienteDao = new ClienteDao();
        if (clienteDao.findCliente(clienteDto.getDni()) != null){
            throw new ClienteExistenteException("Ya existe un cliente con el DNI ingresado");
        }
    }

    // VALIDACIONES DE CUENTAS //

    public void cuentaMismoTipoMoneda(TipoCuenta tipoCuenta, TipoMoneda tipoMoneda, Long dniTitular) throws TipoCuentaExistenteException {
        CuentaDao cuentaDao = new CuentaDao();
        Set<Cuenta> cuentasClientes = cuentaDao.findAllCuentasDelCliente(dniTitular);

        for (Cuenta cuenta: cuentasClientes) {
            if (tipoCuenta.equals(cuenta.getTipoCuenta()) && tipoMoneda.equals(cuenta.getTipoMoneda())) {
                throw new TipoCuentaExistenteException("Ya tiene una cuenta con ese tipo de cuenta y tipo de moneda");
            }
        }
    }

    public void validarCuentaExistente(Long cbu) throws CuentaNoEncontradaException {
        CuentaDao cuentaDao = new CuentaDao();
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null){
            throw new CuentaNoEncontradaException("No se encontro ninguna cuenta con el CBU: " + cbu);
        }
    }

    // VALIDACIONES DE OPERACIONES //

    public void validarSaldo(Cuenta cuenta, double monto) throws CuentaSinDineroException {
        if (cuenta.getSaldo() < monto){
            throw new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $" + cuenta.getSaldo());
        }
    }

}
