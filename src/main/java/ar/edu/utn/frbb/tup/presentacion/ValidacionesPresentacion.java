package ar.edu.utn.frbb.tup.presentacion;

import ar.edu.utn.frbb.tup.modelo.TipoMoneda;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.presentacion.DTOs.PrestamoDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ValidacionesPresentacion {

    // VALIDACIONES DE CLIENTES //
    public void validarDatosCompletos(ClienteDto cliente) {

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
        if (cliente.getTipoPersona() == null || cliente.getTipoPersona().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de persona");
    }

    //Validar DNI
    public void validarDni(Long dni) {
        try {
            if (dni <= 0 ) {
                throw new IllegalArgumentException("Error: DNI invalido");
            }
            if (dni < 1000000 || dni > 99999999) {
                throw new IllegalArgumentException("Error: El dni debe tener entre 7 y 8 digitos");
            }
        } catch(NumberFormatException e){
            throw new IllegalArgumentException("Error: El dni debe ser un numero");
        }
    }

    // VALIDACIONES DE CUENTAS //
    public void validarCuenta(CuentaDto cuentaDto ){

        //Alias de la cuenta
        if (cuentaDto.getAlias() == null || cuentaDto.getAlias().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un nombre");
        //Tipo de cuenta
        if (cuentaDto.getTipoCuenta() == null || cuentaDto.getTipoCuenta().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de cuenta");
        //Tipo de moneda
        if (cuentaDto.getTipoMoneda() == null || cuentaDto.getTipoMoneda().isEmpty()) throw new IllegalArgumentException("Error: Ingrese un tipo de moneda");
        validarTipoMoneda(cuentaDto.getTipoMoneda());
        //DNI Titular
        if (cuentaDto.getDniTitular() == null) throw new IllegalArgumentException("Error: Ingrese un dni");
        if (cuentaDto.getDniTitular() < 10000000 || cuentaDto.getDniTitular() > 99999999) throw new IllegalArgumentException("Error: El dni debe tener entre 7 y 8 digitos");
    }

    public void validarTipoMoneda(String tipoMoneda) {
        try {
            // Convierto el String a TipoMoneda
            TipoMoneda moneda = TipoMoneda.fromString(tipoMoneda);

            // Valido que sea una moneda válida
            if (!moneda.equals(TipoMoneda.PESOS) && !moneda.equals(TipoMoneda.DOLARES)) {
                throw new IllegalArgumentException("El tipo de moneda debe ser 'PESOS' o 'DOLARES'.");
            }
        } catch (IllegalArgumentException e) {
            // Si no se puede convertir, lanzamos una excepción
            throw new IllegalArgumentException("El tipo de moneda ingresado no es válido. Debe ser 'PESOS' o 'DOLARES'.");
        }
    }

    //Validar CBU
    public void validarCBU (Long cbu) {
        if (cbu == null || cbu <= 0) {
            throw new IllegalArgumentException("Error: CBU invalido");
        }
    }
    // VALIDACIONES DE OPERACIONES //

    //Monto
    public void validarMonto(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser mayor a 0.");
        }
    }

    // VALIDACIONES DE PRESTAMOS //

    public void validarSolicitudPrestamo(PrestamoDto prestamoDto) {

        //DNI Titular
        if (prestamoDto.getDniCliente() == null) throw new IllegalArgumentException("Error: Ingrese un dni");
        if (prestamoDto.getDniCliente() < 10000000 || prestamoDto.getDniCliente() > 99999999) throw new IllegalArgumentException("Error: El dni debe tener entre 7 y 8 digitos");

        // Monto
        if (prestamoDto.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto del préstamo debe ser mayor a 0.");
        }

        // Plazo en meses
        if (prestamoDto.getPlazoMeses() <= 0) {
            throw new IllegalArgumentException("El plazo del préstamo debe ser mayor a 0.");
        }

        // Moneda
        if (prestamoDto.getTipoMoneda() == null || prestamoDto.getTipoMoneda().trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda no puede ser nula o vacía.");
        }
        validarTipoMoneda(prestamoDto.getTipoMoneda());
    }
}