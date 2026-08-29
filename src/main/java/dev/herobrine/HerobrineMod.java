package dev.herobrine;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class HerobrineMod implements ModInitializer {
	public static final String MOD_ID = "herobrine";

	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<HerobrineEntity> HEROBRINE = Registry.register(
		BuiltInRegistries.ENTITY_TYPE,
		ResourceLocation.fromNamespaceAndPath(MOD_ID, "herobrine"),
		EntityType.Builder.of(HerobrineEntity::new, MobCategory.MONSTER)
			.sized(0.6F, 1.8F)
			.clientTrackingRange(16)
			.build("herobrine")
	);

	/** How often the game rolls for a sighting, in ticks. */
	private static final int CHECK_INTERVAL = 200;

	/** Chance of a sighting per player per roll. */
	private static final float SIGHTING_CHANCE = 0.35F;

	private static final int MIN_DISTANCE = 24;
	private static final int MAX_DISTANCE = 44;

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(HEROBRINE, HerobrineEntity.createAttributes());

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % CHECK_INTERVAL != 0) {
				return;
			}

			for (ServerLevel level : server.getAllLevels()) {
				if (level.dimension() != Level.OVERWORLD || !level.isNight()) {
					continue;
				}

				for (ServerPlayer player : level.players()) {
					if (level.random.nextFloat() < SIGHTING_CHANCE) {
						trySpawnNear(level, player);
					}
				}
			}
		});
	}

	private static void trySpawnNear(ServerLevel level, ServerPlayer player) {
		// Don't stack sightings.
		if (!level.getEntitiesOfClass(HerobrineEntity.class, player.getBoundingBox().inflate(96.0)).isEmpty()) {
			return;
		}

		for (int attempt = 0; attempt < 20; attempt++) {
			double angle = level.random.nextDouble() * Math.PI * 2.0;
			double distance = MIN_DISTANCE + level.random.nextDouble() * (MAX_DISTANCE - MIN_DISTANCE);
			int x = (int)(player.getX() + Math.cos(angle) * distance);
			int z = (int)(player.getZ() + Math.sin(angle) * distance);

			if (!level.isLoaded(new BlockPos(x, level.getMinBuildHeight() + 1, z))) {
				continue;
			}

			BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
			if (!level.getBlockState(ground).isAir() || !level.getBlockState(ground.above()).isAir()) {
				continue;
			}

			// A rooftop speck 40 blocks overhead isn't a sighting; keep him near your own level.
			if (Math.abs(ground.getY() - player.getY()) > 16) {
				continue;
			}

			HerobrineEntity herobrine = HEROBRINE.create(level);
			if (herobrine == null) {
				return;
			}

			herobrine.moveTo(ground.getX() + 0.5, ground.getY(), ground.getZ() + 0.5, 0.0F, 0.0F);
			herobrine.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, player.position());
			herobrine.finalizeSpawn(level, level.getCurrentDifficultyAt(ground), MobSpawnType.EVENT, null);
			level.addFreshEntity(herobrine);
			LOGGER.info("Sighting near {} at {} ({} blocks away)", player.getName().getString(), ground, (int)Math.sqrt(player.distanceToSqr(herobrine)));
			return;
		}
	}
}
