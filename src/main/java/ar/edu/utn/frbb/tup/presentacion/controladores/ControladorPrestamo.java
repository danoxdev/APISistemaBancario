package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteSinPrestamosException;
import ar.edu.utn.frbb.tup.excepciones.CuentaMonedaNoExisteException;
import ar.edu.utn.frbb.tup.excepciones.PrestamosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.presentacion.DTOs.PrestamoDto;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/prestamos")
public class ControladorPrestamo {
    private final ValidacionesPresentacion validacionesPresentacion;
    private final ServicioPrestamo servicioPrestamo;

    public ControladorPrestamo(ValidacionesPresentacion validacionesPresentacion, ServicioPrestamo servicioPrestamo) {
        this.validacionesPresentacion = validacionesPresentacion;
        this.servicioPrestamo = servicioPrestamo;
        servicioPrestamo.inicializarPrestamos();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> solicitarPrestamo(@RequestBody PrestamoDto prestamoDto) throws ClienteNoEncontradoException, CuentaMonedaNoExisteException, PrestamosVaciosException {
        validacionesPresentacion.validarSolicitudPrestamo(prestamoDto);

        Map<String, Object> resultado = servicioPrestamo.solicitarPrestamo(
                prestamoDto.getDniCliente(),
                prestamoDto.getPlazoMeses(),
                prestamoDto.getMonto(),
                prestamoDto.getTipoMoneda()
        );

        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Set<Prestamo>> listarPrestamos (@PathVariable Long dni) throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        validacionesPresentacion.validarDni(dni);
        return new ResponseEntity<>(servicioPrestamo.listarPrestamos(dni), HttpStatus.OK);
    }
}
