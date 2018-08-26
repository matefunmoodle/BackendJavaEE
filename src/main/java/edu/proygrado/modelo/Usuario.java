
package edu.proygrado.modelo;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author gonzalo
 */
@Entity
public abstract class Usuario implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    String cedula;
    String nombre;
    String apellido;
    String password;
    @OneToOne(cascade = CascadeType.ALL)    
    Configuracion configuracion;

    public Usuario() {
    }

    public Usuario(String cedula, String nombre, String apellido, String password, Configuracion configuracion) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.configuracion = configuracion;
    }
    
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public Configuracion getConfiguracion() {
		return configuracion;
	}

	public void setConfiguracion(Configuracion configuracion) {
		this.configuracion = configuracion;
	}

	
}
