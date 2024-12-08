package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.Movimiento;
import ar.edu.utn.frbb.tup.modelo.Operacion;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServicioOperaciones {
    ValidacionesServicios validar = new ValidacionesServicios();
    CuentaDao cuentaDao = new CuentaDao();
    MovimientosDao movimientosDao = new MovimientosDao();

    public void inicializarMovimientos() {
        movimientosDao.inicializarMovimientos();
    }

    public Operacion consultarSaldo (Long cbu) throws CuentaNoEncontradaException {
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        //Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        //Tomo registro de la operacion que se hizo
        movimientosDao.saveMovimiento("Consulta", 0, cuenta.getCbu());

        //Devuelvo un Objeto operacion de la consulta que se hizo
        return new Operacion()
            .setCbu(cbu)
            .setSaldoActual(cuenta.getSaldo())
            .setTipoOperacion("Consulta");
        }

    public Operacion deposito(Long cbu , double monto) throws CuentaNoEncontradaException {

        Cuenta cuenta = cuentaDao.findCuenta(cbu);

        //Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        //Borro la cuenta ya que va ser modificada
        cuentaDao.deleteCuenta(cuenta.getCbu());

        //Sumo el monto al saldo que tenia la cuenta
        cuenta.setSaldo(cuenta.getSaldo() + monto);

        //Tomo registro de la operacion que se hizo
        movimientosDao.saveMovimiento("Deposito", monto, cuenta.getCbu());

        //Guardo la cuenta modificada
        cuentaDao.saveCuenta(cuenta);

        return new Operacion()
                .setCbu(cbu)
                .setSaldoActual(cuenta.getSaldo())
                .setMonto(monto)
                .setTipoOperacion("Deposito");
    }

    public List<Movimiento> mostrarMovimientos(Long cbu) throws MovimientosVaciosException, CuentaNoEncontradaException {
        Cuenta cuenta = cuentaDao.findCuenta(cbu);

        //Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        List<Movimiento> movimientos = movimientosDao.findMovimientos(cuenta.getCbu());

        if (movimientos.isEmpty()){
            throw new MovimientosVaciosException("La cuenta no tiene movimientos");
        }

        return movimientos;
    }

    public Operacion extraccion (Long cbu, double monto) throws CuentaNoEncontradaException, CuentaSinDineroException {
        Cuenta cuenta = cuentaDao.findCuenta(cbu);

        //Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        //Valido el saldo
        validar.validarSaldo(cuenta, monto);

        //Borro la cuenta ya que va ser modificada
        cuentaDao.deleteCuenta(cuenta.getCbu());

        //Resto el monto al saldo que tenia la cuenta
        cuenta.setSaldo(cuenta.getSaldo() - monto);

        //Tomo registro de la operacion que se hizo
        movimientosDao.saveMovimiento("Extraccion", monto, cuenta.getCbu());

        cuentaDao.saveCuenta(cuenta); //Guardo la cuenta modificada

        return new Operacion()
                .setCbu(cbu)
                .setSaldoActual(cuenta.getSaldo())
                .setMonto(monto)
                .setTipoOperacion("Extraccion");
    }
}


