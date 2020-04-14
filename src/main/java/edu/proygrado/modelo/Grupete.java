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
public class Grupete implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EmbeddedId
    private GrupetePK grupetePK;
	
    @ManyToMany
    @JoinTable(name="ARCHIVOS_COMPARTIDOS_GRUPO")
    private List<Archivo> archivos;
    @ManyToMany
    @JoinTable(name="ALUMNOS_GRUPO")
    private List<Alumno> alumnos;
    
    public Grupete() {
		super();
	}
	
    public Grupete(GrupetePK grupoPK) {
		super();
		this.grupetePK = grupoPK;
		this.archivos = new ArrayList<>();
		this.alumnos = new ArrayList<>();
	}
	
    public GrupetePK getGrupoPK() {
		return grupetePK;
	}
	public void setGrupoPK(GrupetePK grupetePK) {
		this.grupetePK = grupetePK;
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
		result = prime * result + ((grupetePK == null) ? 0 : grupetePK.hashCode());
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
		Grupete other = (Grupete) obj;
		if (grupetePK == null) {
			if (other.grupetePK != null)
				return false;
		} else if (!grupetePK.equals(other.grupetePK))
			return false;
		return true;
	}
    
}
