package org.agmas.noellesroles.client.configscreen;

import net.minecraft.text.Text;

import java.util.List;

public record ConfigCategoryDefinition(
        String id,
        Text title,
        Text description,
        List<ConfigOptionDefinition<?>> options
) {
    public ConfigCategoryDefinition {
        options = List.copyOf(options);
    }
}
