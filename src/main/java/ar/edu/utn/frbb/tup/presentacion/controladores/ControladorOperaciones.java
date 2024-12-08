package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.modelo.Operacion;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Operacion> getConsultarSaldo(@PathVariable Long cbu) throws CuentaNoEncontradaException {
        return new ResponseEntity<>(servicioOperaciones.consultarSaldo(cbu), HttpStatus.OK);
    }

    //Deposito
    @PutMapping("/deposito/{cbu}")
    public ResponseEntity<Operacion> getDeposito(@PathVariable Long cbu, @RequestParam double monto) throws CuentaNoEncontradaException {
        return new ResponseEntity<>(servicioOperaciones.deposito(cbu, monto), HttpStatus.OK);
    }

}
