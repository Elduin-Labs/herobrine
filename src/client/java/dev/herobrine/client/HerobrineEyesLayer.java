package dev.herobrine.client;

import dev.herobrine.HerobrineEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

/** Draws the white eyes at full brightness so they glow in the dark. */
public class HerobrineEyesLayer extends EyesLayer<HerobrineEntity, PlayerModel<HerobrineEntity>> {
	private static final RenderType EYES = RenderType.eyes(HerobrineRenderer.eyesTexture());

	public HerobrineEyesLayer(RenderLayerParent<HerobrineEntity, PlayerModel<HerobrineEntity>> parent) {
		super(parent);
	}

	@Override
	public RenderType renderType() {
		return EYES;
	}
}
