package edu.proygrado.utils;

import java.util.List;
import javax.ws.rs.client.ClientBuilder;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.matefun.MatefunException;

public class MoodleFiles {

	public static final Long rootFileId = 0l;
	
	public static String getMoodleFileContents(InvitadoEJB invitadoEJB, String fileurl, String matefunToken, boolean...forceMatefunWebServicesUser) throws MatefunException {
		
		List<StringPair> allTokenPairs = invitadoEJB.getAllMoodleTokens(matefunToken);
		
		boolean _forceMatefunWebServicesUser = forceMatefunWebServicesUser!=null && forceMatefunWebServicesUser.length>0 && forceMatefunWebServicesUser[0];
		if (_forceMatefunWebServicesUser)
			allTokenPairs = allTokenPairs.subList(allTokenPairs.size()-1, allTokenPairs.size());
		
		for (StringPair tokenPair : allTokenPairs) {
			String fileContents = getFileContentsUsingToken(fileurl, tokenPair);
			if (fileContents != null)
				return fileContents;
		}
		throw new MatefunException("No se puede obtener contenido de archivo '" + fileurl + "'");
	}
	
	private static String getFileContentsUsingToken(String fileurl, StringPair tokenPair) {
		javax.ws.rs.client.Client c = ClientBuilder.newBuilder().build();
		try {
			return c.target(fileurl + "?token=" + tokenPair.getValue()).request().get(String.class);
		}catch(Exception e) {
			System.out.println("No se puede obtener contenido de archivo '" + fileurl + "' usando token: " + tokenPair.getKey());
			return null;
		}finally {
			c.close();
		}
	}
}
