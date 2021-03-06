package elucent.roots.component.components;

import java.util.ArrayList;
import java.util.UUID;

import elucent.roots.component.ComponentBase;
import elucent.roots.component.EnumCastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ComponentPinkTulip extends ComponentBase {
	public ComponentPinkTulip(){
		super("pinktulip", "Vampiric Burst", Blocks.RED_FLOWER,7,10);
	}
	
	@Override
	public void doEffect(World world, Entity caster, EnumCastType type, double x, double y, double z, double potency, double duration, double size){
		if (type == EnumCastType.SPELL){
			ArrayList<EntityLivingBase> targets = (ArrayList<EntityLivingBase>) world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-(2.0+size),y-(2.0+size),z-(2.0+size),x+(2.0+size),y+(2.0+size),z+(2.0+size)));
			for (int i = 0; i < targets.size(); i ++){
				if (targets.get(i).getUniqueID() != caster.getUniqueID()){
					targets.get(i).attackEntityFrom(DamageSource.wither, (int)(3+2*potency));
					((EntityLivingBase)caster).heal(targets.size()*(float)(2.0+1.0*potency));
					targets.get(i).setLastAttacker(caster);
					targets.get(i).setRevengeTarget((EntityLivingBase)caster);
				}
			}
		}
	}
	
	@Override
	public void doEffect(World world, UUID casterId, Vec3d direction, EnumCastType type, double x, double y, double z, double potency, double duration, double size){
		if (type == EnumCastType.SPELL){
			EntityPlayer player = world.getPlayerEntityByUUID(casterId);
			ArrayList<EntityLivingBase> targets = (ArrayList<EntityLivingBase>) world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-(2.0+size),y-(2.0+size),z-(2.0+size),x+(2.0+size),y+(2.0+size),z+(2.0+size)));
			for (int i = 0; i < targets.size(); i ++){
				if (targets.get(i).getUniqueID() != casterId){
					targets.get(i).attackEntityFrom(DamageSource.wither, (int)(3+2*potency));
					if (player != null){
						player.heal(targets.size()*(float)(2.0+1.0*potency));
						targets.get(i).setLastAttacker(player);
						targets.get(i).setRevengeTarget((EntityLivingBase)player);
					}
				}
			}
		}
	}
}
