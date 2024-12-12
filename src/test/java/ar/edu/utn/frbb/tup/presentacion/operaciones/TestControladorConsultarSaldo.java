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

class TestControladorConsultarSaldo {

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
    void getConsultarSaldoExitosamente() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 87654321L;
        Operacion operacionMock = new Operacion();
        operacionMock.setCbu(cbu);
        operacionMock.setSaldoActual(1000.0);
        operacionMock.setTipoOperacion("Consulta");

        // Mockeo la validación de CBU
        doNothing().when(validacionesPresentacion).validarCBU(cbu);

        // Mockeo el servicio de consultar saldo
        when(servicioOperaciones.consultarSaldo(cbu)).thenReturn(operacionMock);

        // Ejecuto el método a testear
        ResponseEntity<Operacion> response = controladorOperaciones.getConsultarSaldo(cbu);

        // Verifico el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(operacionMock, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioOperaciones, times(1)).consultarSaldo(cbu);
    }

    @Test
    void getConsultarSaldoCuentaNoEncontrada() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 87654321L;

        // Mockeo la validación de CBU
        doNothing().when(validacionesPresentacion).validarCBU(cbu);

        // Mockeo que el servicio lance una excepción
        when(servicioOperaciones.consultarSaldo(cbu)).thenThrow(new CuentaNoEncontradaException("La cuenta con el CBU especificado no existe."));

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> controladorOperaciones.getConsultarSaldo(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioOperaciones, times(1)).consultarSaldo(cbu);
    }
}
