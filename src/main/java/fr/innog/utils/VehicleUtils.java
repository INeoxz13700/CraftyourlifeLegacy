package fr.innog.utils;

import fr.dynamx.common.contentpack.parts.PartSeat;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;

public class VehicleUtils {

	public static int getSeatsCount(ModularVehicleInfo info)
	{
		return info.getPartsByType(PartSeat.class).size();
	}
	
}
