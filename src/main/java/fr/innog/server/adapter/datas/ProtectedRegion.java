package fr.innog.server.adapter.datas;

import net.minecraft.util.math.BlockPos;

public class ProtectedRegion {

	private String id;
	
    protected BlockPos minPoint;
    protected BlockPos maxPoint;
	
	public ProtectedRegion(String id, BlockPos minPoint, BlockPos maxPoint)
	{
		this.id = id;
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
	}
	
    public String getId() {
        return this.id;
    }
    
    public BlockPos getMinPoint()
    {
    	return minPoint;
    }
    
    public BlockPos getMaxPoint()
    {
    	return maxPoint;
    }
    
    @Override
    public String toString()
    {
    	return id + " - " + getMinPoint() + " - " + getMaxPoint();
    }
    
}
