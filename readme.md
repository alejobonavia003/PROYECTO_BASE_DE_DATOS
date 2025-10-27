1) descargar el repositorio 
2) crear una nueva base de datos en pg con clic derecho en database 
3) abrir un sql query tol en la nueva base y cargal el dll y el dml 
4) cargar app.java la contraceña y el usuario personal de pg (configurado al instalar pg)
4) en localhost va el host cargado al creal la base de datos si es local siempre va localhost 
    seguido va el puestro ccargado al crear la base de datos 
    seguido de/ va el nombre de la base de datos 
    

5) una vez cargadas las credenciales probar y arreglar errores que tire 


    private static final String url = "jdbc:postgresql://localhost:5432/proyecto_reclamos";
    private static final String user = "postgres";
    private static final String password = "12345";