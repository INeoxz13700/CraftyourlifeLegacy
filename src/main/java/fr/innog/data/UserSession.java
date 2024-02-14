package fr.innog.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

import fr.innog.api.informations.ApiInformations;
import fr.innog.common.ModCore;
import fr.innog.utils.FileEditor;
import fr.innog.utils.HTTPUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;

public class UserSession {

	private String username;
	
	private String cryptedPassword;
	
	private UserSession() { }
	
	
	public static Object[] connectUser(String username, String password, String connectionData)
	{

		String url = ApiInformations.apiUrl +  "/connection_new.php";
		
		try {
			username = URLEncoder.encode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String encodedUrl = "";
		try {
			encodedUrl = URLEncoder.encode(ApiInformations.blacklist_systemkey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(encodedUrl);

		

		String result = HTTPUtils.doPostHttp(url,"pseudo="+username+"&password="+password+"&cdata=" + ApiInformations.blacklist_systemkey + "," + connectionData);
		
		if(result.equalsIgnoreCase("too many attempts"))
		{
			System.out.println("session incorrect");
			return new Object[] {username, password, "§cTrop de tentative réessayez dans 10 minutes", false};
		}
		else if (result.equalsIgnoreCase("newip"))
		{
		      return new Object[] { username, password, "§cUne nouvelle ip a été détecté, un mail vous a été envoyé vous devez valider votre nouvelle ip (vérifiez vos spams)." , false}; 
		}
		else if(result.equalsIgnoreCase("true"))
		{
			System.out.println("Session sucessfully obtained");
			return new Object[] {username, password, "§aAuthentification réussi!", true};
		}
		else if(result.equalsIgnoreCase("false")) {
			System.out.println("session incorrect");
			return new Object[] {username, password, "§cIdentifiants Incorrect", false};
		}
		else if(result.equalsIgnoreCase("blacklisted"))
		{
			System.out.println("session incorrect");
			return new Object[] {username, password, "§cConnexion impossible.", false};
		}
		else
		{
			System.out.println("session incorrect");
			return new Object[] {username, password, result, false};
		}
	}
	
	public static Object[] connectUser(String username, String password)
	{
		return connectUser(username, password, "");
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public String getCryptedPassword()
	{
		return this.cryptedPassword;
	}
	
	public String getBasicToken()
	{
		return Base64.getEncoder().encodeToString((username + ":" + cryptedPassword).getBytes());
	}
	
	public static UserSession getSession() throws Exception
	{
		UserSession session = new UserSession();
		
		String mcUsername = Minecraft.getMinecraft().getSession().getUsername();

		session.username = mcUsername;
		
		FileEditor editor = new FileEditor(new File("launcher/config"),false);
		
		session.cryptedPassword = editor.getString("custom_password");
		
		return session;

	}
	
	
}
