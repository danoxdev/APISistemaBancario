package ar.edu.utn.frbb.tup.presentacion.prestamos;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteSinPrestamosException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.PrestamoDao;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestControladorListarPrestamos {

    @InjectMocks
    private ServicioPrestamo servicioPrestamo;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private PrestamoDao prestamoDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPrestamosExitosamente() throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        // Datos de entrada
        Long dniCliente = 12345678L;

        // Cliente mockeado
        Cliente cliente = new Cliente();
        cliente.setDni(dniCliente);

        // Préstamos mockeados
        Prestamo prestamo1 = new Prestamo(1, dniCliente, 5000.0, 12, 0, 5000.0);
        Prestamo prestamo2 = new Prestamo(2, dniCliente, 10000.0, 24, 0, 10000.0);
        Set<Prestamo> prestamos = new HashSet<>();
        prestamos.add(prestamo1);
        prestamos.add(prestamo2);

        // Configuración de mocks
        when(clienteDao.findCliente(dniCliente)).thenReturn(cliente);
        when(prestamoDao.findPrestamosDelCliente(dniCliente)).thenReturn(prestamos);

        // Ejecutar método
        Set<Prestamo> resultado = servicioPrestamo.listarPrestamos(dniCliente);

        // Validaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(prestamo1));
        assertTrue(resultado.contains(prestamo2));

        // Verificar interacciones con mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(prestamoDao, times(1)).findPrestamosDelCliente(dniCliente);
    }

    @Test
    void listarPrestamosClienteNoEncontrado() {
        // Datos de entrada
        Long dniCliente = 98765432L;

        // Configurar mocks
        when(clienteDao.findCliente(dniCliente)).thenReturn(null); // Simula cliente no encontrado

        // Ejecutar y verificar excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioPrestamo.listarPrestamos(dniCliente)
        );

        assertEquals("No existe un cliente con el DNI ingresado", exception.getMessage());
        verify(clienteDao, times(1)).findCliente(dniCliente);
    }


    @Test
    void listarPrestamosClienteSinPrestamos() throws ClienteNoEncontradoException {
        // Datos de entrada
        Long dniCliente = 12345678L;

        // Cliente mockeado
        Cliente cliente = new Cliente();
        cliente.setDni(dniCliente);

        // Configuración de mocks
        when(clienteDao.findCliente(dniCliente)).thenReturn(cliente);
        when(prestamoDao.findPrestamosDelCliente(dniCliente)).thenReturn(Collections.emptySet());

        // Ejecutar y verificar excepción
        ClienteSinPrestamosException exception = assertThrows(
                ClienteSinPrestamosException.class,
                () -> servicioPrestamo.listarPrestamos(dniCliente)
        );

        assertEquals("El cliente no tiene préstamos", exception.getMessage());
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(prestamoDao, times(1)).findPrestamosDelCliente(dniCliente);
    }
}
