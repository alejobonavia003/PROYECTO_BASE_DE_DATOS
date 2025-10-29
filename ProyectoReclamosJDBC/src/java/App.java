package src.java;

import java.sql.*;
import java.util.Scanner;

/**
 * esta clase se conecta con la base de datos local de mi pc
 * luego abre un menu donde te da 3 opciones 
 * listar reclamos
 * agregar reclamos 
 * eliminar reclamos
 * 
 * como compilar y ejecutar : 
 * compilar: javac -cp lib/postgresql-42.7.5.jar src/java/*.java
 * ejecutar: java -cp ".:lib/postgresql-42.7.5.jar" src.java.App
 * 
 * como commpilar a ejecutable
 * "dentro de src/java" > jar cvfm Ejecutable.jar MANIFEST.MF App.class
 */

public class App {
    // no voy a usar driver por que lo compilo de /lib
    private static final String url = "jdbc:postgresql://localhost:5432/proyecto_reclamos";
    private static final String user = "postgres";
    private static final String password = "12345";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            connection.setAutoCommit(true);
            Scanner scanner = new Scanner(System.in);

            int opcion;

            do {
                System.out.println("\n========= MENÚ PRINCIPAL =========");
                System.out.println("1. Agregar un usuario");
                System.out.println("2. Listar reclamos de un usuario");
                System.out.println("3. Eliminar reclamo por ID");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // limpiar buffer

                //TODO: hay que modularizar y pasar los casos a metodos a parte 

                String query;
                PreparedStatement statement;
                ResultSet resultSet;

                switch (opcion) {
                    case 1:
                        System.out.println("=== INGRESAR UN USUARIO ===");
                        System.out.println("INGRESE EL DNI");
                        int dni = scanner.nextInt();
                        scanner.nextLine();
                       
                        System.out.println("INGRESE EL NOMBRE");
                        String nombre = scanner.nextLine();

                        System.out.println("INGRESE EL APELLIDO");
                        String apellido = scanner.nextLine();

                        System.out.println("INGRESE EL TELEFONO");
                        String tel = scanner.nextLine();

                        System.out.println("INGRESE LA DIRECCION");
                        String direccion = scanner.nextLine();
                        query = "INSERT INTO Usuario(tel, direccion) VALUES (?, ?)";
                        try {
                            statement = connection.prepareStatement(query);
                            statement.setString(1, tel);
                            statement.setString(2, direccion);
                            int fila = statement.executeUpdate();
                            if(fila <= 0){
                                System.out.println("Error al cargar el usuario");
                            }else{
                                query = "INSERT INTO Persona(id, dni, apellido, nombre) VALUES (?, ?, ?, ?)";
                                String query2 = "SELECT id FROM Usuario ORDER BY id DESC LIMIT 1";
                                statement = connection.prepareStatement(query2);
                                resultSet = statement.executeQuery();
                                if (resultSet.next()) {
                                    int id = resultSet.getInt(1);
                                    statement = connection.prepareStatement(query);
                                    statement.setInt(1, id);
                                    statement.setInt(2, dni);
                                    statement.setString(3, apellido);
                                    statement.setString(4, nombre);
                                    fila = statement.executeUpdate();
                                    if(fila <= 0){
                                        System.out.println("Error al cargar la informacion personal");
                                    }
                                    else{
                                        System.out.println("Usuario agregado exitosamente");
                                    }
                                }else{
                                    System.out.println("Error al encontrar el id");
                                }
                            }                           
                        } catch (SQLException e) {
                            System.out.println("error al agregar el usuario: " + e.getMessage());
                        }
                        

                    break;
                    case 2:
                    System.out.println("=== LISTA UN RECLAMO DE UN USUARIOS (vas a elejir un id)=====");
                    System.out.println("==ELIJE UN USUARIO POR ID======");
                        query = "select * from usuario join persona on usuario.id = persona.id";
                        statement = connection.prepareStatement(query);
                        resultSet = statement.executeQuery();
                        // Print results.
                        while(resultSet.next()) {
                            // Quarter

                            System.out.print(" ID: " + resultSet.getString(1));
                            System.out.print(" TEL: " + resultSet.getString(2));
                            System.out.print(" DIRECCION: " + resultSet.getString(3));
                            System.out.print(" DNI: " + resultSet.getString(5));
                            System.out.print(" NOMBRE: " + resultSet.getString(7));
                            System.out.print(" APELLIDO: " + resultSet.getString(6));
                            System.out.print("\n");
                        }

                        System.out.println("ELIJE UN ID DE USUARIO");
                        int id = scanner.nextInt();
                        scanner.nextLine();

                        try {
                        String usuario = "";
                        query = "select apellido from  persona where id = ?";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        resultSet = statement.executeQuery();
                        if (resultSet.next()) {  
                            usuario = resultSet.getString("apellido");
                        } else {
                            System.out.println("NO SE ENCONTRO UN USUARIO CON ESE ID");
                            return;
                        }

                        query = "SELECT r.nro as numero_de_reclamo, r.fecha_resol as fecha_de_resolucion, COUNT(l.numero) as cantidad_de_rellamados FROM Reclamo r LEFT JOIN Llamado l ON r.nro = l.nro WHERE r.id = ? GROUP BY r.nro, r.fecha_resol ORDER BY r.nro;";
                        
                            statement = connection.prepareStatement(query);
                            statement.setInt(1, id);
                            resultSet = statement.executeQuery();
                            System.out.println("LISTADO DE RECLAMOS DEL USUARIO "+usuario+".");

                            if(!resultSet.next()){
                                System.out.println("NO HAY RECLAMOS PARA ESTE USUARIO");
                            } else {
                                do {
                                    System.out.print(" NRO DE RECLAMO: " + resultSet.getString(1));
                                    System.out.print(" FECHA DE RESOLUCION: " + resultSet.getString(2));
                                    System.out.print("\n");
                                } while(resultSet.next());
                            }
                        } catch (SQLException e) {
                            System.out.println("error al listar un reclamo: " + e.getMessage());
                        }



                        break;
                    case 3:
                        
                    System.out.println("LISTA DE RECLAMOS/n");
                        query = "select * from reclamo";
                        statement = connection.prepareStatement(query);
                        resultSet = statement.executeQuery();

                        while(resultSet.next()){
                            System.out.println("Nro_Reclamo: " + resultSet.getString(1));
                            System.out.println("Fecha_Resolucion: " + resultSet.getString(2));
                            System.out.println("ID_Usuario: " + resultSet.getString(3));
                            System.out.println(" \n");

                        }

                        System.out.println("Ingrese un NRO de reclamo a eliminar");
                        int idr = scanner.nextInt();
                        String delete = "Delete from reclamo where nro = ?";

                       
                        try {
                             statement = connection.prepareStatement(delete);   
                             statement.setInt(1, idr);
                             int filas = statement.executeUpdate();
                            if (filas > 0){
                                System.out.println("Reclamo eliminado correctamente.");
                            }else{
                                System.out.println("No se encontró un reclamo con ese NRO.");
                            }
                        } catch (SQLException e) {
                            System.out.println("error al eliminar el reclamo"  + e.getMessage());
                        }  
                        break;
                    case 0:
                        System.out.println("saliendo del programa...");
                        break;
                    default:
                        System.out.println("intente nuevamente.");
                }

            } while (opcion != 0);


        } catch(SQLException sqle) {
            sqle.printStackTrace();
            System.err.println("Error connecting: " + sqle);
        }

    }
    
}


/** a) 
 * 
select persona.apellido, persona.nombre, persona.dni, cant_reclamos from persona join (select * from usuario join (select u.id as ud, count(u.nro) as cant_reclamos from reclamo u group by u.id)
on usuario.id = ud)
on persona.id = ud;
 */


 /**
  c) 
  SELECT deriva.nro,
    COUNT(DISTINCT deriva.id) AS cantidad_empleados_asignados
FROM deriva
GROUP BY deriva.nro
HAVING COUNT(DISTINCT deriva.id) > 1;

  */