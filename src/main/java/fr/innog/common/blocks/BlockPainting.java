package fr.innog.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import fr.innog.client.creativetab.CYLCreativeTab;
import fr.innog.common.tiles.TileEntityPainting;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPainting extends Block {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyInteger PAINTING_TYPE = PropertyInteger.create("painting_type", 0, 11);

	
    public BlockPainting(Material material) {
        super(material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(PAINTING_TYPE, 0));
        this.setCreativeTab(CYLCreativeTab.instance);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(getDescription(stack));
    }
    
    public String getDescription(ItemStack is)
    {
    	EnumRarity rarity = getRarity(is);
    	
    	if(rarity == EnumRarity.EPIC)
    	{
        	return "§l§6Tableau Légendaire";
    	}
    	else if(rarity == EnumRarity.RARE)
    	{
        	return "§l§dTableau Epique";
    	}
    	else if(rarity == EnumRarity.UNCOMMON)
    	{
        	return "§l§aTableau Rare";
    	}
    	return "Tableau";
    }
    
    public EnumRarity getRarity(ItemStack is)
    {
    	
    	if(!is.hasTagCompound()) return EnumRarity.COMMON;
    	
    	byte type = is.getTagCompound().getByte("PaintingType");
    	
    	if(type == 0 || type == 4 || type == 5 || type == 6)
    	{
    		return EnumRarity.EPIC;
    	}
    	else if(type == 7 || type == 2)
    	{
    		return EnumRarity.RARE;
    	}
    	else if(type == 3 || type == 9 || type == 10 || type == 11)
    	{
    		return EnumRarity.UNCOMMON;
    	}
    	return EnumRarity.COMMON;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityPainting();
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean isBlockNormalCube()
    {
    	return false;
    }


    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	if(state.getValue(FACING) == EnumFacing.SOUTH)
    	{
            return new AxisAlignedBB(0, 0, 0, 1f, 1f, 0.1f);
    	}
    	else if(state.getValue(FACING) == EnumFacing.NORTH)
    	{
            return new AxisAlignedBB(0, 0, 0.9f, 1f, 1f, 1f);
    	}
    	else if(state.getValue(FACING) == EnumFacing.WEST)
    	{
            return new AxisAlignedBB(0.9, 0, 0, 1f, 1f, 1f);
    	}
        return new AxisAlignedBB(0, 0, 0, 0.1f, 1f, 1f);

    }
    

    
    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
    	return false;
    }
    
    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
    {
        return false;
    }
    
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {    	
    	if(hand == EnumHand.OFF_HAND) return false;

    	TileEntity tile = worldIn.getTileEntity(pos);

	    if(tile instanceof TileEntityPainting)
	    {
	    	TileEntityPainting tilep = (TileEntityPainting) tile;
	
	    	if(!worldIn.isRemote)
	    	{	    
	    	
		    	if(tilep.entityStealing != null)
		    	{
		    		MinecraftUtils.sendMessage(playerIn, "§cCe Tableau est déjà entrain d'être volé");
		    		return true;
		    	}

		    	PacketCollection.notificateClientStealing(playerIn, new BlockPos(tilep.getPos().getX(),tilep.getPos().getY(),tilep.getPos().getZ()));
		    	
		    	MinecraftUtils.getPlayerCapability(playerIn).steal(tilep);

		    	tilep.setStealingEntity(playerIn);
		    	
		    	return true;
	    	}
	  
	    	
    	}
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    	if(worldIn.isRemote) return;

    	EnumFacing facing = placer.getHorizontalFacing().getOpposite();
    	
    	int type = MathHelper.getInt(placer.getRNG(), 0, 11);
        
    	worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(PAINTING_TYPE, type));
    	
        TileEntityPainting tile = (TileEntityPainting)worldIn.getTileEntity(pos);
        
        tile.setPaintingType((byte)type);
        tile.markDirty();
    
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {           
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        
        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }
        
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }
    
    
    @Override
    public int getMetaFromState(IBlockState state)
    {	
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }
    
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, PAINTING_TYPE});
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
    	if(state.getValue(FACING) == EnumFacing.SOUTH)
    	{
            if(worldIn.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.AIR)
            {
            	worldIn.destroyBlock(pos, true);
            }
    	}
    	else if(state.getValue(FACING) == EnumFacing.NORTH)
    	{
            if(worldIn.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.AIR)
            {
            	worldIn.destroyBlock(pos, true);
            }    	
        }
    	else if(state.getValue(FACING) == EnumFacing.WEST)
    	{
            if(worldIn.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.AIR)
            {
            	worldIn.destroyBlock(pos, true);
            }    	
    	}
    	else if(state.getValue(FACING) == EnumFacing.EAST)
    	{
        	if(worldIn.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR)
            {
            	worldIn.destroyBlock(pos, true);
            } 
    	}
        
    }
    
    @Deprecated
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
    	TileEntityPainting painting = (TileEntityPainting) worldIn.getTileEntity(pos);
    	    	    	    	
        return state.withProperty(PAINTING_TYPE, (int) painting.getPaintingType());
    }

    



}
