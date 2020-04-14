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

import edu.proygrado.dto.LiceoDTO;

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
	private String moodleapiusername;
	private String moodleapiusertoken;
	private String nombre;
    private String moodleuri;
    private String moodlewsservice;

	public String getMoodlewsservice() {
		return moodlewsservice;
	}

	public void setMoodlewsservice(String moodlewsservice) {
		this.moodlewsservice = moodlewsservice;
	}

	public Liceo() { }    

	public Liceo(LiceoDTO liceo) {    
	    this.moodleapiusertoken = liceo.getServicetoken();
	    this.moodleapiusername = liceo.getMoodleapiusername();
		this.nombre = liceo.getNombre();
	    this.moodleuri = liceo.getMoodleuri();
	    this.moodlewsservice = liceo.getServicename();
	}
	
	public Liceo(String nombre, String moodleUri, String moodleapiusername, String moodleapiusertoken, String moodlewsservice) {
		super();
		this.liceoPK = new LiceoPK();
		this.moodleuri = moodleUri;
		this.nombre = nombre;
		this.moodleapiusername = moodleapiusername;
		this.moodlewsservice = moodlewsservice;
		this.moodleapiusertoken = moodleapiusertoken;
	}
    
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

	public String getMoodleuri() {
		return moodleuri;
	}

	public void setMoodleuri(String moodleuri) {
		this.moodleuri = moodleuri;
	}

	public String getMoodleapiusertoken() {
		return moodleapiusertoken;
	}

	public void setMoodleapiusertoken(String moodleapiusertoken) {
		this.moodleapiusertoken = moodleapiusertoken;
	}

	public String getMoodleapiusername() {
		return moodleapiusername;
	}

	public void setMoodleapiusername(String moodleapiusername) {
		this.moodleapiusername = moodleapiusername;
	}
	
}
