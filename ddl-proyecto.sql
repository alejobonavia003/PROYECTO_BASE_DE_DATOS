CREATE TABLE Usuario(
 id serial UNIQUE NOT NULL,
 tel varchar(50),
 direccion varchar(50),
PRIMARY KEY (id));

CREATE TABLE Reclamo(
 nro serial UNIQUE NOT NULL,
 fecha_resol varchar(50),
 id serial NOT NULL,
FOREIGN KEY (id) REFERENCES Usuario (id) ON DELETE CASCADE,
PRIMARY KEY (nro));

CREATE TABLE Empresa(
 id serial UNIQUE NOT NULL,
 nro_cuit varchar(50),
 cap_kw integer,
 CONSTRAINT capacidad CHECK (cap_kw >= 0 AND cap_kw <= 50000),
FOREIGN KEY (id) REFERENCES Usuario (id),
PRIMARY KEY (id));

CREATE TABLE Persona(
 id serial NOT NULL,
 dni integer UNIQUE,
 apellido varchar(50),
 nombre varchar(50),
 CONSTRAINT dniper CHECK (dni > 0 AND dni < 1000000000), 
FOREIGN KEY (id) REFERENCES Usuario (id),
PRIMARY KEY (id));

CREATE TABLE Empleado(
 id serial UNIQUE NOT NULL,
 sueldo varchar(50),
FOREIGN KEY (id) REFERENCES Usuario (id),
PRIMARY KEY (id));

CREATE TABLE deriva(
 nro serial NOT NULL,
 id serial NOT NULL,
FOREIGN KEY (nro) REFERENCES Reclamo (nro),
FOREIGN KEY (id) REFERENCES Usuario (id),
PRIMARY KEY (nro,id));

CREATE TABLE Motivo(
 codigo serial UNIQUE NOT NULL,
 descripcion varchar(50),
PRIMARY KEY (codigo));

CREATE TABLE Materiales(
 codigo serial UNIQUE NOT NULL,
 descripcion varchar(50),
PRIMARY KEY (codigo));

CREATE TABLE Llamado(
 numero serial UNIQUE NOT NULL,
 nro serial UNIQUE NOT NULL,
FOREIGN KEY (nro) REFERENCES Reclamo (nro) ON DELETE CASCADE,
PRIMARY KEY (numero,nro));

CREATE TABLE Usa(
 codigo serial NOT NULL,
 nro serial NOT NULL,
 cantidad integer NOT NULL,
FOREIGN KEY (codigo) REFERENCES Materiales (codigo),
FOREIGN KEY (nro) REFERENCES Reclamo (nro) ON DELETE CASCADE,
PRIMARY KEY (codigo,nro));


CREATE TABLE auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    nro_reclamo_eliminado INTEGER NOT NULL,
    fecha_eliminacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER,
    fecha_resol_reclamo VARCHAR(50),
    eliminado_por VARCHAR(100) -- <--- nuevo campo para el usuario de PostgreSQL
);

CREATE OR REPLACE FUNCTION funcion_auditoria()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO auditoria(nro_reclamo_eliminado, usuario_id, fecha_resol_reclamo, eliminado_por)
    VALUES (OLD.nro, OLD.id, OLD.fecha_resol, current_user);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_auditoria
AFTER DELETE ON reclamo
FOR EACH ROW
EXECUTE FUNCTION funcion_auditoria();

