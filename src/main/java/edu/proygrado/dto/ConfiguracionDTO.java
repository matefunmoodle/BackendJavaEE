package edu.proygrado.dto;

import edu.proygrado.modelo.Configuracion;

public class ConfiguracionDTO {
	String themeEditor;
	int fontSizeEditor;
	boolean argumentoI;
	boolean argumentoF;
	
	public ConfiguracionDTO() {
	}

	public ConfiguracionDTO(Configuracion config){
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
