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
            Scanner scanner = new Scanner(System.in);

            int opcion;

            do {
                System.out.println("\n========= MENÚ PRINCIPAL =========");
                System.out.println("1. Listar reclamos");
                System.out.println("2. Insertar nuevo reclamo");
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
                        query = "SELECT * FROM reclamo ";
                        statement = connection.prepareStatement(query);
                        resultSet = statement.executeQuery();
                              // Print results.
                        while(resultSet.next()) {
                            // Quarter
                            //TODO: aca tenemos que hacer algunos join 
                            //TODO: y listar bien la informacion del reclamo como persona deriva etc
                            System.out.print(" NRO: " + resultSet.getString(1));
                            System.out.print("; FECHA_RESOL: " + resultSet.getString(2));
                            System.out.print("; ID: " + resultSet.getString(3)) ;
                            System.out.print("\n   ");
                            //System.out.print("\n   ");
                        }
                        break;
                    case 2:
                    System.out.println("=== LISTA DE USUARIOS (vas a elejir un id)=====");
                        query = "select * from usuario join persona on usuario.id = persona.id";
                        statement = connection.prepareStatement(query);
                        resultSet = statement.executeQuery();
                        // Print results.
                        while(resultSet.next()) {
                            // Quarter

                            System.out.print(" ID: " + resultSet.getString(1));
                            System.out.print("; TEL: " + resultSet.getString(2));
                            System.out.print("; DIRECCION: " + resultSet.getString(3));
                            System.out.print("; DNI: " + resultSet.getString(5));
                            System.out.print("; NOMBRE: " + resultSet.getString(7));
                            System.out.print("; APELLIDO: " + resultSet.getString(6));
                            System.out.print("\n   ");
                            //System.out.print("\n   ");
                        }

                        System.out.println("ELIJE UN ID DE PERSONA PARA ASIGNAR UN RECLAMO");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("INGRESE FECHA DE RECLAMO");
                        String fecha = scanner.nextLine();
                        
                        //TODO: solucionar problema de que en ddl le puse unique a id de usuario y este se tiene que poder repetir por que varios usuarios pueden hacer mas de un reclamo 
                        query = "INSERT INTO Reclamo(fecha_resol, id) VALUES (?, ?)";
                        try {
                            statement = connection.prepareStatement(query);
                            statement.setString(1, fecha);
                            statement.setInt(2, id);
                            int filas = statement.executeUpdate();
                            if (filas > 0) {
                                System.out.println("eclamo agregado");
                            } else {
                                System.out.println("no se pudo agregar el reclamo");
                            }
                        } catch (SQLException e) {
                            System.out.println("error al agregar reclamo: " + e.getMessage());
                        }



                        break;
                    case 3:
                        System.out.println("Ingrese una ID de reclamo a eliminar");
                        int idr = scanner.nextInt();
                        String delete = "Delete from reclamo where id = ?";

                       
                        try {
                             statement = connection.prepareStatement(delete);   
                             statement.setInt(1, idr);
                             int filas = statement.executeUpdate();
                            if (filas > 0){
                                System.out.println("Reclamo eliminado correctamente.");
                            }else{
                                System.out.println("No se encontró un reclamo con ese ID.");
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

/* b)
SELECT 
    Reclamo.nro AS nro_reclamo,
    Reclamo.fecha_resol AS fecha_resolucion,
    Materiales.codigo AS codigo_material,
    Materiales.descripcion AS material,
    Usa.cantidad
FROM Reclamo
LEFT JOIN Usa ON Reclamo.nro = Usa.nro
LEFT JOIN Materiales ON Usa.codigo = Materiales.codigo
ORDER BY Reclamo.nro;

 */

 /**
  c) 
  SELECT deriva.nro,
    COUNT(DISTINCT deriva.id) AS cantidad_empleados_asignados
FROM deriva
GROUP BY deriva.nro
HAVING COUNT(DISTINCT deriva.id) > 1;

  */