package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.Operacion;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioConsultarSaldo {

    @InjectMocks
    private ServicioOperaciones servicioOperaciones;

    @Mock
    private ValidacionesServicios validacionesServicios;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private MovimientosDao movimientosDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consultarSaldoExitosamente() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);
        cuentaMock.setSaldo(1000);

        // Mockeo la validación de la cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuentaMock);

        // Mockeo el guardado del movimiento
        doNothing().when(movimientosDao).saveMovimiento(anyString(), anyDouble(), anyLong());

        // Ejecuto el método a testear
        Operacion resultado = servicioOperaciones.consultarSaldo(cbu);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cbu, resultado.getCbu());
        assertEquals(1000, resultado.getSaldoActual());
        assertEquals("Consulta", resultado.getTipoOperacion());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, times(1)).saveMovimiento(eq("Consulta"), eq(0.0), eq(cbu));
    }

    @Test
    void consultarSaldoCuentaNoEncontrada() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        // Mockeo la validación de la cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta (no existe)
        when(cuentaDao.findCuenta(cbu)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioOperaciones.consultarSaldo(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
    }
}
