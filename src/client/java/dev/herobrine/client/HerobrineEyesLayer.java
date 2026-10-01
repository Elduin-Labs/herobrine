package dev.herobrine.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/** Draws the white eyes at full brightness so they glow in the dark. */
public class HerobrineEyesLayer extends EyesLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
	private static final RenderType EYES = RenderTypes.eyes(HerobrineRenderer.eyesTexture());

	public HerobrineEyesLayer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> parent) {
		super(parent);
	}

	@Override
	public RenderType renderType() {
		return EYES;
	}
}
