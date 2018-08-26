/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author gonzalo
 */
@Embeddable
public class LiceoPK implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	long liceoId;
	
	public LiceoPK() {
		super();
	}

	public LiceoPK(long liceoId) {
		super();
		this.liceoId = liceoId;
	}

	public long getLiceoId() {
		return liceoId;
	}

	public void setLiceoId(long liceoId) {
		this.liceoId = liceoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (liceoId ^ (liceoId >>> 32));
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
		LiceoPK other = (LiceoPK) obj;
		if (liceoId != other.liceoId)
			return false;
		return true;
	}
    
    
}
