package edu.proygrado.modelo;

public enum EstadoArchivo {
	Edicion, //El alumno o el docente lo esta editando
	Entregado,//Alumno entrega el archivo para que el docente lo corrija
	Devuelto,//Docente marca como devuelto para que el alumno lo vuelva a corregir
	Corregido //El docente lo corrige y agrega una nota.	
}
