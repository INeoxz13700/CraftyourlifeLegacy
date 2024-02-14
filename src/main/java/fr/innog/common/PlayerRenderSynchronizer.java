package fr.innog.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.network.PacketCollection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;

/*Chaque joueur côté serveur posséde une instance de ce type de class
 *Elle permet de transmettre ses données cosmetics à tous les clients dans un rayon de 30 blocks 
 */
public class PlayerRenderSynchronizer {
	
	private final int renderRadius = 30;
	
	private final int updateTime = 2; //In Seconds
	
	private IPlayer ep;
	
	private List<EntityPlayer> previousPlayersInArea = new ArrayList<EntityPlayer>();

	private List<EntityPlayer> playersInArea = new ArrayList<EntityPlayer>();
	
	private long lastTime;
	
	public PlayerRenderSynchronizer(IPlayer ep)
	{
		this.ep = ep;
	}
	
	public boolean isRemote()
	{
		return ep.getPlayer().world.isRemote;
	}
	
	public void update()
	{		
		if((System.currentTimeMillis() - lastTime) / 1000 >= updateTime)
		{
			lastTime = System.currentTimeMillis();
			EntityPlayer player = ep.getPlayer();
			previousPlayersInArea = playersInArea;
			playersInArea =  player.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(player.posX-renderRadius, player.posY-renderRadius, player.posZ-renderRadius, player.posX + renderRadius, player.posY + renderRadius, player.posZ+renderRadius));
			updateRenderDataForPlayers(getPlayersToUpdate());
		}
	}
	
	private List<EntityPlayer> getPlayersToUpdate()
	{
		return playersInArea.stream().filter(x -> !previousPlayersInArea.contains(x)).collect(Collectors.toList());
	}
	
	/*
	 * On récupére tous les joueurs à qui on a jamais encoré envoyé de packet ou qui n'a pas subit de changement au niveau des cosmétics
	 * Un packet qui contient tous la liste des cosmétiques équipé par le joueur maître de cette instance
	 */
	private void updateRenderDataForPlayers(List<EntityPlayer> playersToUpdate)
	{
		List<CosmeticObject> toSend = ep.getCosmeticDatas().getEquippedCosmetics();
		for(int i = 0; i < playersToUpdate.size(); i++)
		{

			EntityPlayer updatePlayer = playersToUpdate.get(i);
			
			if(updatePlayer.getGameProfile().getName().equalsIgnoreCase(ep.getPlayer().getGameProfile().getName())) continue;
						
			//On envoie le packet d'update de rendu à tous les joueurs dans un rayon de 30 blocks
			PacketCollection.updatePlayerCosmeticRenderData(updatePlayer, ep.getPlayer(), toSend);
			
			if(ep.getCurrentPlayingAnimation() != null) PacketCollection.playAnimation(updatePlayer, ep.getPlayer(), ep.getCurrentPlayingAnimation().getAnimationId());
			else PacketCollection.stopAnimation(updatePlayer, ep.getPlayer());
		
			PacketCollection.synchronizeExtendedArmor((EntityPlayerMP)updatePlayer,(EntityPlayerMP) ep.getPlayer());
		}
	}
	
	public void clear()
	{
		previousPlayersInArea.clear();
		playersInArea.clear();
	}
	
}