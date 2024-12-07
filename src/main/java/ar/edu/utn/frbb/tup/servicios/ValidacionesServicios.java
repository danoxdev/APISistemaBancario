package ar.edu.utn.frbb.tup.servicios;
import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.TipoMoneda;
import ar.edu.utn.frbb.tup.modelo.TipoPersona;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class ValidacionesServicios {

    // VALIDACIONES DE CLIENTES //

    public void esMayordeEdad(LocalDate fecha) throws ClienteMenorDeEdadException {
        int edad = LocalDate.now().getYear() - fecha.getYear();
        if (edad < 18) {
            throw new ClienteMenorDeEdadException("El cliente no es mayor de edad");
        }
    }

    public void validarDni(Long dni) {
        try {
            if (dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error: El dni debe tener entre 7 y 8 digitos");
            }
        } catch(NumberFormatException e){
            throw new IllegalArgumentException("Error: El dni debe ser un numero");
        }
    }

    //validar tipo de persona
    public void validarTipoPersona(String tipoPersona) {
        try {
            // Intenta convertirlo a TipoPersona
            TipoPersona.valueOf(tipoPersona);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error: El tipo de persona debe ser 'PERSONA_FISICA' o 'PERSONA_JURIDICA'");
        }
    }

    public void validarDatosCompletos(Cliente cliente) {

        //Valido que el usuario ingreso todos los datos para poder crear nuestro cliente
        //Nombre
        if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un nombre");
        //Apellido
        if (cliente.getApellido() == null || cliente.getApellido().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un apellido");
        //DNI
        if (cliente.getDni() == null) throw new IllegalArgumentException("Error: El DNI no puede ser nulo");
        //Domicilio
        if (cliente.getDomicilio() == null || cliente.getDomicilio().isEmpty()) throw new IllegalArgumentException("Error: Ingrese una direccion");
        //Fecha Nacimiento
        if (cliente.getFechaNacimiento() == null) throw new IllegalArgumentException("Error: Ingrese una fecha de nacimiento");
        //Banco
        if (cliente.getBanco() == null || cliente.getBanco().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un banco");
        //Tipo persona
        if (cliente.getTipoPersona() == null) throw new IllegalArgumentException("Error: Ingrese un tipo de persona");
    }

    public void validarClienteExistente(Cliente cliente) throws ClienteExistenteException {
        ClienteDao clienteDao = new ClienteDao();
        if (clienteDao.findCliente(cliente.getDni()) != null){
            throw new ClienteExistenteException("Ya existe un cliente con el DNI ingresado");
        }
    }

    // VALIDACIONES DE CUENTAS //

    public boolean validarCuentaDestino(String cuentaOrigen, String cuentaDestino) {
        if (!Objects.equals(cuentaOrigen, cuentaDestino)) {
            return true;
        } else {
            System.out.println("Error: el cbu de origen es igual al de destino.");
            return false;
        }
    }

    public boolean validarMonedaDestino(TipoMoneda tipoMonedaOrigen, TipoMoneda tipoMonedaDestino) {
        return tipoMonedaOrigen == tipoMonedaDestino;
    }

}
