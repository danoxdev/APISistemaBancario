package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Movimiento;
import ar.edu.utn.frbb.tup.modelo.Operacion;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/operaciones")

public class ControladorOperaciones {
    private ValidacionesPresentacion validacionesPresentacion;
    private final ServicioOperaciones servicioOperaciones;

    public ControladorOperaciones(ValidacionesPresentacion validacionesPresentacion, ServicioOperaciones servicioOperaciones) {
        this.validacionesPresentacion = validacionesPresentacion;
        this.servicioOperaciones = servicioOperaciones;
        servicioOperaciones.inicializarMovimientos();
    }

    //Consulta de saldo
    @GetMapping("/consultarsaldo/{cbu}")
    public ResponseEntity<Operacion> getConsultarSaldo(@PathVariable Long cbu) throws CuentaNoEncontradaException {
        validacionesPresentacion.validarCBU(cbu);
        return new ResponseEntity<>(servicioOperaciones.consultarSaldo(cbu), HttpStatus.OK);
    }

    //Deposito
    @PutMapping("/deposito/{cbu}")
    public ResponseEntity<Operacion> getDeposito(@PathVariable Long cbu, @RequestParam double monto) throws CuentaNoEncontradaException {
        validacionesPresentacion.validarCBU(cbu);
        validacionesPresentacion.validarMonto(monto);
        return new ResponseEntity<>(servicioOperaciones.deposito(cbu, monto), HttpStatus.OK);
    }

    //Mostrar movimientos
    @GetMapping("/movimientos/{cbu}")
    public ResponseEntity<List<Movimiento>> getMostrarMovimientos(@PathVariable Long cbu) throws CuentaNoEncontradaException, MovimientosVaciosException {
        validacionesPresentacion.validarCBU(cbu);
        return new ResponseEntity<>(servicioOperaciones.mostrarMovimientos(cbu), HttpStatus.OK);
    }

    //Retiro
    @PutMapping("/extraccion/{cbu}")
    public ResponseEntity<Operacion> getRetiro(@PathVariable Long cbu, @RequestParam double monto) throws CuentaNoEncontradaException, CuentaSinDineroException {
        validacionesPresentacion.validarCBU(cbu);
        validacionesPresentacion.validarMonto(monto);
        return new ResponseEntity<>(servicioOperaciones.extraccion(cbu, monto), HttpStatus.OK);
    }

}
