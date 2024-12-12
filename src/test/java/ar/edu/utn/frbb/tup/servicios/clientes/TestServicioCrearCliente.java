package ar.edu.utn.frbb.tup.servicios.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TestServicioCrearCliente {

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
    void crearClienteExitosamente() throws ClienteExistenteException, ClienteMenorDeEdadException {
        // Preparo datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setNombre("Juan");
        clienteDto.setApellido("Pérez");
        clienteDto.setDomicilio("Calle Falsa 123");
        clienteDto.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        clienteDto.setBanco("Banco Nación");
        clienteDto.setTipoPersona("PERSONA_FISICA");

        // Mockeo las validaciones (no lanzan excepciones)
        doNothing().when(validacionesServicios).validarClienteYaExiste(clienteDto);
        doNothing().when(validacionesServicios).esMayordeEdad(clienteDto.getFechaNacimiento());

        // Mockeo el guardado del cliente (no lanza excepción)
        doNothing().when(clienteDao).saveCliente(any(Cliente.class));

        // Ejecuto el método a testear
        Cliente resultado = servicioClientes.crearCliente(clienteDto);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(clienteDto.getDni(), resultado.getDni());
        assertEquals(clienteDto.getNombre(), resultado.getNombre());
        assertEquals(clienteDto.getApellido(), resultado.getApellido());
        assertEquals(clienteDto.getBanco(), resultado.getBanco());

        // Verifico las interacciones con los mocks
        verify(validacionesServicios, times(1)).validarClienteYaExiste(clienteDto);
        verify(validacionesServicios, times(1)).esMayordeEdad(clienteDto.getFechaNacimiento());
        verify(clienteDao, times(1)).saveCliente(any(Cliente.class));
    }

    @Test
    void crearClienteExistente() throws ClienteExistenteException, ClienteMenorDeEdadException {
        // Preparo datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setFechaNacimiento(LocalDate.of(1990, 1, 1)); // mayor de edad

        // Mockeo que el cliente ya existe
        doThrow(new ClienteExistenteException("Ya existe un cliente con el DNI ingresado"))
                .when(validacionesServicios).validarClienteYaExiste(clienteDto);

        // Llamo al método y espero la excepción
        ClienteExistenteException exception = assertThrows(
                ClienteExistenteException.class,
                () -> servicioClientes.crearCliente(clienteDto)
        );

        // Verifico el mensaje
        assertEquals("Ya existe un cliente con el DNI ingresado", exception.getMessage());

        // Verifico que no se haya intentado guardar el cliente
        verify(validacionesServicios, times(1)).validarClienteYaExiste(clienteDto);
        verify(validacionesServicios, never()).esMayordeEdad(any(LocalDate.class));
        verify(clienteDao, never()).saveCliente(any(Cliente.class));
    }

    @Test
    void crearClienteMenorDeEdad() throws ClienteExistenteException, ClienteMenorDeEdadException {
        // Preparo datos de entrada con un cliente menor de edad
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        // Suponiendo fecha actual 2024, ponemos fecha de nacimiento 2010 para que tenga menos de 18 años
        clienteDto.setFechaNacimiento(LocalDate.of(2010, 1, 1));

        // Mockeo que el cliente no existe
        doNothing().when(validacionesServicios).validarClienteYaExiste(clienteDto);
        // Mockeo que es menor de edad
        doThrow(new ClienteMenorDeEdadException("El cliente no es mayor de edad"))
                .when(validacionesServicios).esMayordeEdad(clienteDto.getFechaNacimiento());

        // Llamo al método y espero la excepción
        ClienteMenorDeEdadException exception = assertThrows(
                ClienteMenorDeEdadException.class,
                () -> servicioClientes.crearCliente(clienteDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("El cliente no es mayor de edad", exception.getMessage());

        // Verifico que no se haya guardado el cliente
        verify(validacionesServicios, times(1)).validarClienteYaExiste(clienteDto);
        verify(validacionesServicios, times(1)).esMayordeEdad(clienteDto.getFechaNacimiento());
        verify(clienteDao, never()).saveCliente(any(Cliente.class));
    }

}
