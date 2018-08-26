package edu.proygrado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;

public class AlumnoDTO implements Serializable{
	
	String cedula;
	String nombre;
	String apellido;
	List<ArchivoDTO> archivos;
	List<ArchivoDTO> archivosCompartidos;
	
	public AlumnoDTO(){}
	
	public AlumnoDTO(Alumno alumno){
		this.cedula = alumno.getCedula();
		this.nombre = alumno.getNombre();
		this.apellido = alumno.getApellido();
		this.archivos = new ArrayList<>();
		for(Archivo a: alumno.getArchivos()){
			this.archivos.add(new ArchivoDTO(a));
		}
		this.archivosCompartidos = new ArrayList<>();
		for(Archivo a: alumno.getArchivosCompartidos()){
			this.archivosCompartidos.add(new ArchivoDTO(a));
		}
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public List<ArchivoDTO> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<ArchivoDTO> archivos) {
		this.archivos = archivos;
	}

	public List<ArchivoDTO> getArchivosCompartidos() {
		return archivosCompartidos;
	}

	public void setArchivosCompartidos(List<ArchivoDTO> archivosCompartidos) {
		this.archivosCompartidos = archivosCompartidos;
	}
	
}
