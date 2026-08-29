package dev.herobrine.client;

import dev.herobrine.HerobrineMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class HerobrineModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(HerobrineMod.HEROBRINE, HerobrineRenderer::new);
	}
}
