package edu.proygrado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;

public class AlumnoDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	Long moodleUserId;
	String cedula;
	String nombre;
	String apellido;
	List<ArchivoDTO> archivos;
	List<ArchivoDTO> archivosCompartidos;
	
	public AlumnoDTO(){}
	
	public AlumnoDTO(JsonObject user, List<ArchivoDTO> archivos, List<ArchivoDTO> archivosCompartidos) {
		super();
		
		this.moodleUserId = new Long(user.getInt("id"));
		this.cedula = null;
		this.nombre = user.containsKey("firstname") ? user.getString("firstname") : (user.containsKey("fullname") ? user.getString("fullname").split(" ")[0] : "NO-FIRSTNAME");
		this.apellido = user.containsKey("lastname") ? user.getString("lastname") : (user.containsKey("fullname") ? user.getString("fullname").split(" ")[1] : "NO-LASTNAME");
		this.archivos = archivos;
		this.archivosCompartidos = archivosCompartidos;
	}

	public AlumnoDTO(Alumno alumno){
		this.cedula = alumno.getCedula();
		this.moodleUserId = alumno.getMoodleUserId();
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

	public Long getMoodleUserId() {
		return moodleUserId;
	}

	public void setMoodleUserId(Long moodleUserId) {
		this.moodleUserId = moodleUserId;
	}
	
}
