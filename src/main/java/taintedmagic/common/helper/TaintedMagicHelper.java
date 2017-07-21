package taintedmagic.common.helper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.codechicken.lib.vec.Vector3;

public class TaintedMagicHelper
{
	public static AspectList getPrimals (int amount)
	{
		return new AspectList().add(Aspect.FIRE, amount).add(Aspect.WATER, amount).add(Aspect.EARTH, amount).add(Aspect.AIR, amount).add(Aspect.ORDER, amount).add(Aspect.ENTROPY, amount);
	}

	public static Vector3 getDistanceBetween (Entity e, Entity target)
	{
		Vector3 fromPosition = new Vector3(e.posX, e.posY, e.posZ);
		Vector3 toPosition = new Vector3(target.posX, target.posY, target.posZ);
		Vector3 dist = fromPosition.sub(toPosition);
		dist.normalize();
		return dist;
	}

	public static double getDistanceTo (double x, double y, double z, EntityPlayer p)
	{
		double distX = p.posX + 0.5D - x;
		double distY = p.posY + 0.5D - y;
		double distZ = p.posZ + 0.5D - z;
		return distX * distX + distY * distY + distZ * distZ;
	}
}
