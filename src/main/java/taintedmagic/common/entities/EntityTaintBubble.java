package taintedmagic.common.entities;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTaintBubble extends EntityThrowable implements IEntityAdditionalSpawnData
{
	public static final String TAG_DAMAGE = "dmg";

	public boolean corrosive = false;
	public float dmg = 0.0F;

	public EntityTaintBubble (World w)
	{
		super(w);
	}

	public EntityTaintBubble (World w, EntityLivingBase e, float scatter, float dmg, boolean corrosive)
	{
		super(w, e);
		this.corrosive = corrosive;
		this.dmg = dmg;
		setThrowableHeading(this.motionX, this.motionY, this.motionZ, func_70182_d(), scatter);
	}

	@Override
	public boolean shouldRenderInPass (int pass)
	{
		return pass == 1;
	}

	protected float getGravityVelocity ()
	{
		return 0.0F;
	}

	protected float func_70182_d ()
	{
		return 1.0F;
	}

	public void onUpdate ()
	{
		// simulated taint swarm motion
		ChunkCoordinates c = new ChunkCoordinates((int) this.posX + this.rand.nextInt(7) - this.rand.nextInt(7), (int) this.posY + this.rand.nextInt(6) - 2, (int) this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7));
		double varX = c.posX + 0.5D - this.posX;
		double varY = c.posY + 0.1D - this.posY;
		double varZ = c.posZ + 0.5D - this.posZ;
		this.motionX += (Math.signum(varX) * 0.5D - this.motionX) * 0.025D;
		this.motionY += (Math.signum(varY) * 0.7D - this.motionY) * 0.05D;
		this.motionZ += (Math.signum(varZ) * 0.5D - this.motionZ) * 0.025D;

		float angle = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
		float wrappedAngle = MathHelper.wrapAngleTo180_float(angle - this.rotationYaw);
		this.rotationYaw += wrappedAngle;

		if (this.ticksExisted > 50) setDead();

		this.motionX *= 0.95D;
		this.motionY *= 0.95D;
		this.motionZ *= 0.95D;

		if (this.onGround)
		{
			this.motionX *= 0.65D;
			this.motionY *= 0.65D;
			this.motionZ *= 0.65D;
		}
		super.onUpdate();
	}

	public void writeSpawnData (ByteBuf buf)
	{
		buf.writeFloat(this.dmg);
	}

	public void readSpawnData (ByteBuf buf)
	{
		this.dmg = buf.readFloat();
	}

	protected void onImpact (MovingObjectPosition mop)
	{
		if (!this.worldObj.isRemote && getThrower() != null)
		{
			List l = this.worldObj.getEntitiesWithinAABBExcludingEntity(getThrower(), this.boundingBox.expand(1.0D, 1.0D, 1.0D));

			for (int i = 0; i < l.size(); i++)
			{
				Entity e = (Entity) l.get(i);
				if (e instanceof EntityLivingBase)
				{
					if (mop.entityHit != null)
					{
						((EntityLivingBase) e).attackEntityFrom(new EntityDamageSourceIndirect("taint", this, getThrower()).setMagicDamage(), this.dmg);
						if (this.corrosive)
						{
							try
							{
								((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.wither.id, 100, 1));
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
						else
						{
							try
							{
								((EntityLivingBase) e).addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
		setDead();
	}

	protected boolean canTriggerWalking ()
	{
		return false;
	}

	@SideOnly (Side.CLIENT)
	public float getShadowSize ()
	{
		return 0.1F;
	}

	public void writeEntityToNBT (NBTTagCompound cmp)
	{
		super.writeEntityToNBT(cmp);
		cmp.setFloat(TAG_DAMAGE, this.dmg);
	}

	public void readEntityFromNBT (NBTTagCompound cmp)
	{
		super.readEntityFromNBT(cmp);
		this.dmg = cmp.getInteger(TAG_DAMAGE);
	}

	public boolean canBeCollidedWith ()
	{
		return false;
	}

	public boolean attackEntityFrom (DamageSource s, float f)
	{
		return false;
	}
}
