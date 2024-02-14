package fr.innog.capability.playercapability;

import java.util.List;

import fr.innog.common.PlayerRenderSynchronizer;
import fr.innog.common.animations.PlayerAnimator;
import fr.innog.common.inventory.CustomInventoryPlayer;
import fr.innog.common.penalty.PenaltyManager;
import fr.innog.common.shield.ShieldStats;
import fr.innog.common.thirst.ThirstStats;
import fr.innog.common.tiles.IStealingTileEntity;
import fr.innog.data.CosmeticDatas;
import fr.innog.data.CrowbarState;
import fr.innog.data.EthylotestRequest;
import fr.innog.data.IdentityData;
import fr.innog.data.ItemPressingData;
import fr.innog.data.LoginInformation;
import fr.innog.data.PhoneData;
import fr.innog.data.PlayerHealthData;
import fr.innog.data.VehicleDatas;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.ui.remote.data.RemoteUICache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public interface IPlayer {

	
	public RemoteUIProcessor getCurrentRemoteUI();
	
	public RemoteUICache getCachedData();
	
	public void setRemoteUI(RemoteUIProcessor remoteUI);
		
	public void initData(Entity entity);

	public void permute(IPlayer oldPlayerdata);
	
	public VehicleDatas getVehicleDatas();
	
	public void setVehiclesData(VehicleDatas vehicleData);
	
	public void stopStealing();
	
	public boolean isStealing();
	
	public IStealingTileEntity getStealingTile();
	
	public void steal(IStealingTileEntity te);
	
	public CosmeticDatas getCosmeticDatas();
	
	public EntityPlayer getPlayer();
	
	public PlayerAnimator getCurrentPlayingAnimation();
	
	public void setCurrentPlayingAnimation(PlayerAnimator animator);
	
	public PlayerRenderSynchronizer getPlayerRenderSynchronizer();

	public void updateRenderer();
	
	public List<Integer> getUnlockedAnimations();
	
	public void setUnlockedAnimations(List<Integer> animations);
	
	public void setIdentityData(IdentityData identity);
	
	public IdentityData getIdentityData();	
	
	public ThirstStats getThirstStats();
	
	public ShieldStats getShieldStats();
	
	public void syncThirst();
	
	public void syncShield();
	
	public void syncLoginData();
	
	public boolean isUsingGiletBullet();
	
	public void setUseGiletBullet(boolean state);
	
	public PlayerHealthData getHealthData();
	
	public void onReanimated();
	
	public void onReanimationEnter();
	
	public void onRespawn();
	
	public void reanimate(EntityPlayer target);
	
	public void setEthylotestRequest(EthylotestRequest request);
	
	public EthylotestRequest getEthylotestRequest();
		
	public ItemPressingData getItemPressing();
	
	public LoginInformation getLoginInformation();
	
	public PhoneData getPhoneData();
	
	public PenaltyManager getPenaltyManager();
	
	public CustomInventoryPlayer getInventory();
	
	public CrowbarState getCrowbarState();
	
	public void clearCrowbarState();
	
	public void crowbarDoor(BlockPos doorPos);
	
}
