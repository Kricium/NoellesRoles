package org.agmas.noellesroles.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;

public final class HallucinationFakeBodyRenderer {
    private HallucinationFakeBodyRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(HallucinationFakeBodyRenderer::renderBodies);
    }

    private static void renderBodies(WorldRenderContext context) {
        ClientHallucinationState.renderFakeBodies(context);
    }
}
