package ar.edu.utn.frbb.tup.persistencia;

import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.TipoCuenta;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.TipoMoneda;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CuentaDao extends BaseDao<Cuenta> {
    private final String RUTA_ARCHIVO = "src/main/java/ar/edu/utn/frbb/tup/persistencia/data/cuentas.txt";
    public void inicializarCuentas(){
        String encabezado = "CBU, Cliente, Tipo de cuenta, Moneda, Alias, Fecha de creacion, Saldo";
        inicializarArchivo(encabezado, RUTA_ARCHIVO);
    }

    public void saveCuenta(Cuenta cuenta){
        String infoAguardar = cuenta.getCbu() + "," + cuenta.getDniTitular() + "," + cuenta.getTipoCuenta() + "," + cuenta.getTipoMoneda() + "," + cuenta.getAlias() + "," + cuenta.getFechaCreacion() + "," + cuenta.getSaldo();

        saveInfo(infoAguardar, RUTA_ARCHIVO);
    }

    public void deleteCuenta(long CBU){
        deleteInfo(CBU, RUTA_ARCHIVO);

    }

    public Cuenta findCuenta(long CBU){
        return findInfo(CBU, RUTA_ARCHIVO);
    }

    public List<Cuenta> findAllCuentas() throws CuentasVaciasException{
        List<Cuenta> cuentas = findAllInfo(RUTA_ARCHIVO);

        if (cuentas.isEmpty()){ //Si la lista esta vacia significa que no hay clientes registrados
            throw new CuentasVaciasException("No hay cuentas registradas");
        }

        return cuentas;
    }

    public Cuenta findCuentaDelCliente(long cbu, long dni){
        //Funcion para encontrar la cuenta del cliente, se hace para que el usuario SOLO pueda eliminar la cuenta del cliente que le pertenece
        try {
            File file = new File(RUTA_ARCHIVO);

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String linea; //Leo el encabezado
            linea = reader.readLine(); //Salto encabezado

            while ((linea = reader.readLine()) != null) { //Condicion para que lea el archivo hasta el final y lo guarde en la variable linea

                //Empiezo a leer las lineas de las relaciones, y cada linea la divido por comas con el '.split(",")', para tener los datos de las relaciones
                String[] datos = linea.split(",");

                //Retorno la cuenta si se encuentra la cuenta del cliente ingresado
                if (Long.parseLong(datos[0]) == cbu && Long.parseLong(datos[1]) == dni) {
                    return parseDatosToObjet(datos);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Retorno Null por si no lo encuentra
        return null;
    }

    public List<Long> getRelacionesDni(Long dni){
        //Funcion para guardar todos los CBUs que tiene el dni ingresado
        List<Long> CvuRelacionados = new ArrayList<>();
        try {
            File file = new File(RUTA_ARCHIVO);

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            //Primero agrego el encabezado al contenido,
            String linea = reader.readLine();

            while ((linea = reader.readLine()) != null) { //Condicion para que lea el archivo hasta el final y lo guarde en la variable linea
                String[] datos = linea.split(",");

                if (Long.parseLong(datos[1]) == dni){ //Agrego el cbu relacionado con el dni
                    CvuRelacionados.add(Long.parseLong(datos[0]));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return CvuRelacionados;

    }

    //Funcion para parsear los datos leidos del archivo a un objeto tipo 'Cuenta'
    @Override
    public Cuenta parseDatosToObjet(String[] datos){
        Cuenta cuenta = new Cuenta();
        cuenta.setCbu(Long.parseLong(datos[0]));
        cuenta.setDniTitular(Long.parseLong(datos[1]));
        cuenta.setTipoCuenta(TipoCuenta.valueOf(datos[2]));
        cuenta.setTipoMoneda(TipoMoneda.valueOf(datos[3]));
        cuenta.setAlias(datos[4]);
        cuenta.setFechaCreacion(LocalDate.parse(datos[5]));
        cuenta.setSaldo(Double.parseDouble(datos[6]));
        return cuenta;
    }
}

