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
        //Valido que la cuenta existe y que esta de alta
    Cuenta cuenta = cuentaDao.findCuenta(cbu);

    if (cuenta == null){
        throw new CuentaNoEncontradaException("No se encontro ninguna cuenta con el CBU: " + cbu);
    }

    //Tomo registro de la operacion que se hizo
    movimientosDao.saveMovimiento("Consulta", 0, cuenta.getCbu());

    //Devuelvo un Objeto operacion de la consulta que se hizo
    return new Operacion()
        .setCbu(cbu)
        .setSaldoActual(cuenta.getSaldo())
        .setTipoOperacion("Consulta");
    }
}


