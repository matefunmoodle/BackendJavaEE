/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 *
 * @author gonzalo
 */
@Entity
public class Grupo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EmbeddedId
    private GrupoPK grupoPK;
	
    @ManyToMany
    @JoinTable(name="ARCHIVOS_COMPARTIDOS_GRUPO")
    private List<Archivo> archivos;
    @ManyToMany
    @JoinTable(name="ALUMNOS_GRUPO")
    private List<Alumno> alumnos;
    
    public Grupo() {
		super();
	}
	
    public Grupo(GrupoPK grupoPK) {
		super();
		this.grupoPK = grupoPK;
		this.archivos = new ArrayList<>();
		this.alumnos = new ArrayList<>();
	}
	
    public GrupoPK getGrupoPK() {
		return grupoPK;
	}
	public void setGrupoPK(GrupoPK grupoPK) {
		this.grupoPK = grupoPK;
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
	
	public List<Alumno> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<Alumno> alumnos) {
		this.alumnos = alumnos;
	}
	
	public void addAlumno(Alumno alumno){
		this.alumnos.add(alumno);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grupoPK == null) ? 0 : grupoPK.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grupo other = (Grupo) obj;
		if (grupoPK == null) {
			if (other.grupoPK != null)
				return false;
		} else if (!grupoPK.equals(other.grupoPK))
			return false;
		return true;
	}
    
}
