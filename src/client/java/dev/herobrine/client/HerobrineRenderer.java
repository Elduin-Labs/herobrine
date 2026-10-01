package dev.herobrine.client;

import dev.herobrine.HerobrineEntity;
import dev.herobrine.HerobrineMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class HerobrineRenderer extends HumanoidMobRenderer<HerobrineEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
	/** The vanilla default skin, referenced at runtime rather than shipped with the mod. */
	private static final Identifier SKIN = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

	public HerobrineRenderer(EntityRendererProvider.Context context) {
		// the player layer, so the jacket, sleeves and trousers layers show too
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
		this.addLayer(new HerobrineEyesLayer(this));
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public Identifier getTextureLocation(HumanoidRenderState state) {
		return SKIN;
	}

	static Identifier eyesTexture() {
		return Identifier.fromNamespaceAndPath(HerobrineMod.MOD_ID, "textures/entity/herobrine_eyes.png");
	}
}
