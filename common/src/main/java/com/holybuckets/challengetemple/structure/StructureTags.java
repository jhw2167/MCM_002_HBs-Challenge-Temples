package com.holybuckets.challengetemple.structure;

import com.holybuckets.challengetemple.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface StructureTags {
    TagKey<Structure> CHALLENGE_TEMPLE = create("challenge_temple");
    TagKey<Structure> CHALLENGE_ROOM_2x2 = create("challenge_room_2x2");
    TagKey<Structure> CHALLENGE_STRUCTURE = create("challenge_structure");


    private static TagKey<Structure> create(String id) {
        return TagKey.create(Registries.STRUCTURE, new ResourceLocation(Constants.MOD_ID, id));
    }
}
