package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ServicioCuentas {
    private final ValidacionesServicios validar;
    private final CuentaDao cuentaDao;
    private final ClienteDao clienteDao;
    private final MovimientosDao movimientosDao;

    // Constructor con inyección de dependencias
    public ServicioCuentas(ValidacionesServicios validar,
                           CuentaDao cuentaDao,
                           ClienteDao clienteDao,
                           MovimientosDao movimientosDao) {
        this.validar = validar;
        this.cuentaDao = cuentaDao;
        this.clienteDao = clienteDao;
        this.movimientosDao = movimientosDao;
    }

    public void inicializarCuentas() {
        cuentaDao.inicializarCuentas();
    }

    public Set<Cuenta> mostrarCuentas(Long dni) throws ClienteNoEncontradoException, CuentaNoEncontradaException {
        //Funcion que devuelve el cliente encontrado o vuelve Null si no lo encontro
        Cliente cliente = clienteDao.findCliente(dni);

        if (cliente == null) {
            //Lanzo excepcion si el cliente no fue encontrado
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }

        //Funcion que me devuelve todas las cuentas que tiene el cliente
        Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(dni);

        if (cuentas.isEmpty()){
            throw new CuentaNoEncontradaException("No hay cuentas asociadas al cliente con DNI: " + dni);
        }

        //Retorna la lista de cuentas que tiene asociada el cliente
        return cuentas;
    }

    public Cuenta crearCuenta(CuentaDto cuentaDto) throws ClienteNoEncontradoException, CuentaExistenteException, TipoCuentaExistenteException {
        Cuenta cuenta = new Cuenta(cuentaDto);

        //Valido que exista el cliente, si no lanza excepcion
        Cliente cliente = clienteDao.findCliente(cuenta.getDniTitular());

        if (cliente == null){
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + cuenta.getDniTitular());
        }

        //Valido que si la cuenta mandada ya existia previamente, si no lanza excepcion
        Cuenta cuentaExiste = cuentaDao.findCuenta(cuenta.getCbu());

        if (cuentaExiste != null) {
            throw new CuentaExistenteException("Ya tiene una cuenta con ese CBU");
        }

        //Valido que no exista una cuenta con el mismo tipo de cuenta y tipo de moneda
        validar.cuentaMismoTipoMoneda(cuenta.getTipoCuenta(), cuenta.getTipoMoneda(), cuenta.getDniTitular());

        //Agrego la cuenta al archivo
        cuentaDao.saveCuenta(cuenta);

        //Muestro en pantalla el resultado
        return cuenta;
    }

    public Cuenta actualizarAlias(Long dni, Long cbu, String alias) throws CuentaNoEncontradaException, CuentasVaciasException, ClienteNoEncontradoException {

        //Valido que exista el cliente, si no lanza excepcion
        Cliente cliente = clienteDao.findCliente(dni);

        if (cliente == null) {
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }

        //Valido si el DNI tiene cuentas asociadas
        List<Long> cuentasCbu = cuentaDao.getRelacionesDni(dni);
        if (cuentasCbu.isEmpty()) {
            throw new CuentasVaciasException("No hay cuentas asociadas al cliente con DNI: " + dni);
        }

        //Funcion que devuelve la cuenta encontrada o vuelve null si no lo encontro. Solo devuelve las cuentas que tiene asocida el cliente
        Cuenta cuenta = cuentaDao.findCuentaDelCliente(cbu, dni);

        if (cuenta == null) {
            throw new CuentaNoEncontradaException("El cliente no tiene ninguna cuenta con el CBU: " + cbu);
        }

        cuentaDao.deleteCuenta(cbu); //Borro la cuenta ya que va ser actualizada

        cuenta.setAlias(alias);

        cuentaDao.saveCuenta(cuenta); //Guardo la cuenta actualizada

        return cuenta;
    }

    public Cuenta eliminarCuenta(Long dni, Long cbu) throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException, CuentaTieneSaldoException {

        //Valido que exista el cliente, si no lanza excepcion
        validar.validarClienteExistente(dni);

        //Valido si el DNI tiene cuentas asociadas
        validar.validarCuentasCliente(dni);

        //Funcion que devuelve la cuenta encontrada o vuelve null si no lo encontro. Solo devuelve las cuentas que tiene asocida el cliente
        Cuenta cuenta = cuentaDao.findCuentaDelCliente(cbu, dni);

        if (cuenta == null) {
            throw new CuentaNoEncontradaException("El cliente no tiene ninguna cuenta con el CBU: " + cbu);
        }

        //Valido que la cuenta no tenga saldo, si lo tiene lanza excepcion
        validar.validarSaldoCuenta(cbu);

        //Borro la cuenta y los movimientos de la misma
        cuentaDao.deleteCuenta(cbu);
        movimientosDao.deleteMovimiento(cbu);

        return cuenta;
    }
}
