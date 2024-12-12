package ar.edu.utn.frbb.tup.presentacion.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Movimiento;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorOperaciones;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestControladorMostrarMovimientos {

    @InjectMocks
    private ControladorOperaciones controladorOperaciones;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioOperaciones servicioOperaciones;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMostrarMovimientosExitosamente() throws CuentaNoEncontradaException, MovimientosVaciosException {
        // Preparo datos de entrada
        Long cbu = 87654321L;

        Movimiento movimiento1 = new Movimiento();
        movimiento1.setCbu(cbu);
        movimiento1.setFechaOperacion(LocalDate.now());
        movimiento1.setHoraOperacion(LocalTime.now().withNano(0));
        movimiento1.setTipoOperacion("Deposito");
        movimiento1.setMonto(5000.0);

        Movimiento movimiento2 = new Movimiento();
        movimiento2.setCbu(cbu);
        movimiento2.setFechaOperacion(LocalDate.now().minusDays(1));
        movimiento2.setHoraOperacion(LocalTime.now().withNano(0));
        movimiento2.setTipoOperacion("Extraccion");
        movimiento2.setMonto(2000.0);

        List<Movimiento> movimientosMock = new ArrayList<>();
        movimientosMock.add(movimiento1);
        movimientosMock.add(movimiento2);

        // Mockeo la validación de CBU
        doNothing().when(validacionesPresentacion).validarCBU(cbu);

        // Mockeo el servicio de mostrar movimientos
        when(servicioOperaciones.mostrarMovimientos(cbu)).thenReturn(movimientosMock);

        // Ejecuto el método a testear
        ResponseEntity<List<Movimiento>> response = controladorOperaciones.getMostrarMovimientos(cbu);

        // Verifico el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movimientosMock, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioOperaciones, times(1)).mostrarMovimientos(cbu);
    }

    @Test
    void getMostrarMovimientosCuentaNoEncontrada() throws CuentaNoEncontradaException, MovimientosVaciosException {
        // Preparo datos de entrada
        Long cbu = 87654321L;

        // Mockeo la validación de CBU
        doNothing().when(validacionesPresentacion).validarCBU(cbu);

        // Mockeo que el servicio lance una excepción
        when(servicioOperaciones.mostrarMovimientos(cbu)).thenThrow(new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe."));

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> controladorOperaciones.getMostrarMovimientos(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioOperaciones, times(1)).mostrarMovimientos(cbu);
    }

    @Test
    void getMostrarMovimientosVacios() throws CuentaNoEncontradaException, MovimientosVaciosException {
        // Preparo datos de entrada
        Long cbu = 87654321L;

        List<Movimiento> movimientosVacios = new ArrayList<>();

        // Mockeo la validación de CBU
        doNothing().when(validacionesPresentacion).validarCBU(cbu);

        // Mockeo que el servicio lance una excepción
        when(servicioOperaciones.mostrarMovimientos(cbu)).thenThrow(new MovimientosVaciosException("La cuenta no tiene movimientos."));

        // Llamo al método y espero la excepción
        MovimientosVaciosException exception = assertThrows(
                MovimientosVaciosException.class,
                () -> controladorOperaciones.getMostrarMovimientos(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta no tiene movimientos.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioOperaciones, times(1)).mostrarMovimientos(cbu);
    }
}
