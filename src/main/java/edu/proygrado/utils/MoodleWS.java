package edu.proygrado.utils;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import edu.proygrado.ejb.InvitadoEJB;
import edu.proygrado.matefun.MatefunException;

public class MoodleWS {
	
	public static final String IS_OK = "isOk";
	public static final String VALUE_TYPE_KEY = "valueType";
	public static final String RESULT_KEY = "result";
	public static final String REQUEST_KEY = "request";
	
	public static final String POST = "POST";
	public static final String GET = "GET";
	
	public static final String WSTOKEN_KEY = "wstoken";
	public static final String WSFUNCTION_KEY = "wsfunction";
	public static final String WSRESTFORMAT_KEY = "moodlewsrestformat";

	private static boolean objectHasWarnings(JsonObject dataobj) {
		return 	dataobj.containsKey("warnings") &&
				dataobj.get("warnings").getValueType().equals(ValueType.ARRAY) &&
				!dataobj.getJsonArray("warnings").isEmpty();
	}
	
	private static boolean isOk(JsonStructure data, String functionName) {
		if (data!=null && data instanceof JsonObject) {
			JsonObject dataobj = (JsonObject)data;
			if ( dataobj.containsKey("exception") && dataobj.get("exception").getValueType().equals(ValueType.STRING) && !dataobj.getString("exception").isEmpty() )
				return false;
			
			if (functionName.equals(MoodleFunctions.enrol_manual_enrol_users) || functionName.equals(MoodleFunctions.core_group_get_course_user_groups)) {
				if (objectHasWarnings(dataobj))
					return false;
			}
				
		}else if (data!=null && data instanceof JsonArray) {
			JsonArray dataarr = (JsonArray)data;

			if (functionName.equals(MoodleFunctions.mod_assign_save_submission) &&
				dataarr.size() > 0 && dataarr.getJsonObject(0).getString("warningcode").equals("couldnotsavesubmission"))
				return false;			
		}
		return true;
	}
	
	private static String getType(JsonStructure data) {
		if (data==null)
			return JsonValue.NULL.toString();
		if (data!=null && data instanceof JsonObject)
			return JsonValue.ValueType.OBJECT.toString();
		return JsonValue.ValueType.ARRAY.toString();
	}
	
	private static JsonObject getRequest(String functionName, Map<String, Object> params) throws MatefunException {

		JsonObjectBuilder req = Json.createObjectBuilder();
		req = req.add(WSFUNCTION_KEY, functionName);
		req = req.add(WSTOKEN_KEY, (String)params.get(WSTOKEN_KEY));
		
		for (Map.Entry<String,Object> param : params.entrySet()) {
			if (param.getValue() instanceof String) 
				req = req.add(param.getKey(), (String)param.getValue());
			else if ( param.getValue() instanceof Boolean ) 
				req = req.add(param.getKey(), (Boolean)param.getValue());
			else if ( param.getValue() instanceof Long ) 
				req = req.add(param.getKey(), (Long)param.getValue());
			else if ( param.getValue() instanceof Integer ) 
				req = req.add(param.getKey(), (Integer)param.getValue());
			else
				throw new MatefunException ("No datatype handled: " + param.getValue().getClass().toGenericString() );
		}
		
		return req.build();
	}
	
	private static JsonObject createWSResponse (String functionName, String stringResponse, Map<String, Object> params) throws Exception {

		Map<String, Object> config = new HashMap<String, Object>();
		config.put("javax.json.stream.JsonGenerator.prettyPrinting", Boolean.valueOf(true));
		JsonStructure data = (stringResponse != null && !stringResponse.equalsIgnoreCase("null")) ?
							Json.createReader(new StringReader(stringResponse)).read() :
							null;
							
		boolean isOk = isOk(data, functionName);
		
		JsonObjectBuilder result = null;
		if (stringResponse != null && !stringResponse.isEmpty() ) {
			 result = Json.createBuilderFactory(config).createObjectBuilder()
					 .add(IS_OK, isOk )
					 .add(VALUE_TYPE_KEY, getType(data) )
				     .add(RESULT_KEY, data==null ? JsonValue.NULL : data);
		}
		
		if (!isOk)
			result = result.add(REQUEST_KEY, getRequest(functionName, params));
		return result.build();
	}
	
	private static WebTarget loadParams(WebTarget target , Map<String, Object> params) throws MatefunException {
		for (Map.Entry<String,Object> param : params.entrySet()) {
			if (param.getValue() instanceof String) 
				target = target.queryParam(param.getKey(), (String)param.getValue() );
			else if ( param.getValue() instanceof Boolean ) 
				target = target.queryParam(param.getKey(), (Boolean)param.getValue() );
			else if ( param.getValue() instanceof Long ) 
				target = target.queryParam(param.getKey(), (Long)param.getValue() );
			else if ( param.getValue() instanceof Integer ) 
				target = target.queryParam(param.getKey(), (Integer)param.getValue() );
			else {
				System.out.println("param: " + param);
				System.out.println("param.getKey(): " + param.getKey());
				System.out.println("param.getValue(): " + param.getValue());
				throw new MatefunException ("No datatype handled: " + param.getValue().getClass().toGenericString() );
			}
		}
		return target;
	}
	
	public static JsonObject POST(StringPair moodleTokenPair, String moodleApiEndpoint, String functionName, Map<String, Object> params) throws Exception {
		return MAKE_REQUEST(POST, moodleApiEndpoint, functionName, params, Arrays.asList(moodleTokenPair));
	}
	
	
	public static JsonObject POST(InvitadoEJB invitadoEJB, String matefunToken, String functionName, Map<String, Object> params, boolean... forceSuperuser) throws Exception {
		List<StringPair> moodleTokenPairs = invitadoEJB.getAllMoodleTokens(matefunToken);
		if (forceSuperuser!=null && forceSuperuser.length>0 && forceSuperuser[0])
			moodleTokenPairs = moodleTokenPairs.subList(moodleTokenPairs.size()-1, moodleTokenPairs.size());
		return MAKE_REQUEST(POST, invitadoEJB.getMoodleApiEndpoint(matefunToken), functionName, params, moodleTokenPairs);
	}

	public static JsonObject GET(StringPair moodleTokenPair, String moodleApiEndpoint, String functionName, Map<String, Object> params) throws Exception {
		return MAKE_REQUEST(GET, moodleApiEndpoint, functionName, params, Arrays.asList(moodleTokenPair));
	}
	
	public static JsonObject GET(InvitadoEJB invitadoEJB, String matefunToken, String functionName, Map<String, Object> params, boolean... forceSuperuser) throws Exception {
		List<StringPair> moodleTokenPairs = invitadoEJB.getAllMoodleTokens(matefunToken);
		if (forceSuperuser!=null && forceSuperuser.length>0 && forceSuperuser[0])
			moodleTokenPairs = moodleTokenPairs.subList(moodleTokenPairs.size()-1, moodleTokenPairs.size());
		return MAKE_REQUEST(GET, invitadoEJB.getMoodleApiEndpoint(matefunToken), functionName, params, moodleTokenPairs);
	}
	
	private static boolean isSitePolicyNotAgreed(JsonObject result) {
		return result.getJsonObject("result").getString("errorcode").equals("sitepolicynotagreed");
	}
	
	private static JsonObject MAKE_REQUEST(String verb, String moodleApiEndpoint, String functionName, Map<String, Object> params, List<StringPair> moodleTokenPairs) throws Exception {
		
		for (StringPair tokenPair : moodleTokenPairs) {
			try {
				JsonObject result = setTokenAndPerformRequest(verb, functionName, moodleApiEndpoint, new HashMap<String,Object>(params), tokenPair);
				if (result.getBoolean(IS_OK))
					return result;
				
				else if (isSitePolicyNotAgreed(result)) {
					System.out.println("  ERROR["+verb+" "+functionName+"][token: "+tokenPair.getKey()+"] -> " + result);
					try_agree_site_policy(moodleApiEndpoint, tokenPair.getValue());
					JsonObject resultAgain = setTokenAndPerformRequest(verb, functionName, moodleApiEndpoint, new HashMap<String,Object>(params), tokenPair);
					if (resultAgain.getBoolean(IS_OK))
						return resultAgain;
					else
						System.out.println("  ERROR["+verb+" "+functionName+"][token: "+tokenPair.getKey()+"] -> " + result);
					
				}else {
					System.out.println("  ERROR["+verb+" "+functionName+"][token: "+tokenPair.getKey()+"] -> " + result);
				}
			}catch(Exception e) {
				System.out.println("EXCEPTION["+verb+" "+functionName+"][token: "+tokenPair.getKey()+"] -> " + e);
				e.printStackTrace();
			}
		}
		throw new MatefunException("No se puede realizar operacion con ninguno de los "+moodleTokenPairs.size()+" tokens: " + verb + " "+ functionName);		
	}
	
	private static void try_agree_site_policy(String moodleApiEndpoint, String token) {
		javax.ws.rs.client.Client c = ClientBuilder.newBuilder().build();
		try {
			c.target(moodleApiEndpoint + "/webservice/rest/server.php")
			.queryParam("wsfunction", MoodleFunctions.core_user_agree_site_policy)
			.queryParam("wstoken", token)
			.queryParam("moodlewsrestformat", "json").request()
			.get(String.class);
		}finally {
			c.close();
		}
	}
	
	private static JsonObject setTokenAndPerformRequest(String verb, String functionName, String moodleApiEndpoint, Map<String, Object> parameters, StringPair tokenPair) throws Exception {
		javax.ws.rs.client.Client c = ClientBuilder.newBuilder().build();
		try {
			parameters.put(WSTOKEN_KEY, tokenPair.getValue());
			parameters.put(WSFUNCTION_KEY, functionName);
			parameters.put(WSRESTFORMAT_KEY, "json");
			javax.ws.rs.client.Invocation.Builder b = loadParams(c.target(moodleApiEndpoint + "/webservice/rest/server.php"), parameters).request();
			String stringResp = verb.equals(GET) ? b.get(String.class) : b.post(Entity.json(""), String.class);
			//System.out.println("-->> FUNCTION: "+ parameters.get(WSFUNCTION_KEY) +", token:"+tokenPair.getKey()+", stringResp: " + stringResp);
			return createWSResponse (functionName, stringResp, parameters);
		} finally {
			c.close();
		}
	}
	
}
