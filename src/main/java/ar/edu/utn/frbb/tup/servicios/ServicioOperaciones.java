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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioOperaciones {
    private final ValidacionesServicios validar;
    private final CuentaDao cuentaDao;
    private final MovimientosDao movimientosDao;

    public ServicioOperaciones(CuentaDao cuentaDao, MovimientosDao movimientosDao, ValidacionesServicios validar) {
        this.cuentaDao = cuentaDao;
        this.movimientosDao = movimientosDao;
        this.validar = validar;
    }

    public void inicializarMovimientos() {
        movimientosDao.inicializarMovimientos();
    }

    public Operacion consultarSaldo(Long cbu) throws CuentaNoEncontradaException {
        // Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        // Obtengo la cuenta
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null) {
            throw new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe.");
        }

        // Tomo registro de la operación que se hizo
        movimientosDao.saveMovimiento("Consulta", 0, cuenta.getCbu());

        // Devuelvo un Objeto operación de la consulta que se hizo
        return new Operacion()
                .setCbu(cbu)
                .setSaldoActual(cuenta.getSaldo())
                .setTipoOperacion("Consulta");
    }

    public Operacion deposito(Long cbu, double monto) throws CuentaNoEncontradaException {
        // Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        // Obtengo la cuenta
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null) {
            throw new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe.");
        }

        // Borro la cuenta ya que va a ser modificada
        cuentaDao.deleteCuenta(cuenta.getCbu());

        // Sumo el monto al saldo que tenía la cuenta
        cuenta.setSaldo(cuenta.getSaldo() + monto);

        // Tomo registro de la operación que se hizo
        movimientosDao.saveMovimiento("Deposito", monto, cuenta.getCbu());

        // Guardo la cuenta modificada
        cuentaDao.saveCuenta(cuenta);

        return new Operacion()
                .setCbu(cbu)
                .setSaldoActual(cuenta.getSaldo())
                .setMonto(monto)
                .setTipoOperacion("Deposito");
    }

    public List<Movimiento> mostrarMovimientos(Long cbu) throws MovimientosVaciosException, CuentaNoEncontradaException {
        // Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        // Obtengo la cuenta
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null) {
            throw new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe.");
        }

        // Obtengo los movimientos
        List<Movimiento> movimientos = movimientosDao.findMovimientos(cuenta.getCbu());

        if (movimientos.isEmpty()) {
            throw new MovimientosVaciosException("La cuenta no tiene movimientos.");
        }

        return movimientos;
    }

    public Operacion extraccion(Long cbu, double monto) throws CuentaNoEncontradaException, CuentaSinDineroException {
        // Valido que la cuenta existe
        validar.validarCuentaExistente(cbu);

        // Obtengo la cuenta
        Cuenta cuenta = cuentaDao.findCuenta(cbu);
        if (cuenta == null) {
            throw new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe.");
        }

        // Valido el saldo
        validar.validarSaldo(cuenta, monto);

        // Borro la cuenta ya que va a ser modificada
        cuentaDao.deleteCuenta(cuenta.getCbu());

        // Resto el monto al saldo que tenía la cuenta
        cuenta.setSaldo(cuenta.getSaldo() - monto);

        // Tomo registro de la operación que se hizo
        movimientosDao.saveMovimiento("Extraccion", monto, cuenta.getCbu());

        // Guardo la cuenta modificada
        cuentaDao.saveCuenta(cuenta);

        return new Operacion()
                .setCbu(cbu)
                .setSaldoActual(cuenta.getSaldo())
                .setMonto(monto)
                .setTipoOperacion("Extraccion");
    }
}
