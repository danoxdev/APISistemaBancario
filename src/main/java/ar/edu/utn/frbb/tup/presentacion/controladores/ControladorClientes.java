package ar.edu.utn.frbb.tup.presentacion.controladores;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/clientes")
public class ControladorClientes {
    private ValidacionesPresentacion validacionesPresentacion;
    private ServicioClientes servicioClientes;


    public ControladorClientes(ValidacionesPresentacion validacionesPresentacion, ServicioClientes servicioClientes) {
        this.validacionesPresentacion= validacionesPresentacion;
        this.servicioClientes = servicioClientes;
        servicioClientes.inicializarClientes();
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> getClientes() throws ClientesVaciosException {
        List<Cliente> clientes = servicioClientes.mostrarClientes();
        if (clientes.isEmpty()) {
            throw new ClientesVaciosException("No hay clientes registrados.");
        }
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClientePorDni(@PathVariable Long dni) throws ClienteNoEncontradoException {
        validacionesPresentacion.validarDni(dni);
        return new ResponseEntity<>(servicioClientes.buscarCliente(dni), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteExistenteException, ClienteMenorDeEdadException {
        validacionesPresentacion.validarDatosCompletos(clienteDto);
        validacionesPresentacion.validarDni(clienteDto.getDni());
        return new ResponseEntity<>(servicioClientes.crearCliente(clienteDto), HttpStatus.CREATED);
    }


    @DeleteMapping("/{dni}")
    public ResponseEntity<Cliente> eliminarCliente(@PathVariable Long dni) throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {
        validacionesPresentacion.validarDni(dni);
        return new ResponseEntity<>(servicioClientes.eliminarCliente(dni), HttpStatus.OK);
    }

}

