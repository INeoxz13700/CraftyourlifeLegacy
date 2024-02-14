package fr.innog.capability.playercapability;

import java.util.ArrayList;
import java.util.List;

import fr.innog.advancedui.guicomponents.UIButton.CallBackObject;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.PlayerRenderSynchronizer;
import fr.innog.common.animations.PlayerAnimator;
import fr.innog.common.inventory.CustomInventoryPlayer;
import fr.innog.common.items.Items;
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
import fr.innog.handler.TicksHandler;
import fr.innog.network.PacketCollection;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerData implements IPlayer  {

	public static int crowbarId = 4473;
	
	private VehicleDatas vehicleData;
	
	private CosmeticDatas cosmeticData;
	
	private RemoteUIProcessor currentRemoteUI;
		
	private RemoteUICache cachedData;
	
	private IStealingTileEntity stealingTile;
	
	private EntityPlayer player;
	
	private PlayerAnimator currentPlayingAnimation;
	
	private PlayerRenderSynchronizer renderSynchronizer;
	
	private List<Integer> unlockedAnimations = new ArrayList<>();
	
	private IdentityData identity = new IdentityData();
	
	private ThirstStats thirst;
	
	private ShieldStats shield;
		
	private boolean useBulletGilet = false;
	
	private PlayerHealthData healthData;
	
	private EthylotestRequest request;
	
	private ItemPressingData itemPressing;
	
	private LoginInformation loginInfo;
	
	private PhoneData phoneData;
	
	private PenaltyManager penaltyManager;
	
	private CustomInventoryPlayer inventory;
	
	private CrowbarState crowbarState;

	@Override
	public void initData(Entity entity) {
		cachedData = new RemoteUICache();
		vehicleData = new VehicleDatas();
		cosmeticData = new CosmeticDatas(this);
		renderSynchronizer = new PlayerRenderSynchronizer(this);
		identity = new IdentityData();
		loginInfo = new LoginInformation();
		
		this.player = (EntityPlayer)entity;
			
		shield = new ShieldStats(player);
		thirst = new ThirstStats(player);
			
		healthData = new PlayerHealthData(player);
		
		itemPressing = new ItemPressingData(player);
		
		phoneData = new PhoneData(player);
		
		penaltyManager = new PenaltyManager(this);
		
		inventory = new CustomInventoryPlayer(player);
		
	}
	
	@Override
	public void permute(IPlayer oldPlayerdata) {
		this.vehicleData = oldPlayerdata.getVehicleDatas();
		this.cachedData = oldPlayerdata.getCachedData();
		this.stealingTile = oldPlayerdata.getStealingTile();
		this.cosmeticData = oldPlayerdata.getCosmeticDatas();
		this.identity = oldPlayerdata.getIdentityData();
		this.loginInfo = oldPlayerdata.getLoginInformation();
		this.phoneData = oldPlayerdata.getPhoneData();
		this.penaltyManager = oldPlayerdata.getPenaltyManager();
		this.inventory = oldPlayerdata.getInventory();
	}

	@Override
	public RemoteUIProcessor getCurrentRemoteUI() {
		return currentRemoteUI;
	}

	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) {
		this.currentRemoteUI = remoteUI;
	}

	@Override
	public RemoteUICache getCachedData() {
		return cachedData;
	}


	@Override
	public VehicleDatas getVehicleDatas() {
		return vehicleData;
	}


	@Override
	public void setVehiclesData(VehicleDatas vehicleData) {
		this.vehicleData = vehicleData;
	}

	@Override
	public void stopStealing() {
		if(!player.world.isRemote) 
		{
			TileEntity tile = (TileEntity) stealingTile;

			PacketCollection.notificateClientStealing(player, new BlockPos(tile.getPos().getX(),tile.getPos().getY(),tile.getPos().getZ()));
		}

		stealingTile.resetStealing();
		stealingTile = null;		
	}

	@Override
	public boolean isStealing() {
		return stealingTile != null;
	}

	@Override
	public IStealingTileEntity getStealingTile() {
		return stealingTile;
	}

	@Override
	public void steal(IStealingTileEntity te) {
		this.stealingTile = te;
	}

	@Override
	public CosmeticDatas getCosmeticDatas() {
		return cosmeticData;
	}

	@Override
	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public PlayerAnimator getCurrentPlayingAnimation() {
		return currentPlayingAnimation;
	}

	@Override
	public void setCurrentPlayingAnimation(PlayerAnimator animator) {
		this.currentPlayingAnimation = animator;
	}

	@Override
	public PlayerRenderSynchronizer getPlayerRenderSynchronizer() {
		return renderSynchronizer;
	}
	
    
	public void updateRenderer() 
	{
		if(getPlayerRenderSynchronizer().isRemote())
		{
			PacketCollection.updateRendererSynchronizer();
		}
		else
		{
			getPlayerRenderSynchronizer().clear();
			ModCore.log("Update render");
		}
	}

	@Override
	public List<Integer> getUnlockedAnimations() {
		return unlockedAnimations;
	}

	@Override
	public void setUnlockedAnimations(List<Integer> animations) {
		unlockedAnimations = animations;
	}

	@Override
	public void setIdentityData(IdentityData identity) {
		this.identity = identity;
	}

	@Override
	public IdentityData getIdentityData() {
		return identity;
	}


	@Override
	public ThirstStats getThirstStats() {
		return thirst;
	}

	@Override
	public void syncThirst() {
		PacketCollection.syncDataTo("ThirstData",new SyncStruct<Float>(thirst.getThirst()), player);
	}

	@Override
	public void syncLoginData() {
		syncThirst();
		syncShield();
		
		phoneData.syncPhone();
		
		healthData.syncAlcol();
		healthData.syncShouldBeReanimate();
	}

	@Override
	public ShieldStats getShieldStats() {
		return shield;
	}

	@Override
	public void syncShield() {
		PacketCollection.syncDataTo("ShieldData",new SyncStruct<Float>(shield.getShield()), player);
	}

	@Override
	public boolean isUsingGiletBullet() {
		return useBulletGilet;
	}

	@Override
	public void setUseGiletBullet(boolean state) {
		useBulletGilet = state;
	}

	@Override
	public PlayerHealthData getHealthData() {
		return healthData;
	}

	@Override
	public void onReanimated() {
		getHealthData().setShouldBeReanimate(false);
				

		player.clearActivePotions();
		player.extinguish();

		getHealthData().setAlcolInBlood(0f);
		
		ModControllers.playerController.heal(player);

		
		//setProning(false);
		ModControllers.playerController.forcePlayerWakeup(player);
		
		player.setPositionAndUpdate(-1212, 49, 124);		
	}

	@Override
	public void onReanimationEnter() {
		this.getHealthData().reanimatingPlayername = null;
		this.getHealthData().setShouldBeReanimate(true);
		this.getHealthData().reanimationTick = 0;
		this.getHealthData().startTimeTimestamp = System.currentTimeMillis();
		
		MinecraftUtils.dispatchConsoleCommand("wanted remove " + player.getName());
	}

	@Override
	public void onRespawn() {
		this.getHealthData().setShouldBeReanimate(false);
				
		ModControllers.playerController.heal(player);

		ModControllers.reanimationController.clearInventory(player);		
	
		
		TicksHandler.scheduleCallback(1000, new CallBackObject()
		{
			@Override
			public void call()
			{
				MinecraftUtils.dispatchConsoleCommand("apidata respawn " + player.getName());
			}
		});
	
	}

	@Override
	public void reanimate(EntityPlayer target) {
		
		ItemStack heldItem = player.getHeldItemMainhand();
		if(heldItem != ItemStack.EMPTY && heldItem.getItem() == Items.syringe) //seringue pierre ici
		{
			try {
				if(!ModCore.getCylrp().getJobName(player).equalsIgnoreCase("Medecin"))
				{
					MinecraftUtils.sendMessage(player, "§cVous n'êtes pas medecin.");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			IPlayer targetData = MinecraftUtils.getPlayerCapability(target);
			
	    	if(targetData.getHealthData().getShouldBeReanimate() || targetData.getHealthData().shouldBeInEthylicComa())
	    	{
		    	if(getHealthData().reanimatingPlayername == null)
		    	{
		    		if(targetData.getHealthData().reanimatingPlayername == null)
		    		{
		    			getHealthData().reanimatingPlayername = target.getName();
		    			targetData.getHealthData().reanimatingPlayername = player.getName();
		    			MinecraftUtils.sendMessage(player, "§aDébut de la réanimation de §c" + getHealthData().reanimatingPlayername);
		    		}
		    		else
		    		{
		    			MinecraftUtils.sendMessage(player, "§cCe patient se fait déjà réanimer par quelqu'un.");
		    		} 
		    	}
		    	else
		    	{
			    	MinecraftUtils.sendMessage(player, "§cVous êtes déjà entrain de réanimer un patient.");
		    	}
	    	}
	    	else
	    	{
    			MinecraftUtils.sendMessage(player, "§cCe patient est en forme.");
	    	}
		}
	}

	@Override
	public void setEthylotestRequest(EthylotestRequest request) {
		this.request = request;
	}

	@Override
	public EthylotestRequest getEthylotestRequest() {
		return request;
	}
	@Override
	public ItemPressingData getItemPressing() {
		return itemPressing;
	}

	@Override
	public LoginInformation getLoginInformation() {
		return loginInfo;
	}

	@Override
	public PhoneData getPhoneData() {
		return phoneData;
	}

	@Override
	public PenaltyManager getPenaltyManager() {
		return penaltyManager;
	}

	@Override
	public CustomInventoryPlayer getInventory() {
		return inventory;
	}

	@Override
	public CrowbarState getCrowbarState() {
		return crowbarState;
	}

	@Override
	public void crowbarDoor(BlockPos doorPos) {
		crowbarState = new CrowbarState(doorPos, player.getPosition());
	}

	@Override
	public void clearCrowbarState() {
		crowbarState = null;
	}


}
