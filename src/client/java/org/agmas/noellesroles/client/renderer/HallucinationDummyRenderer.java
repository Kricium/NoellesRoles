package org.agmas.noellesroles.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;

public final class HallucinationDummyRenderer {
    private HallucinationDummyRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(ClientHallucinationState::render);
    }
}
