package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.CuentaDistintaMonedaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaEstaDeBajaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.excepciones.TransferenciaFailException;
import ar.edu.utn.frbb.tup.modelo.Movimiento;
import ar.edu.utn.frbb.tup.modelo.Operacion;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operaciones")

public class ControladorOperaciones {
    private final ServicioOperaciones servicioOperaciones;

    public ControladorOperaciones(ServicioOperaciones servicioOperaciones) {
        this.servicioOperaciones = servicioOperaciones;
        servicioOperaciones.inicializarMovimientos();
    }

    //Consulta de saldo
    @GetMapping("/consultarsaldo/{cbu}")
    public ResponseEntity<Operacion> getConsulta(@PathVariable Long cbu) throws CuentaNoEncontradaException, CuentaEstaDeBajaException {
        return new ResponseEntity<>(servicioOperaciones.consultarSaldo(cbu), HttpStatus.OK);
    }

}
