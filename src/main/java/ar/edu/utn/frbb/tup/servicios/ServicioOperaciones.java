package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.Operacion;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import org.springframework.stereotype.Component;

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
}


