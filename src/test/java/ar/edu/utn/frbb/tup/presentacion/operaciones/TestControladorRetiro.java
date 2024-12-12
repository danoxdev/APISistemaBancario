package ar.edu.utn.frbb.tup.presentacion.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.modelo.Operacion;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestControladorRetiro {

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
    void getRetiroExitosamente() throws CuentaNoEncontradaException, CuentaSinDineroException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500.0;
        Operacion operacionMock = new Operacion();
        operacionMock.setCbu(cbu);
        operacionMock.setMonto(monto);
        operacionMock.setSaldoActual(1500.0);
        operacionMock.setTipoOperacion("Extraccion");

        // Mockeo la validación de CBU y monto
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doNothing().when(validacionesPresentacion).validarMonto(monto);

        // Mockeo el servicio de extraccion
        when(servicioOperaciones.extraccion(cbu, monto)).thenReturn(operacionMock);

        // Ejecuto el método a testear
        ResponseEntity<Operacion> response = controladorOperaciones.getRetiro(cbu, monto);

        // Verifico el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(operacionMock, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(validacionesPresentacion, times(1)).validarMonto(monto);
        verify(servicioOperaciones, times(1)).extraccion(cbu, monto);
    }

    @Test
    void getRetiroCuentaNoEncontrada() throws CuentaNoEncontradaException, CuentaSinDineroException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500.0;

        // Mockeo la validación de CBU y monto
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doNothing().when(validacionesPresentacion).validarMonto(monto);

        // Mockeo que el servicio lance una excepción
        when(servicioOperaciones.extraccion(cbu, monto)).thenThrow(new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe."));

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> controladorOperaciones.getRetiro(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(validacionesPresentacion, times(1)).validarMonto(monto);
        verify(servicioOperaciones, times(1)).extraccion(cbu, monto);
    }

    @Test
    void getRetiroSinDinero() throws CuentaNoEncontradaException, CuentaSinDineroException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500.0;

        // Mockeo la validación de CBU y monto
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doNothing().when(validacionesPresentacion).validarMonto(monto);

        // Mockeo que el servicio lance una excepción
        when(servicioOperaciones.extraccion(cbu, monto)).thenThrow(new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $100.0"));

        // Llamo al método y espero la excepción
        CuentaSinDineroException exception = assertThrows(
                CuentaSinDineroException.class,
                () -> controladorOperaciones.getRetiro(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No posee saldo suficiente para realizar la operacion, su saldo es de $100.0", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(validacionesPresentacion, times(1)).validarMonto(monto);
        verify(servicioOperaciones, times(1)).extraccion(cbu, monto);
    }
}
