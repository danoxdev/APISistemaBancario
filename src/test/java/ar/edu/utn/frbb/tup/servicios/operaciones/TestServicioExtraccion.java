package ar.edu.utn.frbb.tup.servicios.operaciones;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
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

class TestServicioExtraccion {

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
    void extraccionExitosamente() throws CuentaNoEncontradaException, CuentaSinDineroException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500.0;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);
        cuentaMock.setSaldo(1000.0);

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);
        doNothing().when(validacionesServicios).validarSaldo(cuentaMock, monto);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuentaMock);

        // Mockeo el borrado y guardado de la cuenta
        doNothing().when(cuentaDao).deleteCuenta(anyLong());
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));

        // Mockeo el guardado del movimiento
        doNothing().when(movimientosDao).saveMovimiento(anyString(), anyDouble(), anyLong());

        // Ejecuto el método a testear
        Operacion resultado = servicioOperaciones.extraccion(cbu, monto);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cbu, resultado.getCbu());
        assertEquals(500.0, resultado.getSaldoActual());
        assertEquals(monto, resultado.getMonto());
        assertEquals("Extraccion", resultado.getTipoOperacion());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(validacionesServicios, times(1)).validarSaldo(cuentaMock, monto);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(cuentaDao, times(1)).deleteCuenta(anyLong());
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, times(1)).saveMovimiento(anyString(), anyDouble(), anyLong());
    }

    @Test
    void extraccionCuentaNoEncontrada() throws CuentaSinDineroException, CuentaNoEncontradaException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500.0;

        // Mockeo la validación de cuenta existente
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo que la cuenta no existe
        when(cuentaDao.findCuenta(cbu)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioOperaciones.extraccion(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("La cuenta con el CBU especificado no existe.", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(validacionesServicios, never()).validarSaldo(any(Cuenta.class), anyDouble());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
    }

    @Test
    void extraccionSinDinero() throws CuentaNoEncontradaException, CuentaSinDineroException {
        // Preparo datos de entrada
        Long cbu = 11111111L;
        double monto = 500.0;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);
        cuentaMock.setSaldo(100.0); // Saldo insuficiente

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarCuentaExistente(cbu);

        // Mockeo la búsqueda de la cuenta
        when(cuentaDao.findCuenta(cbu)).thenReturn(cuentaMock);

        // Mockeo la validación de saldo (lanza excepción)
        doThrow(new CuentaSinDineroException("No posee saldo suficiente para realizar la operacion, su saldo es de $" + cuentaMock.getSaldo()))
                .when(validacionesServicios).validarSaldo(cuentaMock, monto);

        // Llamo al método y espero la excepción
        CuentaSinDineroException exception = assertThrows(
                CuentaSinDineroException.class,
                () -> servicioOperaciones.extraccion(cbu, monto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No posee saldo suficiente para realizar la operacion, su saldo es de $100.0", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarCuentaExistente(cbu);
        verify(validacionesServicios, times(1)).validarSaldo(cuentaMock, monto);
        verify(cuentaDao, times(1)).findCuenta(cbu);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(movimientosDao, never()).saveMovimiento(anyString(), anyDouble(), anyLong());
    }
}
