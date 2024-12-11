package ar.edu.utn.frbb.tup.presentacion.prestamos;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaMonedaNoExisteException;
import ar.edu.utn.frbb.tup.modelo.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.PrestamoDao;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import ar.edu.utn.frbb.tup.servicios.ServicioScoreCrediticio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestControladorSolicitarPrestamo {

    @InjectMocks
    private ServicioPrestamo servicioPrestamo;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private PrestamoDao prestamoDao;

    @Mock
    private ServicioScoreCrediticio servicioScoreCrediticio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void solicitarPrestamoExitosamente() throws ClienteNoEncontradoException, CuentaMonedaNoExisteException {
        // Datos de entrada
        Long dniCliente = 12345678L;
        int plazoMeses = 12;
        double monto = 10000.00;
        String tipoMoneda = "PESOS";

        // Cliente mockeado
        Cliente cliente = new Cliente();
        cliente.setDni(dniCliente);

        // Cuenta mockeada
        Cuenta cuenta = new Cuenta();
        cuenta.setDniTitular(dniCliente);
        cuenta.setSaldo(5000.00);
        cuenta.setTipoMoneda(TipoMoneda.PESOS);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        // Configuración de mocks
        when(clienteDao.findCliente(dniCliente)).thenReturn(cliente);
        when(cuentaDao.findAllCuentasDelCliente(dniCliente)).thenReturn(Collections.singleton(cuenta));
        when(servicioScoreCrediticio.scoreCrediticio(dniCliente)).thenReturn(true);
        when(prestamoDao.findAllPrestamos()).thenReturn(new ArrayList<>());

        // Ejecutar método
        Map<String, Object> resultado = servicioPrestamo.solicitarPrestamo(dniCliente, plazoMeses, monto, tipoMoneda);

        // Validaciones
        assertNotNull(resultado);
        assertEquals("Aprobado", resultado.get("estado"));
        assertTrue(resultado.get("mensaje").toString().contains("El préstamo fue acreditado en su cuenta bancaria"));
        assertEquals(plazoMeses, ((List<?>) resultado.get("planPagos")).size());

        // Verificar interacciones con mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dniCliente);
        verify(servicioScoreCrediticio, times(1)).scoreCrediticio(dniCliente);
        verify(prestamoDao, times(1)).savePrestamo(any());
        verify(cuentaDao, times(1)).saveCuenta(any());
    }

    @Test
    void solicitarPrestamoClienteNoEncontrado() {
        // Datos de entrada
        Long dniCliente = 98765432L;
        int plazoMeses = 12;
        double monto = 10000.00;
        String tipoMoneda = "PESOS";

        // Configurar mocks
        when(clienteDao.findCliente(dniCliente)).thenReturn(null);

        // Ejecutar y verificar excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioPrestamo.solicitarPrestamo(dniCliente, plazoMeses, monto, tipoMoneda)
        );

        assertEquals("No existe un cliente con el DNI ingresado", exception.getMessage());
        verify(clienteDao, times(1)).findCliente(dniCliente);
    }

    @Test
    void solicitarPrestamoCuentaNoExiste() throws ClienteNoEncontradoException {
        // Datos de entrada
        Long dniCliente = 12345678L;
        int plazoMeses = 12;
        double monto = 10000.00;
        String tipoMoneda = "PESOS";

        // Cliente mockeado
        Cliente cliente = new Cliente();
        cliente.setDni(dniCliente);

        // Configuración de mocks
        when(clienteDao.findCliente(dniCliente)).thenReturn(cliente);
        when(cuentaDao.findAllCuentasDelCliente(dniCliente)).thenReturn(Collections.emptySet());

        // Ejecutar y verificar excepción
        CuentaMonedaNoExisteException exception = assertThrows(
                CuentaMonedaNoExisteException.class,
                () -> servicioPrestamo.solicitarPrestamo(dniCliente, plazoMeses, monto, tipoMoneda)
        );

        assertEquals("No existe una cuenta bancaria con la moneda ingresada", exception.getMessage());
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dniCliente);
    }
}
