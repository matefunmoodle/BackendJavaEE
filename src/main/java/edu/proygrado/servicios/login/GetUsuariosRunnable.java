package edu.proygrado.servicios.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import javax.json.JsonArray;
import javax.json.JsonObject;
import edu.proygrado.dto.AllUsersResultDTO;
import edu.proygrado.dto.UserResultDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.utils.MoodleFunctions;
import edu.proygrado.utils.MoodleWS;
import edu.proygrado.utils.StringPair;

public class GetUsuariosRunnable implements Runnable{

	private String moodleApiEndpoint;
	private StringPair criteria;
	private StringPair moodleToken;
	public Semaphore sem;
	public List<UserResultDTO> output;
	
	public GetUsuariosRunnable(String moodleApiEndpoint, StringPair criteria, StringPair moodleToken) {
		this.moodleApiEndpoint = moodleApiEndpoint;
		this.criteria = criteria;
		this.moodleToken = moodleToken;
		this.sem = new Semaphore(0);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void run() {
		Map<String, Object> values = new HashMap<String, Object>() {{
	        put("criteria[0][key]", criteria.getKey() );
	        put("criteria[0][value]", criteria.getValue() );
	    }};
		try {
			
			JsonObject result = MoodleWS.GET(moodleToken, moodleApiEndpoint, MoodleFunctions.core_user_get_users, values);

			if (!result.getBoolean(MoodleWS.IS_OK)) {
				throw new MatefunException("No se puede obtener informacion de usuarios con core_user_get_users");
			}else {
				JsonArray users = result.getJsonObject("result").getJsonArray("users");
				AllUsersResultDTO userInfo = new AllUsersResultDTO();
				userInfo.setUsers(new ArrayList<UserResultDTO>());
			    for (int i = 0; i < users.size(); ++i)
			    	userInfo.getUsers().add(new UserResultDTO(users.getJsonObject(i)));
			    this.output = userInfo.getUsers();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.output = null;
		}finally {
		    this.sem.release();
		}
	}
}
