package edu.proygrado.modelo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.proygrado.dto.ConfiguracionDTO;

@Entity
public class Configuracion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    long configuracionId;
	String themeEditor;
	int fontSizeEditor;
	boolean argumentoI;
	boolean argumentoF;
	
	public Configuracion(){}
	
	public Configuracion(ConfiguracionDTO config) {
		super();
		this.themeEditor = config.getThemeEditor();
		this.fontSizeEditor = config.getFontSizeEditor();
		this.argumentoI = config.isArgumentoI();
		this.argumentoF = config.isArgumentoF();
	}
	
	public String getThemeEditor() {
		return themeEditor;
	}
	public void setThemeEditor(String themeEditor) {
		this.themeEditor = themeEditor;
	}
	public int getFontSizeEditor() {
		return fontSizeEditor;
	}
	public void setFontSizeEditor(int fontSizeEditor) {
		this.fontSizeEditor = fontSizeEditor;
	}

	public boolean isArgumentoI() {
		return argumentoI;
	}

	public void setArgumentoI(boolean argumentoI) {
		this.argumentoI = argumentoI;
	}

	public boolean isArgumentoF() {
		return argumentoF;
	}

	public void setArgumentoF(boolean argumentoF) {
		this.argumentoF = argumentoF;
	}
	
	
}
