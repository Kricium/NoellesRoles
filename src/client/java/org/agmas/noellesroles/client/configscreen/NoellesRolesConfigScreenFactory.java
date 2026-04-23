package org.agmas.noellesroles.client.configscreen;

import dev.doctor4t.wathe.WatheConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.screen.NoellesRolesConfigScreen;

import java.util.List;

public final class NoellesRolesConfigScreenFactory {
    private NoellesRolesConfigScreenFactory() {
    }

    public static Screen create(Screen parent) {
        return new NoellesRolesConfigScreen(parent, buildCategories());
    }

    public static List<ConfigCategoryDefinition> buildCategories() {
        return List.of(
                new ConfigCategoryDefinition(
                        "client",
                        Text.translatable("config_screen.category.client"),
                        Text.translatable("config_screen.category.client.description"),
                        List.of(
                                ConfigOptionDefinition.enumeration(
                                        "wathe_instinct_mode",
                                        Text.translatable("config_screen.option.wathe_instinct_mode"),
                                        Text.translatable("config_screen.option.wathe_instinct_mode.description"),
                                        List.of(WatheConfig.InstinctModeConfig.values()),
                                        NoellesRolesConfigScreenFactory::getInstinctModeText,
                                        ConfigScreenState::instinctMode,
                                        ConfigScreenState::instinctMode,
                                        (state, value) -> state.instinctMode(value)
                                ),
                                ConfigOptionDefinition.number(
                                        "wathe_chat_history_limit",
                                        Text.translatable("config_screen.option.wathe_chat_history_limit"),
                                        Text.translatable("config_screen.option.wathe_chat_history_limit.description"),
                                        WatheConfig.MIN_CHAT_HISTORY_LIMIT,
                                        WatheConfig.MAX_CHAT_HISTORY_LIMIT,
                                        ConfigScreenState::chatHistoryLimit,
                                        ConfigScreenState::chatHistoryLimit,
                                        (state, value) -> state.chatHistoryLimit(value)
                                ),
                                ConfigOptionDefinition.toggle(
                                        "wathe_show_match_player_count",
                                        Text.translatable("config_screen.option.wathe_show_match_player_count"),
                                        Text.translatable("config_screen.option.wathe_show_match_player_count.description"),
                                        ConfigScreenState::showMatchPlayerCount,
                                        ConfigScreenState::showMatchPlayerCount,
                                        (state, value) -> state.showMatchPlayerCount(value)
                                ),
                                ConfigOptionDefinition.toggle(
                                        "show_assist_interface_hint",
                                        Text.translatable("config_screen.option.show_assist_interface_hint"),
                                        Text.translatable("config_screen.option.show_assist_interface_hint.description"),
                                        state -> state.noellesRolesConfig().showAssistInterfaceHint,
                                        state -> state.noellesRolesConfig().showAssistInterfaceHint,
                                        (state, value) -> state.noellesRolesConfig().showAssistInterfaceHint = value
                                ),
                                ConfigOptionDefinition.toggle(
                                        "show_config_screen_hint",
                                        Text.translatable("config_screen.option.show_config_screen_hint"),
                                        Text.translatable("config_screen.option.show_config_screen_hint.description"),
                                        state -> state.noellesRolesConfig().showConfigScreenHint,
                                        state -> state.noellesRolesConfig().showConfigScreenHint,
                                        (state, value) -> state.noellesRolesConfig().showConfigScreenHint = value
                                )
                        )
                ),
                new ConfigCategoryDefinition(
                        "gameplay",
                        Text.translatable("config_screen.category.gameplay"),
                        Text.translatable("config_screen.category.gameplay.description"),
                        List.of(
                                ConfigOptionDefinition.toggle(
                                        "insane_players_see_morphs",
                                        Text.translatable("config_screen.option.insane_players_see_morphs"),
                                        Text.translatable("config_screen.option.insane_players_see_morphs.description"),
                                        state -> state.noellesRolesConfig().insanePlayersSeeMorphs,
                                        state -> state.noellesRolesConfig().insanePlayersSeeMorphs,
                                        (state, value) -> state.noellesRolesConfig().insanePlayersSeeMorphs = value
                                ),
                                ConfigOptionDefinition.number(
                                        "general_cooldown_ticks",
                                        Text.translatable("config_screen.option.general_cooldown_ticks"),
                                        Text.translatable("config_screen.option.general_cooldown_ticks.description"),
                                        0,
                                        20 * 60 * 10,
                                        state -> state.noellesRolesConfig().generalCooldownTicks,
                                        state -> state.noellesRolesConfig().generalCooldownTicks,
                                        (state, value) -> state.noellesRolesConfig().generalCooldownTicks = value
                                ),
                                ConfigOptionDefinition.toggle(
                                        "voodoo_non_killer_deaths",
                                        Text.translatable("config_screen.option.voodoo_non_killer_deaths"),
                                        Text.translatable("config_screen.option.voodoo_non_killer_deaths.description"),
                                        state -> state.noellesRolesConfig().voodooNonKillerDeaths,
                                        state -> state.noellesRolesConfig().voodooNonKillerDeaths,
                                        (state, value) -> state.noellesRolesConfig().voodooNonKillerDeaths = value
                                )
                        )
                ),
                new ConfigCategoryDefinition(
                        "compatibility",
                        Text.translatable("config_screen.category.compatibility"),
                        Text.translatable("config_screen.category.compatibility.description"),
                        List.of(
                                ConfigOptionDefinition.toggle(
                                        "lock_sound_physics_remastered_config",
                                        Text.translatable("config_screen.option.lock_sound_physics_remastered_config"),
                                        Text.translatable("config_screen.option.lock_sound_physics_remastered_config.description"),
                                        state -> state.noellesRolesConfig().lockSoundPhysicsRemasteredConfig,
                                        state -> state.noellesRolesConfig().lockSoundPhysicsRemasteredConfig,
                                        (state, value) -> state.noellesRolesConfig().lockSoundPhysicsRemasteredConfig = value
                                ),
                                ConfigOptionDefinition.toggle(
                                        "lock_talk_bubbles_config",
                                        Text.translatable("config_screen.option.lock_talk_bubbles_config"),
                                        Text.translatable("config_screen.option.lock_talk_bubbles_config.description"),
                                        state -> state.noellesRolesConfig().lockTalkBubblesConfig,
                                        state -> state.noellesRolesConfig().lockTalkBubblesConfig,
                                        (state, value) -> state.noellesRolesConfig().lockTalkBubblesConfig = value
                                )
                        )
                )
        );
    }

    private static Text getInstinctModeText(WatheConfig.InstinctModeConfig instinctMode) {
        return switch (instinctMode) {
            case HOLD -> Text.translatable("config_screen.enum.wathe_instinct_mode.hold");
            case TOGGLE -> Text.translatable("config_screen.enum.wathe_instinct_mode.toggle");
        };
    }
}
