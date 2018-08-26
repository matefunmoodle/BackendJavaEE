package edu.proygrado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Grupo;

public class GrupoDTO implements Serializable{
	
	int anio;
    int grado;
    String grupo;
    long liceoId;
    List<ArchivoDTO> archivos;
    List<AlumnoDTO> alumnos;
    
    public GrupoDTO(){}
    
    public GrupoDTO(Grupo grupo){
    	this.anio = grupo.getGrupoPK().getAnio();
    	this.grado = grupo.getGrupoPK().getGrado();
    	this.grupo = grupo.getGrupoPK().getGrupo();
    	this.liceoId = grupo.getGrupoPK().getLiceo().getLiceoId(); 
    	this.archivos = new ArrayList<>();
    	for(Archivo a:grupo.getArchivos()){
    		this.archivos.add(new ArchivoDTO(a));
    	}
    	this.alumnos = new ArrayList<>();
    	for(Alumno a:grupo.getAlumnos()){
    		AlumnoDTO alumno = new AlumnoDTO();
    		alumno.setNombre(a.getNombre());
    		alumno.setApellido(a.getApellido());
    		alumno.setCedula(a.getCedula());
    		List<ArchivoDTO> archivos = new ArrayList<>();
    		for(Archivo arch : a.getArchivosCompartidos()){
    			if(!arch.isDirectorio() && !arch.isEliminado() && (arch.getEstado() == EstadoArchivo.Entregado || arch.getEstado() == EstadoArchivo.Corregido)){
    				archivos.add(new ArchivoDTO(arch));
    			}
    		}
    		alumno.setArchivos(archivos);
    		this.alumnos.add(alumno);
    	}
    }
    
	public int getAnio() {
		return anio;
	}
	
	public void setAnio(int anio) {
		this.anio = anio;
	}
	
	public int getGrado() {
		return grado;
	}
	
	public void setGrado(int grado) {
		this.grado = grado;
	}
	
	public String getGrupo() {
		return grupo;
	}
	
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	public long getLiceoId() {
		return liceoId;
	}
	
	public void setLiceoId(long liceoId) {
		this.liceoId = liceoId;
	}

	public List<ArchivoDTO> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<ArchivoDTO> archivos) {
		this.archivos = archivos;
	}

	public List<AlumnoDTO> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<AlumnoDTO> alumnos) {
		this.alumnos = alumnos;
	}
	
}
