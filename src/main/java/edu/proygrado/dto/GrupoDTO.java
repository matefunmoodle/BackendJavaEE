package edu.proygrado.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Grupo;
import edu.proygrado.utils.Utils;

public class GrupoDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	int anio;
    int grado;
    int grupoId;
    String grupo;
    String nombreCurso;
    long liceoId;
    List<ArchivoDTO> archivos;
    List<AlumnoDTO> alumnos;
    List<UsuarioDTO> participantes;

    public GrupoDTO(int anio, int grado, String grupo, int grupoId, long liceoId, List<ArchivoDTO> archivos, List<AlumnoDTO> alumnos,
			List<UsuarioDTO> participantes, String... nombreCurso) {
		super();
		this.anio = anio;
		this.grado = grado;
		this.grupo = grupo;
		this.grupoId = grupoId;
		this.liceoId = liceoId;
		this.archivos = archivos;
		this.alumnos = alumnos;
		this.participantes = participantes;
		this.nombreCurso = (nombreCurso!=null && nombreCurso.length>0 && !Utils.isNullOrEmpty(nombreCurso[0])) ? nombreCurso[0] : "";
	}

    public GrupoDTO(Integer grupoId, String grupo){
    	this.grupoId = grupoId;
    	this.grupo = grupo;
    }
    
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

	public List<UsuarioDTO> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<UsuarioDTO> participantes) {
		this.participantes = participantes;
	}

	public int getGrupoId() {
		return grupoId;
	}

	public void setGrupoId(int grupoId) {
		this.grupoId = grupoId;
	}

	public String getNombreCurso() {
		return nombreCurso;
	}

	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}
	
	@Override
	public boolean equals(Object o) {
		return o!=null && o instanceof GrupoDTO && ((GrupoDTO)o).getGrupoId()==this.getGrupoId();
	}
	
}
