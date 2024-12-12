package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.Movimiento;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioOperaciones;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioMostrarMovimientos {

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
    void mostrarMovimientosExitosamente() throws CuentaNoEncontradaException, MovimientosVaciosException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);

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

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuentaMock);

        // Mockeo la búsqueda de movimientos
        when(movimientosDao.findMovimientos(cbu)).thenReturn(movimientosMock);

        // Ejecuto el método a testear
        List<Movimiento> resultado = servicioOperaciones.mostrarMovimientos(cbu);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(movimiento1));
        assertTrue(resultado.contains(movimiento2));

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, times(1)).findMovimientos(cbu);
    }

    @Test
    void mostrarMovimientosCuentaNoEncontrada() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo que la cuenta no existe
        when(cuentaDao.findCuenta(cbu)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioOperaciones.mostrarMovimientos(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, never()).findMovimientos(anyLong());
    }

    @Test
    void mostrarMovimientosVacios() throws CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);

        List<Movimiento> movimientosVacios = new ArrayList<>();

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuentaMock);

        // Mockeo la búsqueda de movimientos (sin movimientos)
        when(movimientosDao.findMovimientos(cbu)).thenReturn(movimientosVacios);

        // Llamo al método y espero la excepción
        MovimientosVaciosException exception = assertThrows(
                MovimientosVaciosException.class,
                () -> servicioOperaciones.mostrarMovimientos(cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta no tiene movimientos.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(movimientosDao, times(1)).findMovimientos(cbu);
    }
}
