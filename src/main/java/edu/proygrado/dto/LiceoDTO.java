package edu.proygrado.dto;

import java.util.List;
import edu.proygrado.modelo.Liceo;

public class LiceoDTO {

	private long liceoid;
	private String nombre;
	private String moodleapiusername;
	private String moodleuri;	
	private String servicename;
	private String servicetoken;

	public LiceoDTO() {
		super();
	}
	
	public LiceoDTO(long liceoid, String nombre, String moodleuri, String servicename, String moodleapiusername, String servicetoken, List<String> adminnames) {
		super();
		this.liceoid = liceoid;
		this.nombre = nombre;
		this.moodleuri = moodleuri;
		this.servicename = servicename;
		this.moodleapiusername = moodleapiusername;
		this.servicetoken = servicetoken;
	}

	public LiceoDTO(Liceo lic) {
		super();
		this.liceoid = lic.getLiceoPK().getLiceoId();
		this.nombre = lic.getNombre();
		this.moodleuri = lic.getMoodleuri();
		this.moodleapiusername = lic.getMoodleapiusername();
		this.servicename = lic.getMoodlewsservice();
		this.servicetoken = lic.getMoodleapiusertoken();
	}
	
	public long getLiceoid() {
		return liceoid;
	}

	public void setLiceoid(long liceoid) {
		this.liceoid = liceoid;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMoodleuri() {
		return moodleuri;
	}

	public void setMoodleuri(String moodleuri) {
		this.moodleuri = moodleuri;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getServicetoken() {
		return servicetoken;
	}

	public void setServicetoken(String servicetoken) {
		this.servicetoken = servicetoken;
	}

	public String getMoodleapiusername() {
		return moodleapiusername;
	}

	public void setMoodleapiusername(String moodleapiusername) {
		this.moodleapiusername = moodleapiusername;
	}

}
