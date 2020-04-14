package edu.proygrado.ejb.fileSharing;

import java.util.ArrayList;
import java.util.List;
import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.ArchivoRestriccion;
import edu.proygrado.dto.CompartirArchivoInputDTO;
import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.dto.MoodleCoursesInfoDTO;
import edu.proygrado.dto.TipoDestinatario;
import edu.proygrado.ejb.ArchivosEJB;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.utils.MoodleFiles;

public class WSUserSharedFilesMgr implements ISharedFiles{

	@Override
	public String getMoodleSharedFileContents(String matefunToken, InvitadoEJB invitadoEJB, String filepath) throws Exception {
		return MoodleFiles.getMoodleFileContents(invitadoEJB, filepath, matefunToken, true);
	}
	
	@Override
	public List<ArchivoDTO> getAllFilesSharedToMe(String matefunToken, InvitadoEJB invitadoEJB, ArchivosEJB archivosEJB, String filepath) throws Exception {

		ArrayList<ArchivoRestriccion> restricciones = new ArrayList<ArchivoRestriccion>();
		
		MoodleCoursesInfoDTO info = invitadoEJB.getCoursesInfo(matefunToken);
		restricciones.add(new ArchivoRestriccion(TipoDestinatario.USUARIO, info.getId() ));
		
		for (MoodleCourseDTO course : info.getEnrolledcourses()) {
			restricciones.add(new ArchivoRestriccion(TipoDestinatario.CURSO, course.getId() ));
			if ( course.getGrupos() != null) {
				for (GrupoDTO grupo: course.getGrupos()) {
					restricciones.add(new ArchivoRestriccion(TipoDestinatario.GRUPO, course.getId(), grupo.getGrupoId() ));
				}
			}else{
				System.out.println(" curso: " + course.getId() + " , " + course.getFullname() + " : grupos es null");
			}
		}

		return archivosEJB.getAllMoodlePrivateFiles(matefunToken, invitadoEJB, filepath, restricciones, true);
	}

	@Override
	public List<ArchivoDTO> createUpdateSharedFile(String matefunToken, InvitadoEJB invitadoEJB,ArchivosEJB archivosEJB, CompartirArchivoInputDTO dataShareFile) throws Exception {
		
		String newFileName;
		List<ArchivoDTO> ret = new ArrayList<ArchivoDTO>();
		
		switch (dataShareFile.getTipoDestinatario()) {

			case "curso" :
				//[o66dcur39]C.mf
				newFileName = String.format("[o%ddcur%d]%s", invitadoEJB.getUsuario(matefunToken).getMoodleUserId() , dataShareFile.getCursoDestinatario() , dataShareFile.getArchivo().getNombre());
				System.out.println("curso: " + newFileName);
				ArchivoDTO nuevo = dataShareFile.getArchivo();
				nuevo.setNombre(newFileName);
				ret.add(archivosEJB.crearArchivoPrivadoMoodle(nuevo, matefunToken, invitadoEJB, "/", true));
			break;

			case "grupo" :
				//[o66dcur39grp7]F.mf
				for (Long grupoId: dataShareFile.getGruposDestinatarios()) {
					newFileName = String.format("[o%ddcur%dgrp%d]%s", invitadoEJB.getUsuario(matefunToken).getMoodleUserId(), dataShareFile.getCursoDestinatario(), grupoId, dataShareFile.getArchivo().getNombre());
					System.out.println("curso: " + newFileName);
					ArchivoDTO nuevoA = dataShareFile.getArchivo();
					nuevoA.setNombre(newFileName);
					ret.add(archivosEJB.crearArchivoPrivadoMoodle(nuevoA, matefunToken, invitadoEJB, "/", true));
				}
			break;

			case "usuario" :
				//[o66dusr8]B.mf
				for (Long userId: dataShareFile.getUsuariosDestinatarios()) {
					newFileName = String.format("[o%ddusr%d]%s", invitadoEJB.getUsuario(matefunToken).getMoodleUserId(), userId, dataShareFile.getArchivo().getNombre());
					System.out.println("usuario: " + newFileName);
					ArchivoDTO nuevoA = dataShareFile.getArchivo();
					nuevoA.setNombre(newFileName);
					ret.add(archivosEJB.crearArchivoPrivadoMoodle(nuevoA, matefunToken, invitadoEJB, "/", true));
				}

			break;
		}

		return ret;
	}

}
