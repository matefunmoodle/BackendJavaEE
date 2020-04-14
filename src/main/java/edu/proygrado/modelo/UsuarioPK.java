package edu.proygrado.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class UsuarioPK implements Serializable{

	private static final long serialVersionUID = 1L;

	String cedula;
	Liceo liceo;
	
	public UsuarioPK() {
		super();
	}
	
	public UsuarioPK(String cedula, Liceo liceo) {
		super();
		this.cedula = cedula;
		this.liceo = liceo;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public Liceo getLiceo() {
		return liceo;
	}

	public void setLiceo(Liceo liceo) {
		this.liceo = liceo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cedula == null) ? 0 : cedula.hashCode());
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
		UsuarioPK other = (UsuarioPK) obj;
		if (cedula == null) {
			if (other.cedula != null)
				return false;
		} else if (!cedula.equals(other.cedula))
			return false;
		if (liceo == null) {
			if (other.liceo != null)
				return false;
		} else if (!liceo.equals(other.liceo))
			return false;
		return true;
	}
}
