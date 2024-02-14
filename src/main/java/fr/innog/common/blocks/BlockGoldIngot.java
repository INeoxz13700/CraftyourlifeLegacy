package fr.innog.common.blocks;

import javax.annotation.Nullable;

import fr.innog.client.creativetab.CYLCreativeTab;
import fr.innog.common.tiles.TileEntityGoldIngot;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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

public class BlockGoldIngot extends Block {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
    public BlockGoldIngot(Material material) {
        super(material);
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
        return EnumBlockRenderType.MODEL;
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
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
    	return false;
    }
    
    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
    {
        return false;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityGoldIngot();
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
        return new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.25D, 0.9D);
    }
    
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	TileEntity tile = worldIn.getTileEntity(pos);
    	
    	if(hand == EnumHand.OFF_HAND) return false;

	    if(tile instanceof TileEntityGoldIngot)
	    {
	    	TileEntityGoldIngot tilep = (TileEntityGoldIngot) tile;
	
	    	if(!worldIn.isRemote)
	    	{	    
	    	
		    	if(tilep.entityStealing != null)
		    	{
		    		MinecraftUtils.sendMessage(playerIn, "§cCe Lingot est déjà entrain d'être volé");
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
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
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
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }

}
