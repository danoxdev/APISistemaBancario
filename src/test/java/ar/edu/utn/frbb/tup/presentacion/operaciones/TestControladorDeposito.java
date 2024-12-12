package ar.edu.utn.frbb.tup.presentacion.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
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

class TestControladorDeposito {

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
    void getDepositoExitosamente() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 87654321L;
        double monto = 500.0;
        Operacion operacionMock = new Operacion();
        operacionMock.setCbu(cbu);
        operacionMock.setMonto(monto);
        operacionMock.setSaldoActual(1500.0);
        operacionMock.setTipoOperacion("Deposito");

        // Mockeo la validación de CBU y monto
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doNothing().when(validacionesPresentacion).validarMonto(monto);

        // Mockeo el servicio de deposito
        when(servicioOperaciones.deposito(cbu, monto)).thenReturn(operacionMock);

        // Ejecuto el método a testear
        ResponseEntity<Operacion> response = controladorOperaciones.getDeposito(cbu, monto);

        // Verifico el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(operacionMock, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(validacionesPresentacion, times(1)).validarMonto(monto);
        verify(servicioOperaciones, times(1)).deposito(cbu, monto);
    }

    @Test
    void getDepositoCuentaNoEncontrada() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 87654321L;
        double monto = 500.0;

        // Mockeo la validación de CBU y monto
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doNothing().when(validacionesPresentacion).validarMonto(monto);

        // Mockeo que el servicio lance una excepción
        when(servicioOperaciones.deposito(cbu, monto)).thenThrow(new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe."));

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> controladorOperaciones.getDeposito(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(validacionesPresentacion, times(1)).validarMonto(monto);
        verify(servicioOperaciones, times(1)).deposito(cbu, monto);
    }
}
