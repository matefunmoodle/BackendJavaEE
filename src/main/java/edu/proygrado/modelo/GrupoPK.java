/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author gonzalo
 */
@Embeddable
public class GrupoPK implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int anio;
    private int grado;
    private String grupo;
    private LiceoPK liceo;
	
	public GrupoPK() {
		super();
	}
	public GrupoPK(int anio, int grado, String grupo, LiceoPK liceo) {
		super();
		this.anio = anio;
		this.grado = grado;
		this.grupo = grupo;
		this.liceo = liceo;
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
	public LiceoPK getLiceo() {
		return liceo;
	}
	public void setLiceo(LiceoPK liceo) {
		this.liceo = liceo;
	}
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anio;
		result = prime * result + grado;
		result = prime * result + ((grupo == null) ? 0 : grupo.hashCode());
		result = prime * result + ((liceo == null) ? 0 : liceo.hashCode());
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
		GrupoPK other = (GrupoPK) obj;
		if (anio != other.anio)
			return false;
		if (grado != other.grado)
			return false;
		if (grupo == null) {
			if (other.grupo != null)
				return false;
		} else if (!grupo.equals(other.grupo))
			return false;
		if (liceo == null) {
			if (other.liceo != null)
				return false;
		} else if (!liceo.equals(other.liceo))
			return false;
		return true;
	}
	
    
}
