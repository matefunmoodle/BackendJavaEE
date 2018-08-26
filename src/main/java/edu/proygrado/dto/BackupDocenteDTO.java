package edu.proygrado.dto;

import java.util.ArrayList;
import java.util.List;

import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.Grupo;

public class BackupDocenteDTO extends BackupUsuarioDTO{
	
	List<GrupoDTO> gruposAsignados;
	List<ArchivoDTO> archivos;
	
	public BackupDocenteDTO(){}
	
	public BackupDocenteDTO(Docente docente){
		super(docente);
		
		this.gruposAsignados = new ArrayList<>();
		for(Grupo grupo: docente.getGruposAsignados()){
			GrupoDTO grupoDTO = new GrupoDTO();
			grupoDTO.setAnio(grupo.getGrupoPK().getAnio());
			grupoDTO.setGrado(grupo.getGrupoPK().getGrado());
			grupoDTO.setGrupo(grupo.getGrupoPK().getGrupo());
			grupoDTO.setLiceoId(grupo.getGrupoPK().getLiceo().getLiceoId());
			this.gruposAsignados.add(grupoDTO);			
		}
		this.archivos = new ArrayList<>();
		for(Archivo archivo: docente.getArchivos()){
			this.archivos.add(new ArchivoDTO(archivo));
		}
	}

	public List<GrupoDTO> getGruposAsignados() {
		return gruposAsignados;
	}

	public void setGruposAsignados(List<GrupoDTO> gruposAsignados) {
		this.gruposAsignados = gruposAsignados;
	}

	public List<ArchivoDTO> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<ArchivoDTO> archivos) {
		this.archivos = archivos;
	}
	
}
