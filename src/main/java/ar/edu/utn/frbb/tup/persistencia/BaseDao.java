package ar.edu.utn.frbb.tup.persistencia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {

    public void inicializarArchivo(String encabezado, String rutaArchivo){
        PrintWriter writer = null;

        try {
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                //Si no existe, lo creo y guardo el Encabezado para saber el orden de los datos
                FileWriter fileWriter = new FileWriter(rutaArchivo, true);
                writer = new PrintWriter(fileWriter);
                writer.println(encabezado);
            }

        } catch (IOException e) {
            System.out.println("No se pudo abrir el archivo");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void saveInfo(String info, String rutaArchivo){
        //En el try abre directamente el archivo, y cuando termina cierra automaticamente el recurso

        try (FileWriter archivo = new FileWriter(rutaArchivo, true);){

            PrintWriter writer = new PrintWriter(archivo);

            writer.println(info);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Id se refiere al identificador a cual linea borrar, ya sea DNI o CVU
    public void deleteInfo(long id, String rutaArchivo){
        //Lo que hace deleteInfo es leer todoo el archivo, guardarlo en una variable y reescribirlo exceputando la linea que tiene que eliminar
        try {
            StringBuilder contenido = new StringBuilder(); //Creo el contenido para guardar todoo lo leido

            FileReader fileReader = new FileReader(rutaArchivo);
            BufferedReader reader = new BufferedReader(fileReader);

            //Primero agrego el encabezado al contenido,
            String linea = reader.readLine();
            contenido.append(linea).append("\n");

            while ((linea = reader.readLine()) != null) { //Condicion para que lea el archivo hasta el final y lo guarde en la variable linea
                String[] datos = linea.split(",");

                if (Long.parseLong(datos[0]) != id){ //Voy agregando todas las lineas del Archivo excepto las lineas que tengo que eliminar con el DNI dado
                    contenido.append(linea).append("\n"); //Agrego la linea al contenido
                }
            }

            FileWriter fileWriter = new FileWriter(rutaArchivo);
            PrintWriter writer = new PrintWriter(fileWriter);

            //Escribo todoo el contenido excepto con lo que quiero eliminar
            writer.write(contenido.toString());

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T findInfo(long id, String rutaArchivo){
        try {
            File file = new File(rutaArchivo);

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String linea; //Leo el encabezado
            linea = reader.readLine(); //Salto encabezado

            while ((linea = reader.readLine()) != null) { //Leo todas las lineas de info hasta la ultima
                String[] datos = linea.split(",");

                if (Long.parseLong(datos[0]) == id){ //Si cumple condicion significa que la info buscada fue encontrada
                    reader.close();
                    return parseDatosToObjet(datos);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<T> findAllInfo(String rutaArchivo){
        List<T> info = new ArrayList<>();

        try {

            File file = new File(rutaArchivo);

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String linea; //Leo el encabezado
            linea = reader.readLine(); //Salto encabezado

            while ((linea = reader.readLine()) != null) { //Condicion para que lea el archivo hasta el final y lo guarde en la variable linea

                //Empiezo a leer las lineas de informacion, y cada linea la divido por comas con el '.split(",")', para tener los datos de cada objeto
                String[] datos = linea.split(",");

                //Guardo en una lista todos los objetos leidos
                info.add(parseDatosToObjet(datos));

            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return info;
    }

    public abstract T parseDatosToObjet(String[] datos);

}
