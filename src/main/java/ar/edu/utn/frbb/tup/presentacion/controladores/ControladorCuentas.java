package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaExistenteException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.excepciones.TipoCuentaExistenteException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/cuentas")
public class ControladorCuentas {
    private ValidacionesServicios validacionesServicios;
    private ServicioCuentas servicioCuentas;

    public ControladorCuentas (ValidacionesServicios validacionesServicios, ServicioCuentas servicioCuentas) {
        this.validacionesServicios = validacionesServicios;
        this.servicioCuentas = servicioCuentas;
        servicioCuentas.inicializarCuentas();
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Set<Cuenta>> mostrarCuentas(@PathVariable Long dni) throws CuentaNoEncontradaException, ClienteNoEncontradoException, CuentasVaciasException {
        return new ResponseEntity<>(servicioCuentas.mostrarCuentas(dni), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody CuentaDto cuentaDto) throws TipoCuentaExistenteException, CuentaExistenteException, ClienteNoEncontradoException {
        validacionesServicios.validarCuenta(cuentaDto);
        Cuenta cuenta = servicioCuentas.crearCuenta(cuentaDto);
        return new ResponseEntity<>(cuenta, HttpStatus.CREATED);
    }

    @PutMapping("/{dni}/{cbu}")
    public ResponseEntity<Cuenta> actualizarAlias(@PathVariable Long dni, @PathVariable Long cbu, @RequestParam String alias) throws CuentaNoEncontradaException, ClienteNoEncontradoException, CuentaExistenteException, TipoCuentaExistenteException, CuentasVaciasException {
        return new ResponseEntity<>(servicioCuentas.actualizarAlias(dni, cbu, alias), HttpStatus.OK);
    }

    @DeleteMapping("/{dni}/{cbu}")
    public ResponseEntity<Cuenta> eliminarCuenta(@PathVariable Long dni, @PathVariable Long cbu) throws CuentasVaciasException, CuentaNoEncontradaException, ClienteNoEncontradoException {
        return new ResponseEntity<>(servicioCuentas.eliminarCuenta(dni, cbu), HttpStatus.OK);
    }
}

