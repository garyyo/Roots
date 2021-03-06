package elucent.roots.component.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import elucent.roots.Roots;
import elucent.roots.Util;
import elucent.roots.component.ComponentBase;
import elucent.roots.component.EnumCastType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ComponentPeony extends ComponentBase{
	List<BlockPos> pos = new CopyOnWriteArrayList<BlockPos>();
	Random random = new Random();
	public ComponentPeony(){
		super("peony", "Regeneration", Blocks.DOUBLE_PLANT,5,24);
	}
	
	@Override
	public void castingAction(EntityPlayer player, int count, int potency, int efficiency, int size){
		World world = player.worldObj;
		int x = player.getPosition().getX();
		int y = player.getPosition().getY();
		int z = player.getPosition().getZ();
		
		int range = 5;
		for(int x2 = -range; x2 < range; x2++){
			for(int z2 = -range; z2 < range; z2++){
				for(int y2 = -1; y2 < 2; y2++){
					if(world.getBlockState(new BlockPos(x+x2, y+y2, z+z2)).getBlock() == Blocks.RED_FLOWER || world.getBlockState(new BlockPos(x+x2, y+y2, z+z2)).getBlock() == Blocks.YELLOW_FLOWER || world.getBlockState(new BlockPos(x+x2, y+y2, z+z2)).getBlock() == Blocks.DOUBLE_PLANT){
						pos.add(new BlockPos(x+x2, y+y2, z+z2));
					}
				}	
			}
		}
		
		pos.forEach((temp) ->{
			int fX = temp.getX();
			int fY = temp.getY();
			int fZ = temp.getZ();
			double factor = 0.15;
			
			if(random.nextInt(10 * pos.size()) == 0){
				Roots.proxy.spawnParticleMagicAuraFX(world, fX+Util.randomDouble(0.0, 0.5), fY+Util.randomDouble(0.1, 0.5), fZ+Util.randomDouble(0.0, 0.5), (player.posX-fX) * factor, (player.posY-fY) * factor, (player.posZ-fZ) * factor, 138, 42, 235);
				Roots.proxy.spawnParticleMagicAuraFX(world, fX+Util.randomDouble(0.0, 0.5), fY+Util.randomDouble(0.1, 0.5), fZ+Util.randomDouble(0.0, 0.5), (player.posX-fX) * factor, (player.posY-fY) * factor, (player.posZ-fZ) * factor, 138, 42, 235);
			}
		});
	}
	
	@Override
	public void doEffect(World world, Entity caster, EnumCastType type, double x, double y, double z, double potency, double duration, double size){
		
		if (type == EnumCastType.SPELL){
			((EntityLivingBase) caster).heal((float)(pos.size()/2+(potency*2)));
			pos.clear();
		}
	}	
	
	@Override
	public void doEffect(World world, UUID casterId, Vec3d direction, EnumCastType type, double x, double y, double z, double potency, double duration, double size){
		
		if (type == EnumCastType.SPELL){
			List<EntityLivingBase> targets = (List<EntityLivingBase>)world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-0.25,y-0.25,z-0.25,x+0.25,y+0.25,z+0.25));
			if (targets.size() > 0){
				targets.get(0).heal((float)(pos.size()/2+(potency*2)));
			}
			pos.clear();
		}
	}
}