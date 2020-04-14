package edu.proygrado.dto;

public class ArchivoRestriccion {
	
	TipoDestinatario tipoDestinatario;
	long valor1;
	long valor2;
	
	public ArchivoRestriccion() {
		super();
	}
	
	public ArchivoRestriccion(TipoDestinatario tipoDestinatario, long valor) {
		super();
		this.tipoDestinatario = tipoDestinatario;
		this.valor1 = valor;
	}
	
	public ArchivoRestriccion(TipoDestinatario tipoDestinatario, long valor1, long valor2) {
		super();
		this.tipoDestinatario = tipoDestinatario;
		this.valor1 = valor1;
		this.valor2 = valor2;
	}

	public TipoDestinatario getTipoDestinatario() {
		return tipoDestinatario;
	}

	public long getValor() {
		return valor1;
	}
	
	public long getValor1() {
		return valor1;
	}

	public long getValor2() {
		return valor2;
	}

}
