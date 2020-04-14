package edu.proygrado.ejb.fileSharing;

import java.util.List;

import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.CompartirArchivoInputDTO;
import edu.proygrado.ejb.ArchivosEJB;
import edu.proygrado.ejb.InvitadoEJB;

public interface ISharedFiles {
	public List<ArchivoDTO> getAllFilesSharedToMe(String matefunToken, InvitadoEJB invitadoEJB, ArchivosEJB archivosEJB, String filepath) throws Exception;
	public String getMoodleSharedFileContents(String matefunToken, InvitadoEJB invitadoEJB, String filepath) throws Exception;
	public List<ArchivoDTO> createUpdateSharedFile(String matefunToken, InvitadoEJB invitadoEJB, ArchivosEJB archivosEJB, CompartirArchivoInputDTO dataShareFile) throws Exception;
}
