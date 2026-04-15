# Local Agent Notes

- When changing role mechanics, do not modify the role's opening intro text or the first role-description paragraph (`announcement.goals.*`) unless the user explicitly asks for that text change.
- Prefer updating only detailed skill descriptions, HUD tips, or other narrower text when needed.
- When the user asks to commit changes, never include anything under `tmp/` unless the user explicitly asks for `tmp/` to be committed.
- For bottom-corner client HUD/text overlays, render them above the Simple Voice Chat group avatar HUD by using a verified late `InGameHud` hook and wrapping drawing with `HudRenderHelper.pushAboveVoiceChatHudLayer()` / `popAboveVoiceChatHudLayer()`. Prefer a checked intermediary/described target over raw mapped method names when injecting into Minecraft HUD internals.
- Default all role skill prompt/cooldown text to render in that role's own color. Default the top and bottom accent bars of role skill/menu UIs to that role's own color too, unless the user explicitly asks for a different color treatment.
