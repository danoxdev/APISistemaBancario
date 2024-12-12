package ar.edu.utn.frbb.tup.servicios.cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioMostrarCuentas {

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
    void mostrarCuentasExitosamente() throws ClienteNoEncontradoException, CuentaNoEncontradaException {
        // Preparo datos de entrada
        long dni = 12345678L;
        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dni);

        Set<Cuenta> cuentasMock = new HashSet<>();
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setCbu(11111111L);
        Cuenta cuenta2 = new Cuenta();
        cuenta2.setCbu(22222222L);
        cuentasMock.add(cuenta1);
        cuentasMock.add(cuenta2);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dni)).thenReturn(clienteMock);

        // Mockeo la búsqueda de las cuentas del cliente
        when(cuentaDao.findAllCuentasDelCliente(dni)).thenReturn(cuentasMock);

        // Ejecuto el método a testear
        Set<Cuenta> resultado = servicioCuentas.mostrarCuentas(dni);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(cuenta1));
        assertTrue(resultado.contains(cuenta2));

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dni);
    }

    @Test
    void mostrarCuentasClienteNoEncontrado() {
        // Preparo datos de entrada
        long dni = 12345678L;

        // Mockeo que el cliente no existe
        when(clienteDao.findCliente(dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioCuentas.mostrarCuentas(dni)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, never()).findAllCuentasDelCliente(anyLong());
    }

    @Test
    void mostrarCuentasNoEncontradas() throws ClienteNoEncontradoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dni);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dni)).thenReturn(clienteMock);

        // Mockeo que no hay cuentas para el cliente
        when(cuentaDao.findAllCuentasDelCliente(dni)).thenReturn(new HashSet<>());

        // Llamo al método y espero la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> servicioCuentas.mostrarCuentas(dni)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No hay cuentas asociadas al cliente con DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dni);
    }
}

