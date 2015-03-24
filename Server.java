import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/ChatServer")
public class CtoFService {
	
	//to store username.
	public static ArrayList<String> users=new ArrayList<String>();
	//to store messages.
	public static HashMap<String, String> mess=new HashMap<String, String>();
	public static Map<String, HashMap<String,String>> gens = new HashMap<String,HashMap<String,String>>();
	//to store log files int he server.
	public static ArrayList<String> logg=new ArrayList<String>();
	//to store the status of the clients.
	public static HashMap<String, Boolean> messs=new HashMap<String, Boolean>();
	public String to;
	public String from;
	public String message;
	
	// To Register clients to chat application
	@Path("/Register")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addUser(String name)  {
		if(!users.contains(name)){
			users.add(name);
			messs.put(name, true);
			logg.add("the user "+name+" has been added");
			return Response.status(201).entity("success").build();
		}
		else{
			return Response.status(201).entity("please try again with different Name").build();
		}
	}
	
	//To check wheather the client is free to talk or not
	@Path("/Verify")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response VerifyUser(String name)  {
		if(users.contains(name)){
			if(messs.get(name)){
				messs.put(name, false);
				return Response.status(201).entity("").build();
			}
			else{
				return Response.status(201).entity("the user is busy, try another user to chat").build();
			}
		}
		else{
			return Response.status(201).entity("No user exist on that name please try again").build();
		}
	}
	
	// To get hte users who are online.
	@Path("/GetUsers")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String ShowUser(){
		String Names="";
		for (String s2 : users){
			Names+=s2+" ";
		}
		return Names;//List of Clients
	}
	
	//TO send message
	@Path("/SendMessages")
	@POST
	@Consumes(MediaType.APPLICATION_JSON )
    public Response SendMessage(String toa) {
		JSONObject obj=new JSONObject(toa);
		String f=obj.getString("to");
		String g=obj.getString("from");
		String h=obj.getString("message");
		if(messs.get(f)!=null){
			if(messs.get(f)){
				return Response.status(201).entity("the user "+to+" has left chat" ).build();
			}
			else{
				messs.put(f, false);
				messs.put(g, false);
				logg.add(toa);
				mess.put(g,h);
				gens.put(f,mess);
				return Response.status(201).entity("Message sent from "+g+" to "+f ).build();
			}
		}
		else{
			return Response.status(201).entity("the user "+f+" is not online").build();
		}
		
    }
	
	//To recieve message
	@Path("/GetMessage")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response GetMessage(String name) {
		if(gens.containsKey(name)){
			HashMap<String, String> m=gens.get(name);
			String from="";
			String message="";
			for (Entry<String, String> entry : m.entrySet()){
				from=entry.getKey();
				message=entry.getValue();
				m.remove(from);
			}
			gens.remove(name);
			return Response.status(201).entity(from+": "+message).build();
		}
		else{
			return Response.status(201).entity("failure").build();
		}
	}
	
	//To remove Client from the Chat.
	@Path("/del/{name}")
	@DELETE
	public Response DeleteUser(@PathParam("name") String name) {
		if(users.contains(name)){
			users.remove(name);
			messs.remove(name);
			logg.add("the user "+name+" has been removed");
			return Response.status(201).build();
		}
		else{
			return Response.status(401).build();
		}
	}
	
	// to make Client talk to other person.
	@Path("{name}")
	@DELETE
	public void Delete(@PathParam("name") String name) {
		if(!messs.get(name)){
			messs.put(name,true);
			logg.add("the user "+name+" has left the chat");
		}
	}
}
