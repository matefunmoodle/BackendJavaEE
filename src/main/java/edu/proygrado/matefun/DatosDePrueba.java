/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.matefun;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Grupo;
import edu.proygrado.modelo.GrupoPK;
import edu.proygrado.modelo.Liceo;

/**
 *
 * @author gonzalo
 */
public class DatosDePrueba {

	/**
	 * @param args
	 *            the command line arguments
	 */
	static EntityManagerFactory emf = Persistence.createEntityManagerFactory("MatefunPULocal");

	public static void main(String[] args) {
		EntityManager em = emf.createEntityManager();
		try {
			
			/*
			em.getTransaction().begin();

			Liceo varela = new Liceo("Colegio Varela");
			em.persist(varela);

			GrupoPK segundoAPK = new GrupoPK(2017, 2, "Año", varela.getLiceoPK());
			Grupo segundoAnio = new Grupo(segundoAPK);
			em.persist(segundoAnio);

			Alumno a1 = new Alumno("1234", "Juan", "Perez", generateHash("pass"));
			em.persist(a1);
			Archivo root_a1 = new Archivo("root", new Date(), null, EstadoArchivo.Edicion, true, true, null, a1);
			a1.addArchivo(root_a1);
			em.persist(root_a1);
			
			Alumno invitado = new Alumno("invitado", "","Invitado", generateHash("invitado"));
			em.persist(invitado);
			Archivo root_invitado = new Archivo("root", new Date(), null, EstadoArchivo.Edicion, true, true, null, invitado);
			invitado.addArchivo(root_invitado);
			em.persist(root_invitado);
			
			segundoAnio.addAlumno(a1);
			segundoAnio.addAlumno(invitado);

			em.getTransaction().commit();
	
			*/
		} catch (Exception e) {
			em.getTransaction().rollback();
		} finally {
			em.close();
		}
	}

	public static String generateHash(String input) {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; idx++) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			// handle error here.
		}

		return hash.toString();
	}

}
