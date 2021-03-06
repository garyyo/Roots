package elucent.roots.component.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Lists;

import elucent.roots.ConfigManager;
import elucent.roots.RootsNames;
import elucent.roots.Util;
import elucent.roots.PlayerManager;
import elucent.roots.RegistryManager;
import elucent.roots.component.ComponentBase;
import elucent.roots.component.ComponentEffect;
import elucent.roots.component.EnumCastType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public class ComponentAllium extends ComponentBase{
	Random random = new Random();
	public ComponentAllium(){
		super("allium","Allium's Ruin",Blocks.RED_FLOWER,8);	
	}
	
	public void destroyBlockSafe(World world, BlockPos pos, int potency){
		if (world.getBlockState(pos).getBlock().getHarvestLevel(world.getBlockState(pos)) <= 2+potency && world.getBlockState(pos).getBlock().getBlockHardness(world.getBlockState(pos), world, pos) != -1){
			world.destroyBlock(pos, true);
		}
	}
	
	@Override
	public void doEffect(World world, Entity caster, EnumCastType type, double x, double y, double z, double potency, double duration, double size){
		if (type == EnumCastType.SPELL){
			int damageDealt = 0;
			ArrayList<EntityLivingBase> targets = (ArrayList<EntityLivingBase>) world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-(2.0+size),y-(2.0+size),z-(2.0+size),x+(2.0+size),y+(2.0+size),z+(2.0+size)));
			for (int i = 0; i < targets.size(); i ++){
				if (targets.get(i).getUniqueID() != caster.getUniqueID()){
					if (targets.get(i) instanceof EntityPlayer && ConfigManager.disablePVP){
						
					}
					else {
						damageDealt += (int)(5+2*potency);
						targets.get(i).attackEntityFrom(DamageSource.generic, (int)(5+2*potency));
						targets.get(i).setLastAttacker(caster);
						targets.get(i).setRevengeTarget((EntityLivingBase)caster);
						targets.get(i).getEntityData().setDouble(RootsNames.TAG_SPELL_VULNERABILITY, 1.0+0.5*potency);
					}
				}
			}
			if (damageDealt > 80){
				if (caster instanceof EntityPlayer){
					if (!((EntityPlayer)caster).hasAchievement(RegistryManager.achieveLotsDamage)){
						PlayerManager.addAchievement(((EntityPlayer)caster), RegistryManager.achieveLotsDamage);
					}
				}
			}
		}
	}
	
	@Override
	public void doEffect(World world, UUID casterId, Vec3d direction, EnumCastType type, double x, double y, double z, double potency, double duration, double size){
		if (type == EnumCastType.SPELL){
			EntityPlayer player = world.getPlayerEntityByUUID(casterId);
			ArrayList<EntityLivingBase> targets = (ArrayList<EntityLivingBase>) world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-(2.0+size),y-(2.0+size),z-(2.0+size),x+(2.0+size),y+(2.0+size),z+(2.0+size)));
			int damageDealt = 0;
			for (int i = 0; i < targets.size(); i ++){
				if (targets.get(i).getUniqueID() != casterId){
					if (targets.get(i) instanceof EntityPlayer && ConfigManager.disablePVP){
					}
					else {
						damageDealt += (int)(4+2*potency);
						targets.get(i).attackEntityFrom(DamageSource.generic, (int)(5+2*potency));
						if (player != null){
							targets.get(i).attackEntityAsMob(player);
							targets.get(i).setLastAttacker(player);
							targets.get(i).setRevengeTarget((EntityLivingBase)player);
						}
						targets.get(i).getEntityData().setDouble(RootsNames.TAG_SPELL_VULNERABILITY, 1.0+0.5*potency);
					}
				}
			}
			if (damageDealt > 80){
				if (player != null){
					if (!player.hasAchievement(RegistryManager.achieveLotsDamage)){
						PlayerManager.addAchievement(player, RegistryManager.achieveLotsDamage);
					}
				}
			}
		}
	}
}
