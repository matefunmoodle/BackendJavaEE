package edu.proygrado.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;

import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.modelo.Usuario;

public class Utils {
	
	public static final int DifferenceFromGMTSeconds = 3 * 60 * 60;
	
	
	public static boolean isValidAssignment(JsonObject currentAssignment, boolean groupAssignments) {
		Long now = Instant.now().getEpochSecond();
		return currentAssignment.getInt("teamsubmission") == boolToInt(groupAssignments) &&
				now >= currentAssignment.getInt("allowsubmissionsfromdate") &&
				now < currentAssignment.getInt("gradingduedate");
	}
	
	
	public static String getOrDefault(JsonObject obj , String key) {
		return obj.containsKey(key) ? obj.getString(key) : "no-"+key;
	}
	
	public static int boolToInt(boolean bool) {
		return bool ? 1 : 0;
	}
	
	public static JsonObject filterByParam(JsonArray array, String name, String value) {
		for (int i=0 ; i<array.size() ; i++) {
			if (array.get(i).getValueType() == ValueType.OBJECT) {
				if ( array.getJsonObject(i).getString(name).equals(value) )
					return array.getJsonObject(i);
			}
		}
		return null;
	}
	
	public static String getToken(HttpServletRequest httpServletRequest){
    	String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    	if(token!=null && token.contains("Bearer "))
    		return token.substring("Bearer ".length());
    	else
    		throw new NotAuthorizedException("Token not present");
    }
	
	public static boolean isNullOrEmpty(String str) {
		return str==null || str.isEmpty();
	}
	
	public static boolean isNullOrEmpty(List<?> lst) {
		return lst==null || lst.isEmpty();
	}
	
	
	public static String fromBase64(String encodedString) {
		return new String(Base64.getDecoder().decode(encodedString.getBytes()));
	}
	
	public static String toBase64(String content) {
		return Base64.getEncoder().withoutPadding().encodeToString(content.getBytes());
	}
	
	public static Long getIdByFilepath(String filepath, Long moodleUserID, Boolean compartido) {
		return new Long(String.format("%c%d%s", (compartido ? '1' : '0'), moodleUserID , filepath).hashCode());
	}
	
    public static boolean esInvitado(InvitadoEJB invitadoEJB , HttpServletRequest httpServletRequest){
    	Usuario usuario = invitadoEJB.getUsuario(Utils.getToken(httpServletRequest));
    	if(usuario!=null && usuario.getCedula().toLowerCase().equals("invitado")){
    		System.out.println("Es usuario invitado");
    		return true;
    	}
    	return false;
    }
    
	public static String generateHash(String input) throws NoSuchAlgorithmException {
		StringBuilder hash = new StringBuilder();

		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] hashedBytes = sha.digest(input.getBytes());
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		for (int idx = 0; idx < hashedBytes.length; idx++) {
			byte b = hashedBytes[idx];
			hash.append(digits[(b & 0xf0) >> 4]);
			hash.append(digits[b & 0x0f]);
		}

		return hash.toString();
	}
	
}
