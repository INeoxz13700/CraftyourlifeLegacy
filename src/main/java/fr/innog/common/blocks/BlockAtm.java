package fr.innog.common.blocks;

import fr.innog.client.creativetab.CYLCreativeTab;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAtm extends Block {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool RENDERMODEL = PropertyBool.create("render_model");

    public BlockAtm(Material material) {
        super(material);
        this.setHardness(100000.0F);
        this.setResistance(100000.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	TileEntity tile = worldIn.getTileEntity(pos);
    	
    	if(hand == EnumHand.OFF_HAND) return false;

    	if(worldIn.isRemote) return false;
    	
    	ModControllers.uiController.displayUI(playerIn, 6);
	    
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    	EnumFacing facing = placer.getHorizontalFacing().getOpposite();

        worldIn.setBlockState(pos, state.withProperty(FACING, facing).withProperty(RENDERMODEL, true));
        
        worldIn.setBlockState(pos.up(), state.withProperty(FACING, facing).withProperty(RENDERMODEL, false));
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {  
    	boolean flag = false;
        
        if(meta >= 10)
        {
           meta -= 10;
           flag = true;
        }
        
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(RENDERMODEL, flag);
    }
    
    @Override
    public int getMetaFromState(IBlockState state)
    {
    	int meta = 0;
    	
    	if(state.getValue(RENDERMODEL))
    	{
    		meta = 10;
    	}
    	
        return meta + ((EnumFacing)state.getValue(FACING)).getIndex();
    }
    
    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
    	if(state.getValue(RENDERMODEL))
    	{
    		worldIn.setBlockToAir(pos.up());
    	}
    	else
    	{
    		worldIn.setBlockToAir(pos.down());
    	}
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
        return new BlockStateContainer(this, new IProperty[] {FACING, RENDERMODEL});
    }

}
