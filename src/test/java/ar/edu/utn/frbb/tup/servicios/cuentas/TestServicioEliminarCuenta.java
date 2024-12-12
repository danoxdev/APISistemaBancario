package ar.edu.utn.frbb.tup.servicios.cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaTieneSaldoException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioEliminarCuenta {

    @InjectMocks
    private ServicioCuentas servicioCuentas;

    @Mock
    private ValidacionesServicios validacionesServicios;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private MovimientosDao movimientosDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void eliminarCuentaExitosamente() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);
        cuentaMock.setDniTitular(dni);
        cuentaMock.setSaldo(0);

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteExistente(dni);
        doNothing().when(validacionesServicios).validarCuentasCliente(dni);
        doNothing().when(validacionesServicios).validarSaldoCuenta(cbu);

        // Mockeo la búsqueda de la cuenta del cliente
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(cuentaMock);

        // Mockeo el borrado de la cuenta y movimientos
        doNothing().when(cuentaDao).deleteCuenta(cbu);
        doNothing().when(movimientosDao).deleteMovimiento(cbu);

        // Ejecuto el método a testear
        Cuenta resultado = servicioCuentas.eliminarCuenta(dni, cbu);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cuentaMock.getCbu(), resultado.getCbu());
        assertEquals(cuentaMock.getDniTitular(), resultado.getDniTitular());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasCliente(dni);
        verify(validacionesServicios, times(1)).validarSaldoCuenta(cbu);
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, times(1)).deleteCuenta(cbu);
        verify(movimientosDao, times(1)).deleteMovimiento(cbu);
    }

    @Test
    void eliminarCuentaClienteNoEncontrado() throws ClienteNoEncontradoException, CuentaTieneSaldoException, CuentasVaciasException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        // Mockeo que el cliente no existe
        doThrow(new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni))
                .when(validacionesServicios).validarClienteExistente(dni);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteExistente(dni);
        verify(validacionesServicios, never()).validarCuentasCliente(anyLong());
        verify(validacionesServicios, never()).validarSaldoCuenta(anyLong());
        verify(cuentaDao, never()).findCuentaDelCliente(anyLong(), anyLong());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }

    @Test
    void eliminarCuentaCuentasVacias() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteExistente(dni);
        doThrow(new CuentasVaciasException("No hay cuentas asociadas al cliente con DNI: " + dni))
                .when(validacionesServicios).validarCuentasCliente(dni);

        // Llamo al método y espero la excepción
        CuentasVaciasException exception = assertThrows(
                CuentasVaciasException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No hay cuentas asociadas al cliente con DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasCliente(dni);
        verify(validacionesServicios, never()).validarSaldoCuenta(anyLong());
        verify(cuentaDao, never()).findCuentaDelCliente(anyLong(), anyLong());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }

    @Test
    void eliminarCuentaNoEncontrada() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteExistente(dni);
        doNothing().when(validacionesServicios).validarCuentasCliente(dni);

        // Mockeo que la cuenta no existe
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("El cliente no tiene ninguna cuenta con el CBU: " + cbu, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasCliente(dni);
        verify(validacionesServicios, never()).validarSaldoCuenta(anyLong());
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }

    @Test
    void eliminarCuentaConSaldo() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException, CuentaTieneSaldoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 87654321L;

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);
        cuentaMock.setDniTitular(dni);
        cuentaMock.setSaldo(100); // Cuenta con saldo

        // Mockeo las validaciones
        doNothing().when(validacionesServicios).validarClienteExistente(dni);
        doNothing().when(validacionesServicios).validarCuentasCliente(dni);

        // Mockeo la búsqueda de la cuenta del cliente
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(cuentaMock);

        // Mockeo la validación de saldo (lanza excepción)
        doThrow(new CuentaTieneSaldoException("No se puede eliminar la cuenta porque tiene saldo"))
                .when(validacionesServicios).validarSaldoCuenta(cbu);

        // Llamo al método y espero la excepción
        CuentaTieneSaldoException exception = assertThrows(
                CuentaTieneSaldoException.class,
                () -> servicioCuentas.eliminarCuenta(dni, cbu)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se puede eliminar la cuenta porque tiene saldo", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteExistente(dni);
        verify(validacionesServicios, times(1)).validarCuentasCliente(dni);
        verify(validacionesServicios, times(1)).validarSaldoCuenta(cbu);
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(movimientosDao, never()).deleteMovimiento(anyLong());
    }
}