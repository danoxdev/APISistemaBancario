package ar.edu.utn.frbb.tup.servicios.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteTieneCuentasException;
import ar.edu.utn.frbb.tup.excepciones.ClienteTienePrestamosException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiciosEliminarCliente {

    @InjectMocks
    private ServicioClientes servicioClientes;

    @Mock
    private ValidacionesServicios validacionesServicios;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private MovimientosDao movimientosDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void eliminarClienteExitosamente() throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {
        // Datos de entrada
        long dni = 12345678L;

        // Creo un cliente que se supone existe
        Cliente cliente = new Cliente();
        cliente.setDni(dni);

        // Mockeo que el cliente existe
        when(clienteDao.findCliente(dni)).thenReturn(cliente);

        // Mockeo las validaciones sin excepción (el cliente no tiene cuentas ni préstamos)
        doNothing().when(validacionesServicios).validarClienteSinCuentas(dni);
        doNothing().when(validacionesServicios).validarClienteSinPrestamos(dni);

        // Mockeo que no haya cuentas asociadas
        when(cuentaDao.getRelacionesDni(dni)).thenReturn(Collections.emptyList());

        // Mockeo la eliminación del cliente
        doNothing().when(clienteDao).deleteCliente(dni);

        // Ejecuto el método
        Cliente resultado = servicioClientes.eliminarCliente(dni);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cliente, resultado);

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
        verify(validacionesServicios, times(1)).validarClienteSinCuentas(dni);
        verify(validacionesServicios, times(1)).validarClienteSinPrestamos(dni);
        verify(clienteDao, times(1)).deleteCliente(dni);
        verify(cuentaDao, times(1)).getRelacionesDni(dni);
        // Como la lista está vacía, no se elimina ninguna cuenta ni movimiento.
        verifyNoMoreInteractions(clienteDao, cuentaDao, movimientosDao, validacionesServicios);
    }

    @Test
    void eliminarClienteNoEncontrado() throws ClienteTieneCuentasException, ClienteTienePrestamosException {
        // Datos de entrada
        long dni = 12345678L;

        // Mockeo que el cliente no existe (findCliente retorna null)
        when(clienteDao.findCliente(dni)).thenReturn(null);

        // Espero que se lance ClienteNoEncontradoException
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioClientes.eliminarCliente(dni)
        );

        assertTrue(exception.getMessage().contains("No se encontro el cliente con el DNI"));

        // Verifico las interacciones
        verify(clienteDao, times(1)).findCliente(dni);
        // Como no se encontró el cliente, no se validan cuentas ni préstamos
        verify(validacionesServicios, never()).validarClienteSinCuentas(dni);
        verify(validacionesServicios, never()).validarClienteSinPrestamos(dni);
        verify(clienteDao, never()).deleteCliente(anyLong());
    }

    @Test
    void eliminarClienteConCuentasAsociadas() throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {
        // Datos de entrada
        long dni = 12345678L;

        // Cliente simulado
        Cliente cliente = new Cliente();
        cliente.setDni(dni);

        // Configuración del mock
        when(clienteDao.findCliente(dni)).thenReturn(cliente);
        doThrow(new ClienteTieneCuentasException("No se puede eliminar el cliente porque tiene cuentas"))
                .when(validacionesServicios).validarClienteSinCuentas(dni);

        // Llamada al método y verificación de la excepción
        ClienteTieneCuentasException exception = assertThrows(
                ClienteTieneCuentasException.class,
                () -> servicioClientes.eliminarCliente(dni)
        );

        assertEquals("No se puede eliminar el cliente porque tiene cuentas", exception.getMessage());

        // Verificaciones
        verify(clienteDao, times(1)).findCliente(dni);
        verify(validacionesServicios, times(1)).validarClienteSinCuentas(dni);
        verify(validacionesServicios, never()).validarClienteSinPrestamos(dni);
        verify(clienteDao, never()).deleteCliente(anyLong());
    }

    @Test
    void eliminarClienteConPrestamosAsociados() throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {
        // Datos de entrada
        long dni = 12345678L;

        // Mockeo que el cliente existe
        Cliente cliente = new Cliente();
        cliente.setDni(dni);
        when(clienteDao.findCliente(dni)).thenReturn(cliente);

        // Mockeo que no tenga cuentas
        doNothing().when(validacionesServicios).validarClienteSinCuentas(dni);

        // Mockeo que sí tenga préstamos
        doThrow(new ClienteTienePrestamosException("No se puede eliminar el cliente porque tiene prestamos"))
                .when(validacionesServicios).validarClienteSinPrestamos(dni);

        // Espero ClienteTienePrestamosException
        ClienteTienePrestamosException exception = assertThrows(
                ClienteTienePrestamosException.class,
                () -> servicioClientes.eliminarCliente(dni)
        );

        assertEquals("No se puede eliminar el cliente porque tiene prestamos", exception.getMessage());

        // Verifico las interacciones
        verify(clienteDao, times(1)).findCliente(dni);
        verify(validacionesServicios, times(1)).validarClienteSinCuentas(dni);
        verify(validacionesServicios, times(1)).validarClienteSinPrestamos(dni);
        verify(clienteDao, never()).deleteCliente(anyLong());
    }
}
