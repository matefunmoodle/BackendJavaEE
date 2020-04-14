/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.modelo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 *
 * @author gonzalo
 */
@Entity
public class Alumno extends Usuario{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@OneToMany
	@JoinTable(name = "ARCHIVOS_ALUMNO")
    List<Archivo> archivos;
	@OneToMany
	@JoinTable(name = "ARCHIVOS_COMPARTIDOS_ALUMNO")
    List<Archivo> archivosCompartidos;

    public Alumno() {
    }

    public Alumno(String cedula, Long moodleUserId, String nombre, String apellido, Liceo liceo, Configuracion configuracion) {
        super(cedula, moodleUserId, nombre, apellido, liceo,  configuracion);
        this.archivos = new ArrayList<>();
        this.archivosCompartidos = new ArrayList<>();
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }
    
    public void addArchivo(Archivo archivo){
    	this.archivos.add(archivo);
    }

	public List<Archivo> getArchivosCompartidos() {
		return archivosCompartidos;
	}

	public void setArchivosCompartidos(List<Archivo> archivosCompartidos) {
		this.archivosCompartidos = archivosCompartidos;
	}
    
    public void addArchivoCompartido(Archivo archivo){
    	this.archivosCompartidos.add(archivo);
    }

	@Override
	public String getTipo() {
		return "alumno";
	}
    
}
