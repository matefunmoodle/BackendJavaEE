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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author gonzalo
 */
@Entity
public class Docente extends Usuario {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManyToMany
	@JoinTable(name="DOCENTES_GRUPO")
	List<Grupo> gruposAsignados;
	@OneToMany
	@JoinTable(name = "ARCHIVOS_DOCENTE")
	List<Archivo> archivos;

	public Docente() {
	}

	public Docente(String cedula, Long moodleUserId, String nombre, String apellido, String roleId, String courseId, Liceo liceo, Configuracion configuracion) {
		super(cedula, moodleUserId, nombre, apellido, liceo,  configuracion);
		this.gruposAsignados = new ArrayList<>();
		this.archivos = new ArrayList<>();
	}

	public List<Grupo> getGruposAsignados() {
		return gruposAsignados;
	}

	public void setGruposAsignados(List<Grupo> gruposAsignados) {
		this.gruposAsignados = gruposAsignados;
	}
	
	public void addGrupoAsignado(Grupo grupo){
		this.gruposAsignados.add(grupo);
	}

	public List<Archivo> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<Archivo> archivos) {
		this.archivos = archivos;
	}

	public void addArchivo(Archivo archivo) {
		this.archivos.add(archivo);
	}

	@Override
	public String getTipo() {
		return "docente";
	}
}
