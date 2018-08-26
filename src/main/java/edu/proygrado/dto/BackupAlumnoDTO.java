package edu.proygrado.dto;

import java.util.ArrayList;
import java.util.List;

import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Grupo;

public class BackupAlumnoDTO extends BackupUsuarioDTO{
	
	List<ArchivoDTO> archivos;
	List<ArchivoDTO> archivosCompartidos;

	List<GrupoDTO> grupos;

	public BackupAlumnoDTO() {
	}

	public BackupAlumnoDTO(Alumno alumno, List<Grupo> grupos) {
		super(alumno);
		
		this.archivos = new ArrayList<>();

		for (Archivo archivo : alumno.getArchivos()) {
			this.archivos.add(new ArchivoDTO(archivo));
		}
		this.archivosCompartidos = new ArrayList<>();

		for (Archivo archivo : alumno.getArchivosCompartidos()) {
			this.archivosCompartidos.add(new ArchivoDTO(archivo));
		}

		this.grupos = new ArrayList<>();
		for (Grupo grupo : grupos) {
			GrupoDTO grupoDTO = new GrupoDTO();
			grupoDTO.setAnio(grupo.getGrupoPK().getAnio());
			grupoDTO.setGrado(grupo.getGrupoPK().getGrado());
			grupoDTO.setGrupo(grupo.getGrupoPK().getGrupo());
			grupoDTO.setLiceoId(grupo.getGrupoPK().getLiceo().getLiceoId());
			this.grupos.add(grupoDTO);
		}
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

	public List<GrupoDTO> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoDTO> grupos) {
		this.grupos = grupos;
	}
	
}
