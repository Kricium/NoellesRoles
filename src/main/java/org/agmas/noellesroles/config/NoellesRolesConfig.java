package org.agmas.noellesroles.config;

import dev.doctor4t.wathe.game.GameConstants;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NoellesRolesConfig {
    public static ConfigClassHandler<NoellesRolesConfig> HANDLER = ConfigClassHandler.createBuilder(NoellesRolesConfig.class)
            .id(Identifier.of(Noellesroles.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve( Noellesroles.MOD_ID + ".json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "Whether insane players will randomly see people as morphed.")
    public boolean insanePlayersSeeMorphs = true;

    @SerialEntry(comment = "Starting cooldown (in ticks)")
    public int generalCooldownTicks = GameConstants.getInTicks(0,30);

    @SerialEntry(comment = "Allow Natural deaths to trigger voodoo (deaths without an assigned killer)")
    public boolean voodooNonKillerDeaths = false;

    @SerialEntry(comment = "Lock Sound Physics Remastered config to the server-defined values while connected")
    public boolean lockSoundPhysicsRemasteredConfig = true;

    @SerialEntry(comment = "Server-enforced Sound Physics Remastered values. Keys must match Sound Physics config entry field names.")
    public Map<String, String> soundPhysicsRemasteredLockedValues = new LinkedHashMap<>(Map.ofEntries(
            Map.entry("enabled", "true"),
            Map.entry("attenuationFactor", "1.0"),
            Map.entry("reverbAttenuationDistance", "0.2"),
            Map.entry("reverbGain", "1.0"),
            Map.entry("reverbBrightness", "1.0"),
            Map.entry("reverbDistance", "1.0"),
            Map.entry("blockAbsorption", "4.0"),
            Map.entry("occlusionVariation", "0.15"),
            Map.entry("defaultBlockReflectivity", "0.5"),
            Map.entry("defaultBlockOcclusionFactor", "1.0"),
            Map.entry("soundDistanceAllowance", "4.0"),
            Map.entry("airAbsorption", "1.0"),
            Map.entry("underwaterFilter", "0.8"),
            Map.entry("evaluateAmbientSounds", "true"),
            Map.entry("environmentEvaluationRayCount", "224"),
            Map.entry("environmentEvaluationRayBounces", "8"),
            Map.entry("nonFullBlockOcclusionFactor", "0.25"),
            Map.entry("maxOcclusionRays", "16"),
            Map.entry("maxOcclusion", "10.0"),
            Map.entry("strictOcclusion", "false"),
            Map.entry("soundDirectionEvaluation", "true"),
            Map.entry("redirectNonOccludedSounds", "true"),
            Map.entry("updateMovingSounds", "true"),
            Map.entry("soundUpdateInterval", "5"),
            Map.entry("maxSoundProcessingDistance", "256.0"),
            Map.entry("unsafeLevelAccess", "false"),
            Map.entry("levelCloneRange", "4"),
            Map.entry("levelCloneMaxRetainTicks", "20"),
            Map.entry("levelCloneMaxRetainBlockDistance", "16"),
            Map.entry("debugLogging", "false"),
            Map.entry("occlusionLogging", "false"),
            Map.entry("environmentLogging", "false"),
            Map.entry("performanceLogging", "false"),
            Map.entry("renderSoundBounces", "false"),
            Map.entry("renderOcclusion", "false"),
            Map.entry("simpleVoiceChatIntegration", "true"),
            Map.entry("hearSelf", "false")
    ));

    @SerialEntry(comment = "Lock TalkBubbles config to the server-defined values while connected")
    public boolean lockTalkBubblesConfig = true;

    @SerialEntry(comment = "Server-enforced TalkBubbles values. Keys must match TalkBubbles config field names.")
    public Map<String, String> talkBubblesLockedValues = new LinkedHashMap<>(Map.ofEntries(
            Map.entry("chatRange", "30.0"),
            Map.entry("chatTime", "200"),
            Map.entry("maxChatWidth", "180"),
            Map.entry("chatColor", "1315860"),
            Map.entry("chatHeight", "-0.5"),
            Map.entry("chatScale", "1.0"),
            Map.entry("backgroundOpacity", "0.699999988079071"),
            Map.entry("backgroundRed", "1.0"),
            Map.entry("backgroundGreen", "1.0"),
            Map.entry("backgroundBlue", "1.0"),
            Map.entry("maxUUIDWordCheck", "0"),
            Map.entry("showOwnBubble", "true")
    ));
}
