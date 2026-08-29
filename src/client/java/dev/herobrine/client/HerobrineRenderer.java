package dev.herobrine.client;

import dev.herobrine.HerobrineEntity;
import dev.herobrine.HerobrineMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class HerobrineRenderer extends LivingEntityRenderer<HerobrineEntity, PlayerModel<HerobrineEntity>> {
	/** The vanilla default skin, referenced at runtime rather than shipped with the mod. */
	private static final ResourceLocation SKIN = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

	public HerobrineRenderer(EntityRendererProvider.Context context) {
		super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
		this.addLayer(new HerobrineEyesLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(HerobrineEntity entity) {
		return SKIN;
	}

	static ResourceLocation eyesTexture() {
		return ResourceLocation.fromNamespaceAndPath(HerobrineMod.MOD_ID, "textures/entity/herobrine_eyes.png");
	}
}
