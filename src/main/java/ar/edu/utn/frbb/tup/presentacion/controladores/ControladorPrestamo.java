package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteSinPrestamosException;
import ar.edu.utn.frbb.tup.excepciones.CuentaMonedaNoExisteException;
import ar.edu.utn.frbb.tup.excepciones.PrestamosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.presentacion.DTOs.PrestamoDto;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/prestamos")
public class ControladorPrestamo {
    private final ServicioPrestamo servicioPrestamo;
    private final ValidacionesServicios validacionesServicios;

    public ControladorPrestamo(ServicioPrestamo servicioPrestamo, ValidacionesServicios validacionesServicios) {
        this.servicioPrestamo = servicioPrestamo;
        this.validacionesServicios = validacionesServicios;
        servicioPrestamo.inicializarPrestamos();
    }

    /**
     * Endpoint para solicitar un préstamo.
     * Entrada: JSON con los datos del préstamo.
     * Salida: JSON con el estado del préstamo, mensaje y plan de pagos.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> solicitarPrestamo(@RequestBody PrestamoDto prestamoDto)
            throws ClienteNoEncontradoException, CuentaMonedaNoExisteException, PrestamosVaciosException {
        // Validar la prestamoDto
        validacionesServicios.validarSolicitudPrestamo(prestamoDto);

        // Procesar la prestamoDto
        Map<String, Object> resultado = servicioPrestamo.solicitarPrestamo(
                prestamoDto.getDniCliente(),
                prestamoDto.getPlazoMeses(),
                prestamoDto.getMonto(),
                prestamoDto.getTipoMoneda()
        );

        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    /**
     * Endpoint para listar los préstamos de un cliente por su DNI.
     * Entrada: DNI como parámetro de ruta.
     * Salida: JSON con la lista de préstamos.
     */
    @GetMapping("/{dni}")
    public ResponseEntity<Set<Prestamo>> listarPrestamos(@PathVariable Long dni)
            throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        // Obtener los préstamos del cliente
        Set<Prestamo> prestamos = servicioPrestamo.listarPrestamos(dni);

        return new ResponseEntity<>(prestamos, HttpStatus.OK);
    }
}
