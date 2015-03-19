import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	
	final public String HOME="http://localhost:8456/CrunchifyRESTJerseyExample/chat";
	public static String name;
	public String message;
	public static String to;
	public static boolean exit=true;
	
	public static void main(String[] args) {
		RestClient r = new RestClient();
		r.SendName();
		r.ListUsers();
		try {
			String output;
			do{
			System.out.println("type wait to wait");
			System.out.println("type chat to chat");
			System.out.println("type exit to exit");
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		    output = stdIn.readLine();
			if(output.equals("wait")){
				System.out.println("waitng ......");
				do{
				System.out.println("continue or quit");
				output=stdIn.readLine();
				if(output.equals("continue")){
					r.GetMessage();
					r.SendMessage();
					exit=true;
				}
				else{
					r.LogOff();
					exit=false;
				}
				}while(exit);
			}
			else if(output.equals("chat")) {
				r.GetToName();
				do{
				System.out.println("continue or quit");
				output=stdIn.readLine();
				if(output.equals("continue")){
				r.SendMessage();
				r.GetMessage();
					}
				else{
					r.LogOff();
					exit=false;
				}
				}while(exit);
			} 
		}while(!output.equals("exit"));
			r.LogOffChat();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void SendName() {
		
		try {
		String output;
		Client client = Client.create();
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		do {
		WebResource webResource = client.resource(HOME+"/Register");
		System.out.println("enter Your Name");
		name = stdIn.readLine();
		ClientResponse response = webResource.type("text/plain").put(ClientResponse.class, name);
		if (response.getStatus() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
		}
		output = response.getEntity(String.class);
		System.out.println(output);
		} while (output.equals("please try again with different Name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void ListUsers(){
		try {
			System.out.println("The Users who are online are");
			URL oracle = new URL(HOME+"/Names");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			String inputLine;
			inputLine = in.readLine();
			System.out.println(inputLine);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void GetToName(){
		try {
			String output;
			Client client = Client.create();
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter username you want to chat to");
			do {
			WebResource webResource = client.resource(HOME+"/Verify");
			to = stdIn.readLine();
			ClientResponse response = webResource.type("text/plain").put(ClientResponse.class, to);
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
			}
			output = response.getEntity(String.class);
			if(!output.equals("")){
				System.out.println(output);
			}
			}while (output.equals("No user exist on that name please try again")||output.equals("the user is busy, try another user to chat"));
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	private void SendMessage(){
		try {
			Client client = Client.create();
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			WebResource webResource = client.resource(HOME+"/SendMessages");
			System.out.println("Enter the message you want to send to"+to);
			message=stdIn.readLine();
			JSONObject obj=new JSONObject();
			obj.put("to",to);
			obj.put("from",name);
			obj.put("message",message);
			String input=obj.toString();
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
			//System.out.println("Message deliverd..");
			String output = response.getEntity(String.class);
			System.out.println(output);
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void GetMessage(){
			try {
				String output;
				Client client = Client.create();
				System.out.println("waitng for reply");
				do{
				WebResource webResource = client.resource(HOME+"/GetMessage");
				ClientResponse response = webResource.type("text/plain").put(ClientResponse.class, name);
				if (response.getStatus() != 201) {
					System.out.println(response.getStatus());
				}
				output = response.getEntity(String.class);
				}while(output.equals("failure") || output.equals(null+": "+null));
				System.out.println(output);
				String namepass[] = output.split(":"); 
				to = namepass[0]; 
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	private void LogOffChat(){
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(HOME+"/del/"+name);
			webResource.delete();
		} catch (Exception e) {
			e.printStackTrace();
			}
		}
	
	private void LogOff(){
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(HOME+"/"+name);
			webResource.delete();
		} catch (Exception e) {
			e.printStackTrace();
			}
		}
}
