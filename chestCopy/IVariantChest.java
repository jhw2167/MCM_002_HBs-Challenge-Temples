package org.violetmoon.quark.content.building.module;

public interface IVariantChest {
    String getTexturePath();

    default String getTextureFolder() {
        return "quark_variant_chests";
    }
}
