package ar.edu.utn.frbb.tup.presentacion.prestamos;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaMonedaNoExisteException;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorPrestamo;
import ar.edu.utn.frbb.tup.presentacion.DTOs.PrestamoDto;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestControladorSolicitarPrestamo {

    @InjectMocks
    private ControladorPrestamo controladorPrestamo;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioPrestamo servicioPrestamo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void solicitarPrestamoExitosamente() throws CuentaMonedaNoExisteException, ClienteNoEncontradoException {
        // Preparo datos de entrada
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setDniCliente(12345678L);
        prestamoDto.setMonto(10000.0);
        prestamoDto.setPlazoMeses(12);
        prestamoDto.setTipoMoneda("PESOS");

        // Mockeo la validación de solicitud de préstamo
        doNothing().when(validacionesPresentacion).validarSolicitudPrestamo(prestamoDto);

        // Mockeo el servicio de solicitud de préstamo
        Prestamo prestamo = new Prestamo(1, 12345678L, 10000.0, 12, 0, 10000.0);
        Map<String, Object> resultadoMock = new HashMap<>();
        resultadoMock.put("estado", "Aprobado");
        resultadoMock.put("mensaje", "Préstamo aprobado y acreditado");
        resultadoMock.put("prestamo", prestamo);
        when(servicioPrestamo.solicitarPrestamo(prestamoDto.getDniCliente(), prestamoDto.getPlazoMeses(), prestamoDto.getMonto(), prestamoDto.getTipoMoneda()))
                .thenReturn(resultadoMock);

        // Ejecuto el método a testear
        ResponseEntity<Map<String, Object>> response = controladorPrestamo.solicitarPrestamo(prestamoDto);

        // Verifico el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Aprobado", response.getBody().get("estado"));
        assertEquals("Préstamo aprobado y acreditado", response.getBody().get("mensaje"));

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarSolicitudPrestamo(prestamoDto);
        verify(servicioPrestamo, times(1)).solicitarPrestamo(prestamoDto.getDniCliente(), prestamoDto.getPlazoMeses(), prestamoDto.getMonto(), prestamoDto.getTipoMoneda());
    }

    @Test
    void solicitarPrestamoCuentaMonedaNoExiste() throws CuentaMonedaNoExisteException, ClienteNoEncontradoException {
        // Preparo datos de entrada
        PrestamoDto prestamoDto = new PrestamoDto();
        prestamoDto.setDniCliente(12345678L);
        prestamoDto.setMonto(10000.0);
        prestamoDto.setPlazoMeses(12);
        prestamoDto.setTipoMoneda("DOLARES");

        // Mockeo la validación de solicitud de préstamo
        doNothing().when(validacionesPresentacion).validarSolicitudPrestamo(prestamoDto);

        // Mockeo que el servicio lance una excepción
        when(servicioPrestamo.solicitarPrestamo(prestamoDto.getDniCliente(), prestamoDto.getPlazoMeses(), prestamoDto.getMonto(), prestamoDto.getTipoMoneda()))
                .thenThrow(new CuentaMonedaNoExisteException("No existe una cuenta bancaria con la moneda ingresada"));

        // Llamo al método y espero la excepción
        CuentaMonedaNoExisteException exception = assertThrows(
                CuentaMonedaNoExisteException.class,
                () -> controladorPrestamo.solicitarPrestamo(prestamoDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No existe una cuenta bancaria con la moneda ingresada", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarSolicitudPrestamo(prestamoDto);
        verify(servicioPrestamo, times(1)).solicitarPrestamo(prestamoDto.getDniCliente(), prestamoDto.getPlazoMeses(), prestamoDto.getMonto(), prestamoDto.getTipoMoneda());
    }
}
