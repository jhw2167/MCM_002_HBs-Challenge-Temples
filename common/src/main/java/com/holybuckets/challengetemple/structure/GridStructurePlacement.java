package com.holybuckets.challengetemple.structure;


import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.model.ManagedChunkUtility;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import org.jetbrains.annotations.NotNull;

public class GridStructurePlacement extends StructurePlacement {

    static Map<ResourceLocation, GridStructurePlacement> USE_GRID;
    final static int X_SPACING = 8;
    final static int Z_SPACING = 8;
    final static float FREQUENCY = 1.0F;
    static {
        USE_GRID = new HashMap<>();
        ResourceLocation templeLoc = new ResourceLocation("hbs_challenge_temple", "challenge_temple");
        USE_GRID.put(templeLoc, new GridStructurePlacement(1, X_SPACING, Z_SPACING, 526287558));
        ResourceLocation templeRoom = new ResourceLocation("hbs_challenge_temple", "challenge_room_2x2");
        USE_GRID.put(templeRoom, new GridStructurePlacement(1, X_SPACING, Z_SPACING, 526287558));
    }

    //Valid ints range to 2^24, so you can have singleton placements by setting spacing really high.
    public static final Codec<GridStructurePlacement> CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec(instance ->
        placementCodec(instance).and(instance.group(
            Codec.intRange(0, 16777216).fieldOf("spacing").forGetter(GridStructurePlacement::spacing),
            Codec.intRange(0, 16777216).fieldOf("x_offset").forGetter(GridStructurePlacement::xOffset),
            Codec.intRange(0, 16777216).fieldOf("z_offset").forGetter(GridStructurePlacement::zOffset))
        ).apply(instance, GridStructurePlacement::new)), GridStructurePlacement::validate).codec();

    private final int spacing;
    private final int xOffset;
    private final int zOffset;
    private float frequency = FREQUENCY;

    private static DataResult<GridStructurePlacement> validate(GridStructurePlacement placement) {
        return placement.spacing <= placement.xOffset || placement.spacing <= placement.zOffset
            ? DataResult.error(() -> "Spacing has to be larger than offsets")
            : DataResult.success(placement);
    }

    public GridStructurePlacement(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<StructurePlacement.ExclusionZone> exclusionZone, int spacing, int xOffset, int zOffset) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.spacing = spacing;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
    }

    //Don't know what this does, but it's in Vanilla and some random mod probably needs it
    public GridStructurePlacement(int spacing, int xOffset, int zOffset, int salt) {
        this(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty(), spacing, xOffset, zOffset);
    }

    public int spacing() {
        return this.spacing;
    }

    public int xOffset() {
        return this.xOffset;
    }

    public int zOffset() {
        return this.zOffset;
    }

    /**
     * Player can adjust frequency of temples, will still populate in grid form
     * @param frequency
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    //** API **
    protected boolean isPlacementChunk(ChunkGeneratorStructureState structureState, int x, int z) {
        if(!isInGrid(x, z)) return false;
        float sample = ManagedChunkUtility.getChunkRandom(HBUtil.ChunkUtil.getChunkPos(x+","+z)).nextFloat();
        return sample < this.frequency;
    }

    public @NotNull StructurePlacementType<?> type() {
        return StructurePlacementType.RANDOM_SPREAD;
    }


    //** STATIC UTILITY **
    public static boolean isInGrid (int x, int z) {
        return mod(x, X_SPACING) == 0 && mod(z, Z_SPACING) == 0;
    }

    private static int mod(int value, int modulus) {
        int result = value % modulus;
        return result < 0 ? result + modulus : result;
    }




    //MIXIN
    public static <T> StructureSet updatePlacementOnRegister(ResourceKey<T> key, StructureSet value) {
        if(USE_GRID.containsKey(key.location()))
        {
            if (value instanceof StructureSet) {
                return new StructureSet(value.structures(), USE_GRID.get(key.location()));
            }
        }
        return value;
    }

}

