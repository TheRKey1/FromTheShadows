package net.sonmok14.fromtheshadows.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class NehemothEntity extends Monster implements Enemy, IAnimatable, IAnimationTickable {
    private static final Predicate<Entity> NO_NEHEMOTH_AND_ALIVE = (p_33346_) -> {
        return p_33346_.isAlive() && !(p_33346_ instanceof NehemothEntity);
    };
    public int attackID;
    public int attacktick;
    public static final byte MELEE_ATTACK = 1;
    public static final byte BITE_ATTACK = 2;
    public static final byte ROAR_ATTACK = 3;
    public static final byte SMASH_ATTACK = 4;
    public static final byte BLINK_ATTACK = 5;

    public NehemothEntity(EntityType<? extends NehemothEntity> type, Level world) {
        super(type, world);
        maxUpStep = 3;
        xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 10D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 5.0D);
    }


    //animation

    private AnimationFactory factory = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && isAggressive() && attackID == 0) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.run", true));
            return PlayState.CONTINUE;
        }
        if (attackID == 1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.meleeattack", true));
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.walk", true));
            return PlayState.CONTINUE;
        }
        if(!event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.dracan.ldle", true));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }



    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<NehemothEntity>(this, "controller", 5, this::predicate));
    }

    @Override
    public boolean doHurtTarget(Entity p_85031_1_) {
        if (!this.level.isClientSide && this.attackID == 0) {
            if (this.random.nextInt(4) != 0) {
                this.attackID = MELEE_ATTACK;
            } else {
                this.attackID = MELEE_ATTACK;
            }
        }
        return true;
    }


    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public int tickTimer() {
        return 0;
    }

    //animation


    //ai

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (p_199899_) -> {
            return !p_199899_.isBaby();
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.3D));
    }

    public void setAttackID(int id) {
        this.attackID = id;
        this.attacktick = 0;
        this.level.broadcastEntityEvent(this, (byte) -id);
    }



    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 0) {
            this.attackID = Math.abs(id);
            this.attacktick = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    //ai


    //tick

    @Override
    public void tick() {
        super.tick();
        if (this.attackID != 0) {
            ++this.attacktick;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_, MobSpawnType p_21436_, @Nullable SpawnGroupData p_21437_, @Nullable CompoundTag p_21438_) {
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
    }

    @Override
    protected int calculateFallDamage(float p_21237_, float p_21238_) {
        return 0;
    }

    private void meleeattack() {
        float range = 4f;
        float arc = 60;
        List<LivingEntity> entitiesHit = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0D), NO_NEHEMOTH_AND_ALIVE);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yHeadRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) && (entityRelativeAngle >= 360 - arc / 2 == entityRelativeAngle <= -360 + arc / 2)) {
                if (!(entityHit instanceof NehemothEntity)) {
                    entityHit.invulnerableTime = 0;
                    entityHit.hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }

            }
        }
    }

    //tick

    //advanced ai

    class MeleeAttackGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public MeleeAttackGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == MELEE_ATTACK;
        }

        public void start() {
            this.nehemoth.setAttackID(NehemothEntity.MELEE_ATTACK);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 25;
        }


        public void tick() {
            double dist = (double) this.nehemoth.distanceTo(attackTarget);
            if (nehemoth.attacktick < 25 && attackTarget.isAlive()) {
                nehemoth.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }

            if (nehemoth.attacktick == 11) {
                meleeattack();
                attackTarget.invulnerableTime = 0;

            }
            if (nehemoth.attacktick == 22) {
               meleeattack();
                attackTarget.invulnerableTime = 0;
            }

        }
    }

    }
