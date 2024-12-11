package ar.edu.utn.frbb.tup.presentacion.cuentas;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestControladorActualizarAlias {

    @InjectMocks
    private ServicioCuentas servicioCuentas;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    private Cliente cliente;
    private Cuenta cuenta;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializamos el cliente y la cuenta utilizando ClienteDto y CuentaDto
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setNombre("Juan");
        clienteDto.setApellido("Perez");
        clienteDto.setFechaNacimiento(LocalDate.ofEpochDay(1980-01-01));
        clienteDto.setDomicilio("Calle 123");
        clienteDto.setBanco("Banco Nacion");
        clienteDto.setTipoPersona("PERSONA_FISICA");

        cliente = new Cliente(clienteDto);

        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(cliente.getDni());
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("ahorroPesos");

        cuenta = new Cuenta(cuentaDto);
        cliente.addCuenta(cuenta); // Asociamos la cuenta al cliente
    }

    @Test
    public void testActualizarAlias_ClienteNoEncontrado() {
        // Configuramos el mock para que retorne null cuando se busque al cliente
        when(clienteDao.findCliente(12345678L)).thenReturn(null);

        // Ejecutamos el método y validamos la excepción
        assertThrows(ClienteNoEncontradoException.class, () -> {
            servicioCuentas.actualizarAlias(12345678L, cuenta.getCbu(), "NuevoAlias");
        });
    }

    @Test
    public void testActualizarAlias_CuentasVacias() throws ClienteNoEncontradoException {
        // Configuramos el mock para que retorne un cliente válido
        when(clienteDao.findCliente(12345678L)).thenReturn(cliente);
        when(cuentaDao.getRelacionesDni(12345678L)).thenReturn(List.of()); // Devuelve una lista vacía

        // Ejecutamos el método y validamos la excepción
        assertThrows(CuentasVaciasException.class, () -> {
            servicioCuentas.actualizarAlias(12345678L, cuenta.getCbu(), "NuevoAlias");
        });
    }

    @Test
    public void testActualizarAlias_CuentaNoEncontrada() throws ClienteNoEncontradoException, CuentasVaciasException {
        // Configuramos los mocks para retornar un cliente válido y cuentas asociadas
        when(clienteDao.findCliente(12345678L)).thenReturn(cliente);
        when(cuentaDao.getRelacionesDni(12345678L)).thenReturn(new ArrayList<>(Set.of(cuenta.getCbu())));
        when(cuentaDao.findCuentaDelCliente(cuenta.getCbu(), 12345678L)).thenReturn(null);

        // Ejecutamos el método y validamos la excepción
        assertThrows(CuentaNoEncontradaException.class, () -> {
            servicioCuentas.actualizarAlias(12345678L, cuenta.getCbu(), "NuevoAlias");
        });
    }

    @Test
    public void testActualizarAliasExitosamente() throws ClienteNoEncontradoException, CuentasVaciasException, CuentaNoEncontradaException {
        // Configuramos los mocks para que el cliente y la cuenta existan
        when(clienteDao.findCliente(12345678L)).thenReturn(cliente);
        when(cuentaDao.getRelacionesDni(12345678L)).thenReturn(List.of(cuenta.getCbu())); // Devuelve una lista con el CBU
        when(cuentaDao.findCuentaDelCliente(cuenta.getCbu(), 12345678L)).thenReturn(cuenta);

        // Ejecutamos el método para actualizar el alias
        Cuenta cuentaActualizada = servicioCuentas.actualizarAlias(12345678L, cuenta.getCbu(), "NuevoAlias");

        // Verificamos que el alias haya sido actualizado correctamente
        assertNotNull(cuentaActualizada);
        assertEquals("NuevoAlias", cuentaActualizada.getAlias());
        verify(cuentaDao).saveCuenta(cuentaActualizada); // Verificamos que la cuenta haya sido guardada
    }
}
