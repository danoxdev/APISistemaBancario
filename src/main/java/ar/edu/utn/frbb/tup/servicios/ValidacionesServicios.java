package ar.edu.utn.frbb.tup.servicios;
import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelo.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.PrestamoDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class ValidacionesServicios {
    private final ClienteDao clienteDao;
    private final CuentaDao cuentaDao;
    private final PrestamoDao prestamoDao;

    public ValidacionesServicios(ClienteDao clienteDao, CuentaDao cuentaDao, PrestamoDao prestamoDao) {
        this.clienteDao = clienteDao;
        this.cuentaDao = cuentaDao;
        this.prestamoDao = prestamoDao;
    }

    // VALIDACIONES DE CLIENTES //

    //Validar que el cliente sea mayor de edad
    public void esMayordeEdad(LocalDate fecha) throws ClienteMenorDeEdadException {
        int edad = LocalDate.now().getYear() - fecha.getYear();
        if (edad < 18) {
            throw new ClienteMenorDeEdadException("El cliente no es mayor de edad");
        }
    }

    //Validar DNI
    public void validarDni(Long dni) {
        try {
            if (dni == 0 || dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error: El dni debe tener entre 7 y 8 digitos");
            }
        } catch(NumberFormatException e){
            throw new IllegalArgumentException("Error: El dni debe ser un numero");
        }
    }

    //Validar que el cliente no exista
    public void validarClienteYaExiste(ClienteDto clienteDto) throws ClienteExistenteException {
        if (clienteDao.findCliente(clienteDto.getDni()) != null){
            throw new ClienteExistenteException("Ya existe un cliente con el DNI ingresado");
        }
    }

    //Validar que el cliente no tenga cuentas antes de elimnarlo
    public void validarClienteSinCuentas(Long dni) throws ClienteTieneCuentasException {
        Cliente cliente = clienteDao.findCliente(dni);
        if (cliente != null && !cliente.getCuentas().isEmpty()) {
            throw new ClienteTieneCuentasException("No se puede eliminar el cliente porque tiene cuentas");
        }
    }

    //Validar que el cliente no tenga prestamos antes de elimnarlo
    public void validarClienteSinPrestamos(Long dni) throws ClienteTienePrestamosException {
        Cliente cliente = clienteDao.findCliente(dni);
        if (cliente != null && !cliente.getPrestamos().isEmpty()) {
            throw new ClienteTienePrestamosException("No se puede eliminar el cliente porque tiene prestamos");
        }
    }

    // VALIDACIONES DE CUENTAS //

    //Validar que el cliente exista
    public void validarClienteExistente(Long dni) throws ClienteNoEncontradoException {
        if (clienteDao.findCliente(dni) == null){
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }
    }

    //Validar que el cliente tenga cuentas asociadas
    public void validarCuentasCliente(Long dni) throws CuentasVaciasException {
        List<Long> cuentasCbu = cuentaDao.getRelacionesDni(dni);
        if (cuentasCbu.isEmpty()) {
            throw new CuentasVaciasException("No hay cuentas asociadas al cliente con DNI: " + dni);
        }
    }

    public void cuentaMismoTipoMoneda(TipoCuenta tipoCuenta, TipoMoneda tipoMoneda, Long dniTitular) throws TipoCuentaExistenteException {
        Set<Cuenta> cuentasClientes = cuentaDao.findAllCuentasDelCliente(dniTitular);

        for (Cuenta cuenta: cuentasClientes) {
            if (tipoCuenta.equals(cuenta.getTipoCuenta()) && tipoMoneda.equals(cuenta.getTipoMoneda())) {
                throw new TipoCuentaExistenteException("Ya tiene una cuenta con ese tipo de cuenta y tipo de moneda");
            }
        }
    }

    public void validarCuentaExistente(Long cbu) throws CuentaNoEncontradaException {
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null){
            throw new CuentaNoEncontradaException("No se encontro ninguna cuenta con el CBU: " + cbu);
        }
    }

    //Validar que la cuenta no tenga saldo antes de eliminarla
    public void validarSaldoCuenta(Long cbu) throws CuentaTieneSaldoException {
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta.getSaldo() > 0) {
            throw new CuentaTieneSaldoException("No se puede eliminar la cuenta porque tiene saldo");
        }
    }

    // VALIDACIONES DE OPERACIONES //

    public void validarSaldo(Cuenta cuenta, double monto) throws CuentaSinDineroException {
        if (cuenta.getSaldo() < monto){
            throw new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $" + cuenta.getSaldo());
        }
    }

}