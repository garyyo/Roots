package elucent.roots.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.util.text.TextFormatting;

import elucent.roots.Roots;
import elucent.roots.RegistryManager;
import elucent.roots.Util;
import elucent.roots.component.ComponentBase;
import elucent.roots.component.ComponentManager;
import elucent.roots.component.EnumCastType;
import elucent.roots.entity.EntitySpritePlacator;
import elucent.roots.entity.ISprite;
import elucent.roots.item.DustPetal;
import elucent.roots.item.ItemStaff;
import elucent.roots.ritual.RitualBase;
import elucent.roots.ritual.RitualManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TileEntitySpiritFont extends TEBase implements ITickable {
	int ticker = 0;
	Random random = new Random();
	float angle = 0;
	float power = 0;
	float totalPower = 0;
	public TileEntitySpiritFont(){
		super();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		return tag;
	}

	@Override
	public void update() {
		angle += 24.0f;
		ticker ++;
		if (ticker % 2 == 0){
			Roots.proxy.spawnParticleMagicSmallSparkleFX(getWorld(), getPos().getX()+0.125+random.nextFloat()*0.75, getPos().getY()+0.375+random.nextFloat()*0.75,getPos().getZ()+0.125+random.nextFloat()*0.75, 0, 0, 0, 107, 255, 28);
		}
		if (ticker % 100 == 0){
			float sum = 0;
			for (int i = -5; i < 6; i ++){
				for (int j = -5; j < 6; j ++){
					BlockPos highest = getWorld().getTopSolidOrLiquidBlock(getPos().add(i,0,j));
					if (getWorld().getBlockState(highest.up()).getBlock() != Blocks.AIR){
						highest.add(0,1,0);
					}
					sum += Util.getNatureAmount(getWorld().getBlockState(highest)); 
				}
			}
			this.power = sum/50.0f;
			sum = 0;
			for (int i = -5; i < 6; i ++){
				for (int j = -5; j < 6; j ++){
					BlockPos pos = getPos().down().add(i,0,j);
					if (getWorld().getBlockState(pos.down()).getBlock() == Blocks.WATER){
						sum += 1.0f;
					}
				}
			}

			totalPower = (float)Math.pow(sum,1.5)*0.0625f;
			float powerMirror = totalPower;
			List<EntityLivingBase> entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos().getX()-10.0,getPos().getY()-15.0,getPos().getZ()-10.0,getPos().getX()+11.0,getPos().getY()+16.0,getPos().getZ()+11.0));
			for (int i = 0; i < entities.size(); i ++){
				if (entities.get(i) instanceof ISprite && powerMirror > 0 && random.nextInt(2) == 0){
					if (!getWorld().isRemote){
						EntitySpritePlacator p = new EntitySpritePlacator(getWorld());
						if (powerMirror >= power){
							p.initSpecial(entities.get(i), power, getPos());
							powerMirror -= power;
						}
						else {
							p.initSpecial(entities.get(i), powerMirror, getPos());
							powerMirror = 0;
						}
						p.setPosition(getPos().getX()+0.5, getPos().getY()+0.5, getPos().getZ()+0.5);
						getWorld().spawnEntityInWorld(p);
					}
				}
			}
		}
	}
}
