package elucent.roots.entity;

import java.util.Random;

import java.util.List;
import elucent.roots.Roots;
import elucent.roots.Util;
import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityGreaterSprite  extends EntityFlying implements ISprite {// implements IRangedAttackMob {
    public float range = 64;
    public static final DataParameter<Float> targetDirectionX = EntityDataManager.<Float>createKey(EntityGreaterSprite.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> targetDirectionY = EntityDataManager.<Float>createKey(EntityGreaterSprite.class, DataSerializers.FLOAT);
    public static final DataParameter<Integer> dashTimer = EntityDataManager.<Integer>createKey(EntityGreaterSprite.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> shootTimer = EntityDataManager.<Integer>createKey(EntityGreaterSprite.class, DataSerializers.VARINT);
    public static final DataParameter<Float> happiness = EntityDataManager.<Float>createKey(EntityGreaterSprite.class, DataSerializers.FLOAT);
    public static final DataParameter<Boolean> stunned = EntityDataManager.<Boolean>createKey(EntityGreaterSprite.class, DataSerializers.BOOLEAN);
    public static final DataParameter<BlockPos> targetBlock = EntityDataManager.<BlockPos>createKey(EntityGreaterSprite.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<BlockPos> lastTargetBlock = EntityDataManager.<BlockPos>createKey(EntityGreaterSprite.class, DataSerializers.BLOCK_POS);
    public float addDirectionX = 0;
    public float addDirectionY = 0;
    public float twirlTimer = 0;
    public float prevYaw1 = 0;
    public float prevYaw2 = 0;
    public float prevYaw3 = 0;
    public float prevYaw4 = 0;
    public float prevYaw5 = 0;
    public float prevPitch1 = 0;
    public float prevPitch2 = 0;
    public float prevPitch3 = 0;
    public float prevPitch4 = 0;
    public float prevPitch5 = 0;
    Random random = new Random();
    
    public SoundEvent ambientSound;
    public SoundEvent hurtSound;

    public EntityGreaterSprite(World worldIn) {
    	super(worldIn);
        setSize(1.0f, 1.0f);
        this.isAirBorne = true;
		this.experienceValue = 20;
		ambientSound = new SoundEvent(new ResourceLocation("roots:spiritAmbient"));
		hurtSound = new SoundEvent(new ResourceLocation("roots:spiritHurt"));
    }
    
    @Override
    protected void entityInit(){
    	super.entityInit();
        this.getDataManager().register(targetDirectionX, Float.valueOf(0));
        this.getDataManager().register(targetDirectionY, Float.valueOf(0));
        this.getDataManager().register(shootTimer, Integer.valueOf(0));
        this.getDataManager().register(dashTimer, Integer.valueOf(0));
        this.getDataManager().register(happiness, Float.valueOf(-5));
        this.getDataManager().register(stunned, Boolean.valueOf(false));
        this.getDataManager().register(targetBlock, new BlockPos(0,-1,0));
        this.getDataManager().register(lastTargetBlock, new BlockPos(0,-1,0));
    }
    
    @Override
    public void collideWithEntity(Entity entity){
    	if (this.getAttackTarget() != null && this.getHealth() > 0 && !getDataManager().get(stunned).booleanValue()){
    		if (entity.getUniqueID().compareTo(this.getAttackTarget().getUniqueID()) == 0){
    			((EntityLivingBase)entity).attackEntityFrom(DamageSource.generic, 7.0f);
    			float magnitude = (float)Math.sqrt(motionX*motionX+motionZ*motionZ);
    			((EntityLivingBase)entity).knockBack(this, 4.0f*magnitude, -motionX/magnitude, -motionZ/magnitude);
    			((EntityLivingBase)entity).attackEntityAsMob(this);
    			((EntityLivingBase)entity).setRevengeTarget(this);
    		}
    	}
    }
    
    @Override
    public void updateAITasks(){
    	super.updateAITasks();
    }
    
    @Override
    public void onUpdate(){
    	super.onUpdate();
    	prevYaw5 = prevYaw4;
    	prevYaw4 = prevYaw3;
    	prevYaw3 = prevYaw2;
    	prevYaw2 = prevYaw1;
    	prevYaw1 = rotationYaw;

    	prevPitch5 = prevPitch4;
    	prevPitch4 = prevPitch3;
    	prevPitch3 = prevPitch2;
    	prevPitch2 = prevPitch1;
    	prevPitch1 = rotationPitch;
    	
    	if (!getDataManager().get(stunned).booleanValue()){
		    if (this.ticksExisted % 20 == 0){
		    	if (random.nextInt(7) == 0 && this.getDataManager().get(stunned).booleanValue() == false){
		    		getEntityWorld().playSound(posX, posY, posZ, ambientSound, SoundCategory.NEUTRAL, random.nextFloat()*0.1f+0.95f, random.nextFloat()*0.1f+0.7f, false);
		    	}
		    }
	    	
	    	if (twirlTimer > 0){
	    		twirlTimer -= 1.0f;
	    	}
	    	if (getDataManager().get(dashTimer) > 0){
	    		getDataManager().set(dashTimer, getDataManager().get(dashTimer)-1);
	    		getDataManager().setDirty(dashTimer);
	    	}
	    	if (getDataManager().get(shootTimer) > 0){
	    		getDataManager().set(shootTimer, getDataManager().get(shootTimer)-1);
	    		getDataManager().setDirty(shootTimer);
	    		if (!getEntityWorld().isRemote && getDataManager().get(shootTimer) == 0 || getDataManager().get(shootTimer) == 10 || getDataManager().get(shootTimer) == 20){
	    			EntitySpriteProjectile proj = new EntitySpriteProjectile(getEntityWorld());
	    			proj.setPosition(posX, posY, posZ);
	    			proj.onInitialSpawn(getEntityWorld().getDifficultyForLocation(getPosition()), null);
	    			proj.setVelocity(random.nextDouble()-0.5, random.nextDouble()-0.5, random.nextDouble()-0.5);
	    			proj.initSpecial(getAttackTarget(), 4.0f);
	    			getEntityWorld().spawnEntityInWorld(proj);
	    			getEntityWorld().playSound(posX, posY, posZ, new SoundEvent(new ResourceLocation("roots:staffCast")), SoundCategory.HOSTILE, 0.95f+random.nextFloat()*0.1f, 0.7f+random.nextFloat()*0.1f, false);
					for (int i = 0; i < 40; i ++){
						Roots.proxy.spawnParticleMagicSparkleFX(getEntityWorld(), posX, posY+height/2.0f, posZ, Math.pow(1.15f*(random.nextFloat()-0.5f),3.0), Math.pow(1.15f*(random.nextFloat()-0.5f),3.0), Math.pow(1.15f*(random.nextFloat()-0.5f),3.0), 107, 255, 28);
					}
	    		}
	    	}
	    	if (this.getAttackTarget() != null && !this.getEntityWorld().isRemote){
	    		if (getDataManager().get(targetBlock).getY() != -1){
	    			getDataManager().set(targetBlock,new BlockPos(0,-1,0));
	    			getDataManager().setDirty(targetBlock);
	    		}
	    		if (getDataManager().get(dashTimer) <= 0){
	    			this.getDataManager().set(targetDirectionX, (float)Math.toRadians(Util.yawDegreesBetweenPoints(posX, posY, posZ, getAttackTarget().posX, getAttackTarget().posY+getAttackTarget().getEyeHeight()/2.0, getAttackTarget().posZ)));
	    			this.getDataManager().set(targetDirectionY, (float)Math.toRadians(Util.pitchDegreesBetweenPoints(posX, posY, posZ, getAttackTarget().posX, getAttackTarget().posY+getAttackTarget().getEyeHeight()/2.0, getAttackTarget().posZ)));
	    			this.getDataManager().setDirty(targetDirectionX);
	    		 	this.getDataManager().setDirty(targetDirectionY);
	    		}
	    		if (this.ticksExisted % 20 == 0 && random.nextInt(8) == 0 && getDataManager().get(dashTimer) <= 0){
	    			getDataManager().set(shootTimer, 50);
	        		getDataManager().setDirty(shootTimer);
	    		}
	    		if (this.ticksExisted % 20 == 0 && random.nextInt(3) == 0 && getDataManager().get(shootTimer) <= 0){
	    			getDataManager().set(dashTimer, 20);
	        		getDataManager().setDirty(dashTimer);
	    			twirlTimer = 20;
	    		}
	    	}
	    	else if (getDataManager().get(targetBlock).getY() != -1){
	    		if (this.ticksExisted % 50 == 0 && !this.getEntityWorld().isRemote){
	    			Vec3d target = new Vec3d(getDataManager().get(targetBlock).getX()+0.5+(random.nextFloat()-0.5f)*7.0f,getDataManager().get(targetBlock).getY()+9.0+(random.nextFloat()-0.5f)*17.0f,getDataManager().get(targetBlock).getZ()+0.5+(random.nextFloat()-0.5f)*7.0f);
	    			this.getDataManager().set(targetDirectionX, (float)Math.toRadians(Util.yawDegreesBetweenPoints(posX, posY, posZ, target.xCoord, target.yCoord, target.zCoord)));
	    			this.getDataManager().set(targetDirectionY, (float)Math.toRadians(Util.pitchDegreesBetweenPoints(posX, posY, posZ, target.xCoord, target.yCoord, target.zCoord)));
	    			this.getDataManager().setDirty(targetDirectionX);
	    		 	this.getDataManager().setDirty(targetDirectionY);
		    	}
	    	}
	    	else {
	    		if (this.ticksExisted % 50 == 0 && !this.getEntityWorld().isRemote){
	    			this.getDataManager().set(targetDirectionX, (float)Math.toRadians(random.nextFloat()*360.0f));
	    			this.getDataManager().set(targetDirectionY, (float)Math.toRadians(random.nextFloat()*180.0f-90.0f));
	    		 	this.getDataManager().setDirty(targetDirectionX);
	    		 	this.getDataManager().setDirty(targetDirectionY);
	    	    }
	    	}
			addDirectionX = (float) (addDirectionX*0.9f+0.1f*getDataManager().get(targetDirectionX));
			addDirectionY = (float) (addDirectionY*0.9f+0.1f*getDataManager().get(targetDirectionY));
			this.rotationYaw = (rotationYaw*0.9f+addDirectionX*0.1f);
			this.rotationPitch = (rotationPitch*0.9f+addDirectionY*0.1f);
			Vec3d moveVec = Util.lookVector(this.rotationYaw,this.rotationPitch - 0.05f*(float)Math.toRadians(4.0f*(posY-(getEntityWorld().getTopSolidOrLiquidBlock(getPosition()).getY()+1.5))))
					.scale(getAttackTarget() != null ? (getDataManager().get(shootTimer) > 0 ? 0.03 : (getDataManager().get(dashTimer) > 0 ? 0.4 : 0.2)) : 0.1);
			this.setVelocity(0.5f*motionX+0.5f*moveVec.xCoord,0.5f*motionY+0.5f*moveVec.yCoord,0.5f*motionZ+0.5f*moveVec.zCoord);
			if (getDataManager().get(dashTimer) > 0){
				Roots.proxy.spawnParticleMagicSparkleFX(getEntityWorld(), posX+((random.nextDouble())-0.5), posY+0.25+((random.nextDouble())-0.5), posZ+((random.nextDouble())-0.5), -0.25*moveVec.xCoord, -0.25*moveVec.yCoord, -0.25*moveVec.zCoord, 76, 230, 0);
			}
			if (getDataManager().get(happiness) < -25 && this.ticksExisted % 20 == 0 && this.getAttackTarget() == null){
				List<EntityPlayer> players = (List<EntityPlayer>) getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(posX-16.0,posY-16.0,posZ-16.0,posX+16.0,posY+16.0,posZ+16.0));
				if (players.size() > 0){
					this.setAttackTarget(players.get(0));
				}
			}
    	}
    	else {
    		if (this.getHealth() > 0.75f*getMaxHealth()){
    			getDataManager().set(stunned, false);
    			getDataManager().setDirty(stunned);
    		}
    		this.rotationPitch *= 0.9;
    		this.setVelocity(motionX*0.9, -0.05, motionZ*0.9);
    	}
    	getDataManager().set(happiness,(getDataManager().get(happiness)*0.999f));
    	getDataManager().setDirty(happiness);
    }
    
    @Override
    public int getBrightnessForRender(float partialTicks){
    	if (getDataManager().get(stunned).booleanValue()){
    		return 128;
    	}
    	return 255;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount){
    	getEntityWorld().playSound(posX, posY, posZ, hurtSound, SoundCategory.NEUTRAL, random.nextFloat()*0.1f+0.95f, random.nextFloat()*0.1f+0.7f, false);
    	getDataManager().set(happiness, getDataManager().get(happiness)-5);
    	this.getDataManager().setDirty(happiness);
		if (source.getEntity() instanceof EntityLivingBase && getDataManager().get(happiness).intValue() <= -5){
    		this.setAttackTarget((EntityLivingBase)source.getEntity());
    	}
    	return super.attackEntityFrom(source, amount);
    }
    
    @Override
    public void damageEntity(DamageSource source, float amount){
    	if (this.getHealth() - amount <= 0 && !getDataManager().get(stunned).booleanValue()){
    		this.setHealth(1);
    		getDataManager().set(stunned, true);
    		getDataManager().setDirty(stunned);
    	}
    	else {
    		super.damageEntity(source, amount);
    	}
    }
    
    @Override
    public boolean attackEntityAsMob(Entity entity){
    	if (entity instanceof EntityLivingBase && getDataManager().get(happiness).intValue() <= -5){
    		this.setAttackTarget((EntityLivingBase)entity);
    	}
    	return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }
    
    @Override
    public void setDead(){
    	super.setDead();
    	getEntityWorld().playSound(posX, posY, posZ, hurtSound, SoundCategory.NEUTRAL, random.nextFloat()*0.1f+0.95f, (random.nextFloat()*0.1f+0.7f)/2.0f, false);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		getDataManager().set(targetDirectionX, compound.getFloat("targetDirectionX"));
		getDataManager().set(targetDirectionY, compound.getFloat("targetDirectionY"));
		getDataManager().set(dashTimer, compound.getInteger("dashTimer"));
		getDataManager().set(shootTimer, compound.getInteger("shootTimer"));
		getDataManager().set(happiness, compound.getFloat("happiness"));
		getDataManager().set(stunned, compound.getBoolean("stunned"));
		getDataManager().set(targetBlock, new BlockPos(compound.getInteger("targetBlockX"),compound.getInteger("targetBlockY"),compound.getInteger("targetBlockZ")));
		getDataManager().set(lastTargetBlock, new BlockPos(compound.getInteger("lastTargetBlockX"),compound.getInteger("lastTargetBlockY"),compound.getInteger("lastTargetBlockZ")));
		getDataManager().setDirty(targetDirectionX);
		getDataManager().setDirty(targetDirectionY);
		getDataManager().setDirty(dashTimer);
		getDataManager().setDirty(shootTimer);
		getDataManager().setDirty(happiness);
		getDataManager().setDirty(stunned);
		getDataManager().setDirty(targetBlock);
		getDataManager().setDirty(lastTargetBlock);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setFloat("targetDirectionX", getDataManager().get(targetDirectionX));
		compound.setFloat("targetDirectionY", getDataManager().get(targetDirectionY));
		compound.setInteger("dashTimer", getDataManager().get(dashTimer));
		compound.setInteger("shootTimer", getDataManager().get(shootTimer));
		compound.setFloat("happiness", getDataManager().get(happiness));
		compound.setBoolean("stunned", getDataManager().get(stunned));
		compound.setInteger("targetBlockX", getDataManager().get(targetBlock).getX());
		compound.setInteger("targetBlockY", getDataManager().get(targetBlock).getY());
		compound.setInteger("targetBlockZ", getDataManager().get(targetBlock).getZ());
		compound.setInteger("lastTargetBlockX", getDataManager().get(lastTargetBlock).getX());
		compound.setInteger("lastTargetBlockY", getDataManager().get(lastTargetBlock).getY());
		compound.setInteger("lastTargetBlockZ", getDataManager().get(lastTargetBlock).getZ());
	}

	@Override
	public float getHappiness() {
		return getDataManager().get(happiness).floatValue();
	}

	@Override
	public void setHappiness(float value) {
		getDataManager().set(happiness, value);
		getDataManager().setDirty(happiness);
	}
	
	@Override
	public void setTargetPosition(BlockPos pos){
		if (!pos.equals(getDataManager().get(lastTargetBlock)) && !pos.equals(getDataManager().get(targetBlock))){
			getDataManager().set(lastTargetBlock, getDataManager().get(targetBlock));
			getDataManager().setDirty(lastTargetBlock);
			getDataManager().set(targetBlock, pos);
			getDataManager().setDirty(targetBlock);
		}
	}
}