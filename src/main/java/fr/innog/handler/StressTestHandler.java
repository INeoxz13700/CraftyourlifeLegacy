package fr.innog.handler;

/*@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class StressTestHandler {

	
	private static  final int maxPlayers = 100;
	public static List<String> fakePlayers = new ArrayList<>();

	@SubscribeEvent
	public static void ServerTickEvent(TickEvent.ServerTickEvent event)
	{
		
		if(event.phase == TickEvent.Phase.START)
		{
			Random rand = new Random();

			int randInt = MathHelper.getInt(rand, 1,1000);
			
			if(randInt >= 500 && randInt <= 600)
			{
				if(fakePlayers.size() < maxPlayers)
					addPlayer(true);
			}
				
			if(fakePlayers.size() > 0)
			{
				if(randInt < 100 || randInt > 900)
				{
					String username = fakePlayers.remove(MathHelper.getInt(rand, 0, fakePlayers.size()-1));
					removePlayer(username);
				}
			}
		}
	}
	
	public static void addPlayer(boolean randName)
	{
		Random rand = new Random();
		String name = "";
		if(randName)
		{
			name = generateRandomUsername(MathHelper.getInt(rand, 4, 13));
			
		}
		else
		{
			name = "test" + (fakePlayers.size()+1);
		}
		
		fakePlayers.add(name);
		
		MinecraftUtils.dispatchConsoleCommand("fakeplayers summon " + name);
		
		PacketHandler.receivedPacketsServer.put(name, new ConcurrentLinkedQueue<PacketBase>());
	}
	
	public static void removePlayer(String name)
	{
		MinecraftUtils.dispatchConsoleCommand("fakeplayers disband " + name);
		fakePlayers.remove(name);
		
		PacketHandler.receivedPacketsServer.remove(name);

	}
	
	 public static String generateRandomUsername(int length) {
		 String validCharacters = "abcdefghijklmnopqrstuvwxyz123456789";
		 Random random = new Random();
	        
	     StringBuilder usernameBuilder = new StringBuilder();
	     for (int i = 0; i < length; i++) {
	         int randomIndex = random.nextInt(validCharacters.length());
	         
	         usernameBuilder.append(validCharacters.charAt(randomIndex));
	        
	     }
	        
	        
	     return usernameBuilder.toString();
	    
	 }
	
}*/
