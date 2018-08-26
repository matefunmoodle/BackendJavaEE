/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.modelo;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *
 * @author gonzalo
 */
@Entity
public class Liceo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private LiceoPK liceoPK;
    
    private String nombre;

    public Liceo(){}    
    
	public Liceo(String nombre) {
		super();
		this.liceoPK = new LiceoPK();
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LiceoPK getLiceoPK() {
		return liceoPK;
	}

	public void setLiceoPK(LiceoPK liceoPK) {
		this.liceoPK = liceoPK;
	}
	
}
