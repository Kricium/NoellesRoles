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
                        Text.literal("客户端"),
                        Text.literal("本地界面与 Wathe 客户端显示相关设置。"),
                        List.of(
                                ConfigOptionDefinition.enumeration(
                                        "wathe_instinct_mode",
                                        Text.literal("本能模式"),
                                        Text.literal("设置 Wathe 的本能键行为：按住生效或按一下切换。"),
                                        List.of(WatheConfig.InstinctModeConfig.values()),
                                        NoellesRolesConfigScreenFactory::getInstinctModeText,
                                        ConfigScreenState::instinctMode,
                                        ConfigScreenState::instinctMode,
                                        (state, value) -> state.instinctMode(value)
                                ),
                                ConfigOptionDefinition.number(
                                        "wathe_chat_history_limit",
                                        Text.literal("聊天栏记录条数"),
                                        Text.literal("设置 Wathe 聊天记录保留上限。"),
                                        WatheConfig.MIN_CHAT_HISTORY_LIMIT,
                                        WatheConfig.MAX_CHAT_HISTORY_LIMIT,
                                        ConfigScreenState::chatHistoryLimit,
                                        ConfigScreenState::chatHistoryLimit,
                                        (state, value) -> state.chatHistoryLimit(value)
                                ),
                                ConfigOptionDefinition.toggle(
                                        "wathe_show_match_player_count",
                                        Text.literal("对局玩家数显示"),
                                        Text.literal("控制是否在 HUD 头部显示当前对局玩家数。"),
                                        ConfigScreenState::showMatchPlayerCount,
                                        ConfigScreenState::showMatchPlayerCount,
                                        (state, value) -> state.showMatchPlayerCount(value)
                                ),
                                ConfigOptionDefinition.toggle(
                                        "show_assist_interface_hint",
                                        Text.literal("显示辅助界面提示"),
                                        Text.literal("控制 `~` 辅助界面的底角提示是否显示。"),
                                        state -> state.noellesRolesConfig().showAssistInterfaceHint,
                                        state -> state.noellesRolesConfig().showAssistInterfaceHint,
                                        (state, value) -> state.noellesRolesConfig().showAssistInterfaceHint = value
                                ),
                                ConfigOptionDefinition.toggle(
                                        "show_config_screen_hint",
                                        Text.literal("显示配置界面提示"),
                                        Text.literal("控制配置界面的底角提示是否显示。"),
                                        state -> state.noellesRolesConfig().showConfigScreenHint,
                                        state -> state.noellesRolesConfig().showConfigScreenHint,
                                        (state, value) -> state.noellesRolesConfig().showConfigScreenHint = value
                                )
                        )
                ),
                new ConfigCategoryDefinition(
                        "gameplay",
                        Text.literal("游戏规则"),
                        Text.literal("会直接影响玩法体验的核心开关。"),
                        List.of(
                                ConfigOptionDefinition.toggle(
                                        "insane_players_see_morphs",
                                        Text.literal("疯魔错觉"),
                                        Text.literal("让疯魔玩家随机把其他人看成变形后的样子。"),
                                        state -> state.noellesRolesConfig().insanePlayersSeeMorphs,
                                        state -> state.noellesRolesConfig().insanePlayersSeeMorphs,
                                        (state, value) -> state.noellesRolesConfig().insanePlayersSeeMorphs = value
                                ),
                                ConfigOptionDefinition.number(
                                        "general_cooldown_ticks",
                                        Text.literal("通用冷却"),
                                        Text.literal("统一技能冷却，单位为 ticks。"),
                                        0,
                                        20 * 60 * 10,
                                        state -> state.noellesRolesConfig().generalCooldownTicks,
                                        state -> state.noellesRolesConfig().generalCooldownTicks,
                                        (state, value) -> state.noellesRolesConfig().generalCooldownTicks = value
                                ),
                                ConfigOptionDefinition.toggle(
                                        "voodoo_non_killer_deaths",
                                        Text.literal("自然死亡触发巫蛊"),
                                        Text.literal("允许没有明确击杀者的死亡也触发巫蛊效果。"),
                                        state -> state.noellesRolesConfig().voodooNonKillerDeaths,
                                        state -> state.noellesRolesConfig().voodooNonKillerDeaths,
                                        (state, value) -> state.noellesRolesConfig().voodooNonKillerDeaths = value
                                )
                        )
                ),
                new ConfigCategoryDefinition(
                        "compatibility",
                        Text.literal("联机兼容"),
                        Text.literal("和外部模组配置联动的安全锁定项。"),
                        List.of(
                                ConfigOptionDefinition.toggle(
                                        "lock_sound_physics_remastered_config",
                                        Text.literal("锁定 Sound Physics"),
                                        Text.literal("进入服务器后，自动把 Sound Physics Remastered 配置锁成服务端要求的值。"),
                                        state -> state.noellesRolesConfig().lockSoundPhysicsRemasteredConfig,
                                        state -> state.noellesRolesConfig().lockSoundPhysicsRemasteredConfig,
                                        (state, value) -> state.noellesRolesConfig().lockSoundPhysicsRemasteredConfig = value
                                ),
                                ConfigOptionDefinition.toggle(
                                        "lock_talk_bubbles_config",
                                        Text.literal("锁定 TalkBubbles"),
                                        Text.literal("进入服务器后，自动把 TalkBubbles 配置锁成服务端要求的值。"),
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
            case HOLD -> Text.literal("按住");
            case TOGGLE -> Text.literal("切换");
        };
    }
}
