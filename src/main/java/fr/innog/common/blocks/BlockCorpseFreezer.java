package fr.innog.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCorpseFreezer extends Block {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool RENDERMODEL = PropertyBool.create("render_model");
    public static final PropertyBool OPEN_STATE = PropertyBool.create("open_state");

	
	public BlockCorpseFreezer(Material materialIn) {
		super(materialIn);
        this.setHardness(100000.0F);
        this.setResistance(100000.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(RENDERMODEL, true).withProperty(OPEN_STATE, false));
	}
	
	  @Override
	    public boolean isOpaqueCube(IBlockState state)
	    {
	        return false;
	    }
	    
	    @Override
	    public EnumBlockRenderType getRenderType(IBlockState state)
	    {
	    	if(state.getValue(RENDERMODEL))
	    		return EnumBlockRenderType.MODEL;
	    	
	    	return EnumBlockRenderType.INVISIBLE;
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
	    
	    @Override
	    public boolean hasTileEntity(IBlockState state)
	    {
	        return false;
	    }
	    
	    @SideOnly(Side.CLIENT)
	    public boolean isBlockNormalCube()
	    {
	    	return false;
	    }


	    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	    {
	        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	    }
	    
	    
	    @Override
	    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	    {
	    	if(worldIn.isRemote) return;

	    	if(state.getValue(FACING) == EnumFacing.NORTH)
	    	{
	    		if(state.getValue(RENDERMODEL))
		    	{
		    		worldIn.setBlockToAir(pos.south());
		    	}
		    	else
		    	{
		    		worldIn.setBlockToAir(pos.north());
		    	}
	    	}
	    	if(state.getValue(FACING) == EnumFacing.SOUTH)
	    	{
	    		if(state.getValue(RENDERMODEL))
		    	{
		    		worldIn.setBlockToAir(pos.north());
		    	}
		    	else
		    	{
		    		worldIn.setBlockToAir(pos.south());
		    	}
	    	}
	    	if(state.getValue(FACING) == EnumFacing.WEST)
	    	{
	    		if(state.getValue(RENDERMODEL))
		    	{
		    		worldIn.setBlockToAir(pos.east());
		    	}
		    	else
		    	{
		    		worldIn.setBlockToAir(pos.west());
		    	}
	    	}
	    	if(state.getValue(FACING) == EnumFacing.EAST)
	    	{
	    		if(state.getValue(RENDERMODEL))
		    	{
		    		worldIn.setBlockToAir(pos.west());
		    	}
		    	else
		    	{
		    		worldIn.setBlockToAir(pos.east());
		    	}
	    	}
	    	
	    	
	    	
	 
	    }
	    
	    
	    @Override
	    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	    {	    	
	    	if(hand == EnumHand.OFF_HAND) return false;

	    	if(worldIn.isRemote) return false;
	    	
	    	if(state.getValue(FACING) == EnumFacing.WEST)
	    	{		
	    		worldIn.setBlockState(pos,state.withProperty(OPEN_STATE, !state.getValue(OPEN_STATE)));
	    	}
	    	else if(state.getValue(FACING) == EnumFacing.EAST)
	    	{
	    		worldIn.setBlockState(pos,state.withProperty(OPEN_STATE, !state.getValue(OPEN_STATE)));

	    	}
	    	else if(state.getValue(FACING) == EnumFacing.NORTH)
	    	{
	    		worldIn.setBlockState(pos,state.withProperty(OPEN_STATE, !state.getValue(OPEN_STATE)));
	    	}
	    	else if(state.getValue(FACING) == EnumFacing.SOUTH)
	    	{
	    		worldIn.setBlockState(pos,state.withProperty(OPEN_STATE, !state.getValue(OPEN_STATE)));
	    	}
	    	
		    
	        return false;
	    }
	    
	    @Override
	    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	    {
	    	EnumFacing facing = placer.getHorizontalFacing().getOpposite();

	    	if(facing == EnumFacing.WEST)
	    	{
	    		if(worldIn.getBlockState(pos.east()).getBlock() != net.minecraft.init.Blocks.AIR)
	    		{
	    			worldIn.setBlockToAir(pos);
	    			return;
	    		}
		        worldIn.setBlockState(pos.east(), state.withProperty(FACING, facing).withProperty(RENDERMODEL, false).withProperty(OPEN_STATE, false));
		        worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(RENDERMODEL, true).withProperty(OPEN_STATE, false));
	    	}
	    	else if(facing == EnumFacing.EAST)
	    	{
	    		if(worldIn.getBlockState(pos.west()).getBlock() != net.minecraft.init.Blocks.AIR)
	    		{
	    			worldIn.setBlockToAir(pos);
	    			return;
	    		}
		        worldIn.setBlockState(pos.west(), state.withProperty(FACING, facing).withProperty(RENDERMODEL, false).withProperty(OPEN_STATE, false));
		        worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(RENDERMODEL, true).withProperty(OPEN_STATE, false));
	    	}
	    	else if(facing == EnumFacing.NORTH)
	    	{
	    		if(worldIn.getBlockState(pos.south()).getBlock() != net.minecraft.init.Blocks.AIR)
	    		{
	    			worldIn.setBlockToAir(pos);
	    			return;
	    		}
		        worldIn.setBlockState(pos.south(), state.withProperty(FACING, facing).withProperty(RENDERMODEL, false).withProperty(OPEN_STATE, false));
		        worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(RENDERMODEL, true).withProperty(OPEN_STATE, false));
	    	}
	    	else if(facing == EnumFacing.SOUTH)
	    	{
	    		if(worldIn.getBlockState(pos.north()).getBlock() != net.minecraft.init.Blocks.AIR)
	    		{
	    			worldIn.setBlockToAir(pos);
	    			return;
	    		}
		        worldIn.setBlockState(pos.north(), state.withProperty(FACING, facing).withProperty(RENDERMODEL, false).withProperty(OPEN_STATE, false));
		        worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(RENDERMODEL, true).withProperty(OPEN_STATE, false));
	    	}
	    }
	    
	    final int DIRECTION_MASK = 0b11;   // Deux premiers bits pour la direction
	    final int BOOLEAN1_MASK = 0b100;   // Troisième bit pour le booléen 1
	    final int BOOLEAN2_MASK = 0b1000;  // Quatrième bit pour le booléen 2
	    
	    @Override
	    public IBlockState getStateFromMeta(int meta)
	    {  	    	
	    	
	        EnumFacing facing = EnumFacing.getFront((meta & DIRECTION_MASK)+2);
	        boolean renderModel = (meta & BOOLEAN1_MASK) != 0;
	        boolean openState = (meta & BOOLEAN2_MASK) != 0;

	    	IBlockState state = this.getDefaultState().withProperty(FACING, facing);
	    	state = state.withProperty(RENDERMODEL, renderModel);
	    	state = state.withProperty(OPEN_STATE, openState);

	    	return state;
	    }
	    
	    @Override
	    public int getMetaFromState(IBlockState state)
	    {
	    	//NORTH = 0 SOUTH = 1 WEST = 2 EAST = 3
	        int meta = ((EnumFacing) state.getValue(FACING)).getIndex()-2;

	        meta |= (meta & DIRECTION_MASK);

	        if (state.getValue(RENDERMODEL)) {
	        	meta |= BOOLEAN1_MASK;
	        }
	        if (state.getValue(OPEN_STATE)) {
	        	meta |= BOOLEAN2_MASK;
	        }


	        return meta;
	    }
	    

	    
	    @Override
	    protected BlockStateContainer createBlockState()
	    {
	        return new BlockStateContainer(this, new IProperty[] {FACING,  RENDERMODEL, OPEN_STATE});
	    }
	    
}
