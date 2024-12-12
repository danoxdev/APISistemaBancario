package ar.edu.utn.frbb.tup.servicios.cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioActualizarAlias {

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
    void actualizarAliasExitosamente() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 11111111L;
        String nuevoAlias = "nuevoAlias";

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dni);

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setCbu(cbu);
        cuentaMock.setDniTitular(dni);
        cuentaMock.setAlias("aliasAnterior");

        List<Long> cuentasCbu = new ArrayList<>();
        cuentasCbu.add(cbu);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dni)).thenReturn(clienteMock);

        // Mockeo la validación de cuentas del cliente
        when(cuentaDao.getRelacionesDni(dni)).thenReturn(cuentasCbu);

        // Mockeo la búsqueda de la cuenta del cliente
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(cuentaMock);

        // Mockeo el borrado y guardado de la cuenta
        doNothing().when(cuentaDao).deleteCuenta(cbu);
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));

        // Ejecuto el método a testear
        Cuenta resultado = servicioCuentas.actualizarAlias(dni, cbu, nuevoAlias);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(nuevoAlias, resultado.getAlias());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).getRelacionesDni(dni);
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, times(1)).deleteCuenta(cbu);
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
    }

    @Test
    void actualizarAliasClienteNoEncontrado() {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 11111111L;
        String nuevoAlias = "nuevoAlias";

        // Mockeo que el cliente no existe
        when(clienteDao.findCliente(dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioCuentas.actualizarAlias(dni, cbu, nuevoAlias)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, never()).getRelacionesDni(anyLong());
        verify(cuentaDao, never()).findCuentaDelCliente(anyLong(), anyLong());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void actualizarAliasCuentasVacias() {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 11111111L;
        String nuevoAlias = "nuevoAlias";

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dni);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dni)).thenReturn(clienteMock);

        // Mockeo que no hay cuentas asociadas
        when(cuentaDao.getRelacionesDni(dni)).thenReturn(new ArrayList<>());

        // Llamo al método y espero la excepción
        CuentasVaciasException exception = assertThrows(
                CuentasVaciasException.class,
                () -> servicioCuentas.actualizarAlias(dni, cbu, nuevoAlias)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No hay cuentas asociadas al cliente con DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).getRelacionesDni(dni);
        verify(cuentaDao, never()).findCuentaDelCliente(anyLong(), anyLong());
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void actualizarAliasCuentaNoEncontrada() {
        // Preparo datos de entrada
        long dni = 12345678L;
        long cbu = 11111111L;
        String nuevoAlias = "nuevoAlias";

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dni);

        List<Long> cuentasCbu = new ArrayList<>();
        cuentasCbu.add(cbu);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dni)).thenReturn(clienteMock);

        // Mockeo la validación de cuentas del cliente
        when(cuentaDao.getRelacionesDni(dni)).thenReturn(cuentasCbu);

        // Mockeo que la cuenta no existe
        when(cuentaDao.findCuentaDelCliente(cbu, dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioCuentas.actualizarAlias(dni, cbu, nuevoAlias)
        );

        // Verifico el mensaje de la excepción
        assertEquals("El cliente no tiene ninguna cuenta con el CBU: " + cbu, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).getRelacionesDni(dni);
        verify(cuentaDao, times(1)).findCuentaDelCliente(cbu, dni);
        verify(cuentaDao, never()).deleteCuenta(anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }
}
