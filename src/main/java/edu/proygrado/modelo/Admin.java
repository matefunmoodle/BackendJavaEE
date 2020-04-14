package edu.proygrado.modelo;

import javax.persistence.Entity;

/**
 *
 * @author ramiro
 */
@Entity
public class Admin extends Usuario {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String password;

	public Admin() {
	}

	public Admin(String cedula, String nombre, String apellido,
			String password, Liceo liceo, Configuracion configuracion) {
		super(cedula, -1l, nombre, apellido, liceo, configuracion);
		this.password = password;
	}

	@Override
	public String getTipo() {
		return "admin";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void addArchivo (Archivo a) {
		throw new UnsupportedOperationException("No se puede crear un archivo para el administrador de matefun");
	}
	
}
