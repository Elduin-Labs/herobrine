package dev.herobrine;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * He watches. He does not approach, and he does not let you approach either:
 * look at him too long or walk toward him and he is simply gone.
 */
public class HerobrineEntity extends PathfinderMob {
	/** He leaves once a player is this close. */
	private static final double FLEE_DISTANCE = 10.0;

	/** Ticks a player may stare before he vanishes. */
	private static final int STARE_TOLERANCE = 45;

	/** He never lingers longer than this. */
	private static final int LIFETIME = 1200;

	private int staredAt;
	private int age;

	public HerobrineEntity(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.setPersistenceRequired();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 20.0)
			.add(Attributes.MOVEMENT_SPEED, 0.0)
			.add(Attributes.FOLLOW_RANGE, 64.0);
	}

	@Override
	protected void registerGoals() {
		// He only ever turns to face you.
		this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 64.0F, 1.0F));
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		// You cannot fight him. Attacking just makes him leave.
		this.vanish(level);
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void doPush(net.minecraft.world.entity.Entity entity) {
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	public void tick() {
		super.tick();

		if (!(this.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		this.age++;
		if (this.age > LIFETIME) {
			this.vanish(serverLevel);
			return;
		}

		Player player = serverLevel.getNearestPlayer(this, 64.0);
		if (player == null) {
			this.vanish(serverLevel);
			return;
		}

		if (this.distanceTo(player) < FLEE_DISTANCE) {
			this.vanish(serverLevel);
			return;
		}

		if (this.isLookedAtBy(player)) {
			this.staredAt++;
			if (this.staredAt > STARE_TOLERANCE) {
				this.vanish(serverLevel);
			}
		} else {
			this.staredAt = 0;
		}
	}

	/** True when the player's crosshair is roughly on him and nothing solid is between. */
	private boolean isLookedAtBy(LivingEntity viewer) {
		Vec3 look = viewer.getViewVector(1.0F).normalize();
		Vec3 toMe = new Vec3(this.getX() - viewer.getX(), this.getEyeY() - viewer.getEyeY(), this.getZ() - viewer.getZ());
		double alignment = look.dot(toMe.normalize());
		return alignment > 0.97 && viewer.hasLineOfSight(this);
	}

	private void vanish(ServerLevel level) {
		level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 12, 0.2, 0.5, 0.2, 0.01);
		level.playSound(null, this.blockPosition(), SoundEvents.AMBIENT_CAVE.value(), SoundSource.HOSTILE, 0.6F, 0.6F);
		this.discard();
	}

	@Override
	public boolean isSilent() {
		return true;
	}
}
