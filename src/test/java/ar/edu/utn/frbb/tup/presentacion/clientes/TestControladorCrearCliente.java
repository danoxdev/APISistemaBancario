package ar.edu.utn.frbb.tup.presentacion.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorClientes;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestControladorCrearCliente {

    @InjectMocks
    private ControladorClientes controladorClientes;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioClientes servicioClientes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearClienteExitosamente() throws ClienteExistenteException, ClienteMenorDeEdadException {
        //Cargo los datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setNombre("Juan");
        clienteDto.setApellido("Pérez");
        clienteDto.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        clienteDto.setBanco("Banco Nación");
        clienteDto.setTipoPersona("PERSONA_FISICA");

        //Creo el cliente a retornar
        Cliente clienteEsperado = new Cliente(clienteDto);

        //Simulo el comportamiento del servicio.
        when(servicioClientes.crearCliente(clienteDto)).thenReturn(clienteEsperado);

        //Ejecuto el método del controlador con los datos de entrada simulados.
        ResponseEntity<Cliente> response = controladorClientes.crearCliente(clienteDto);

        //Compruebo que el código de estado HTTP sea 201 (creación exitosa) y el cuerpo de la respuesta sea el cliente esperado.
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(clienteEsperado, response.getBody());

        //Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDatosCompletos(clienteDto);
        verify(servicioClientes, times(1)).crearCliente(clienteDto);
    }

    @Test
    void crearClienteExistente() throws ClienteExistenteException, ClienteMenorDeEdadException {
        // Cargo los datos de entrada
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);

        // Simulo que el cliente ya existe
        doThrow(new ClienteExistenteException("El cliente ya existe"))
                .when(servicioClientes).crearCliente(clienteDto);

        //Llamo al controlador y espero que se lance la excepción
        ClienteExistenteException exception = assertThrows(
                ClienteExistenteException.class,
                () -> controladorClientes.crearCliente(clienteDto)
        );

        //Verifico que el mensaje de la excepción sea el esperado.
        assertEquals("El cliente ya existe", exception.getMessage());

        //Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDatosCompletos(clienteDto);
        verify(servicioClientes, times(1)).crearCliente(clienteDto);
    }

    @Test
    void crearClienteMenorDeEdad() throws ClienteExistenteException, ClienteMenorDeEdadException {
        //Cargo los datos de entrada con un cliente menor de edad
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDni(12345678L);
        clienteDto.setFechaNacimiento(LocalDate.of(2010, 1, 1)); // Cliente menor de edad (fecha posterior a 18 años desde la fecha actual)

        //Simulo que el cliente es menor de edad
        doThrow(new ClienteMenorDeEdadException("El cliente es menor de edad"))
                .when(servicioClientes).crearCliente(clienteDto);

        //Llamo al controlador y espero que se lance la excepción
        ClienteMenorDeEdadException exception = assertThrows(
                ClienteMenorDeEdadException.class,
                () -> controladorClientes.crearCliente(clienteDto)
        );

        //Me aseguro que el mensaje de la excepción sea el esperado.
        assertEquals("El cliente es menor de edad", exception.getMessage());

        //Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDatosCompletos(clienteDto);
        verify(servicioClientes, times(1)).crearCliente(clienteDto);
    }

}
