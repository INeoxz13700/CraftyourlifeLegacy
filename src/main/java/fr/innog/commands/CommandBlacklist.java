package fr.innog.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import fr.innog.api.informations.ApiInformations;
import fr.innog.utils.HTTPUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandBlacklist  extends ExtendedCommandBase {

	public CommandBlacklist(String commandName) {
		super(commandName);
	}

	@Override
	protected void buildCommand() {
		try {
			addCommandArgument(new CommandStructure("Bannir adresse mac du joueur").addPredefinedArg("ban").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String username = value[1].toString();
					
					if(username.isEmpty())
					{
						MinecraftUtils.sendMessage(sender, "§cEntrez un pseudo");
					}
	
					MinecraftUtils.sendMessage(sender, "§aJoueur en cours de blacklist...");
					Thread r =  new Thread() {

						public  void run() 
						{
										
								try {
									String url = ApiInformations.apiUrl +  "/blacklist_user.php";

								    String result = HTTPUtils.doPostHttp(url, "pseudo=" + username + "&key=" +  URLEncoder.encode(ApiInformations.blacklist_systemkey, "UTF-8") + "&type=ban");
									
								    if(result.equals("true"))
								    {
								    	MinecraftUtils.sendMessage(sender, "§aJoueur définitivement blacklist");
								    		
								    	EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(username);
								    	if(player != null)
								    	{
									    	player.connection.disconnect(new TextComponentString("§chmm, why ?"));
								    	}
								    }
								    else
								    {
								    	MinecraftUtils.sendMessage(sender, result);
								    }
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							    	
						}

					};
					r.start();
				}
			}));
			
			addCommandArgument(new CommandStructure("Débannir adresse mac du joueur").addPredefinedArg("unban").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String username = value[1].toString();
					
					if(username.isEmpty())
					{
						MinecraftUtils.sendMessage(sender, "§cEntrez un pseudo");
					}
	
					MinecraftUtils.sendMessage(sender, "§aRetrait du joueur de la blacklist en cours...");
					Runnable r =  new Runnable() {

					    public  void run() 
					    {

							try {
								String url = ApiInformations.apiUrl +  "/blacklist_user.php";

								String result = HTTPUtils.doPostHttp(url, "pseudo=" + username + "&key=" + URLEncoder.encode(ApiInformations.blacklist_systemkey, "UTF-8") + "&type=unban");
							
								if(result.equals("true"))
						    	{
						    		MinecraftUtils.sendMessage(sender, "§aJoueur retiré de la blacklist!");
						    	}
						    	else
						    	{
						    		MinecraftUtils.sendMessage(sender, result);
						    	}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
					    	
					    }

					};
					r.run();
				}
			}));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
