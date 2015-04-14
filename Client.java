import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	//URL of the chatServer
	final public String HOME="http://localhost:8456/ChatApplication/ChatServer";
	
	//To register
	public String SendName(String name) {
		
		String output=null;
		try {
		Client client = Client.create();
		WebResource webResource = client.resource(HOME+"/Register");
		ClientResponse response = webResource.type("text/plain").put(ClientResponse.class, name);
		if (response.getStatus() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		output = response.getEntity(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	//To get the list of users
	public String ListUsers(){
		String output=null;
		try {
			URL oracle = new URL(HOME+"/GetUsers");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			output = in.readLine();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	//To verify that the user is ready to chat.
	public String GetToName(String name){
		String output = null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(HOME+"/Verify");
			ClientResponse response = webResource.type("text/plain").put(ClientResponse.class, name);
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
			}
			output = response.getEntity(String.class);
			}catch (Exception e) {
				e.printStackTrace();
			}
		return output;
	}
	
	//To send message
	public String SendMessage(String from,String to,String message){
		String output=null;
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(HOME+"/SendMessages");
			JSONObject obj=new JSONObject();
			obj.put("to",to);
			obj.put("from",from);
			obj.put("message",message);
			String input=obj.toString();
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
			output = response.getEntity(String.class);
			} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	//To recieve message.
	public String GetMessage(String name){
		String output = null;
			try {
				Client client = Client.create();
				do{
				WebResource webResource = client.resource(HOME+"/GetMessage");
				ClientResponse response = webResource.type("text/plain").put(ClientResponse.class, name);
				if (response.getStatus() != 201) {
					System.out.println(response.getStatus());
				}
				output = response.getEntity(String.class);
				}while(output.equals("failure") || output.equals(null+": "+null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			return output;
	}
	
	//To quit chatapp
	public void LogOffChat(String name){
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(HOME+"/del/"+name);
			webResource.delete();
		} catch (Exception e) {
			e.printStackTrace();
			}
		}
	
	
	//to chat to other client
	public void LogOff(String name){
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(HOME+"/"+name);
			webResource.delete();
		} catch (Exception e) {
			e.printStackTrace();
			}
		}
}
