
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

	private static final long serialVersionUID = 1L;
	@Id
    String cedula;
	Long moodleUserId;
    String nombre;
    String apellido;
    
    
    @OneToOne(cascade = CascadeType.DETACH)
    Liceo liceo;
    
    @OneToOne(cascade = CascadeType.ALL)
    Configuracion configuracion;

    public Usuario() {
    }


    public Usuario(String cedula, Long moodleUserId, String nombre, String apellido, Liceo liceo, Configuracion configuracion) {
        this.cedula = cedula;
        this.moodleUserId = moodleUserId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.configuracion = configuracion;
        this.liceo = liceo;
    }
    
    public abstract String getTipo();
    
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

	public Configuracion getConfiguracion() {
		return configuracion;
	}

	public void setConfiguracion(Configuracion configuracion) {
		this.configuracion = configuracion;
	}

	public Long getMoodleUserId() {
		return moodleUserId;
	}


	public void setMoodleUserId(Long moodleUserId) {
		this.moodleUserId = moodleUserId;
	}


	public Liceo getLiceo() {
		return liceo;
	}


	public void setLiceo(Liceo liceo) {
		this.liceo = liceo;
	}
	
	public abstract void addArchivo (Archivo a);

}
