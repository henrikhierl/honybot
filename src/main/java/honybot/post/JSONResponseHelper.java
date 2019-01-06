package honybot.post;

import org.json.simple.JSONObject;

public class JSONResponseHelper {

	
	@SuppressWarnings("unchecked")
	public static JSONObject createSuccess(String text){
		JSONObject obj = new JSONObject();
		obj.put("type", "success");
		obj.put("title", "Success!");
		obj.put("text", text);		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createError(String text){
		JSONObject obj = new JSONObject();
		obj.put("type", "danger");
		obj.put("title", "Error!");
		obj.put("text", text);		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createSuccess(String text, String data){
		JSONObject obj = new JSONObject();
		obj.put("type", "success");
		obj.put("title", "Success!");
		obj.put("text", text);
		obj.put("data", data);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createError(String text, String data){
		JSONObject obj = new JSONObject();
		obj.put("type", "danger");
		obj.put("title", "Error!");
		obj.put("text", text);
		obj.put("data", data);
		return obj;
	}
	
    @SuppressWarnings("unchecked")
    public static JSONObject createSuccess(String text, JSONObject data){
        JSONObject obj = new JSONObject();
        obj.put("type", "success");
        obj.put("title", "Success!");
        obj.put("text", text);
        obj.put("data", data);                
        return obj;
    }
}
