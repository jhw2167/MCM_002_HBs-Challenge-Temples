package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.structure.GridStructurePlacement;
import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

import static com.holybuckets.foundation.block.ModBlocks.stoneBrickBlockEntity;

public class TempleUtility {

    /**
     * CLEAN CLUSTERS
     * @return boolean false if cleaning failed
     */
    public static BlockPos findTempleBlockEntity(ChunkAccess c)
    {
        if( c == null || !(c instanceof  LevelChunk)) return null;
        LevelChunk levelChunk = (LevelChunk) c;
        LevelChunkSection[] sections = levelChunk.getSections();

        BlockState seeking = stoneBrickBlockEntity.defaultBlockState();
        final int SECTION_SZ = 16;
        //loop in reverse, top, down
        final boolean TURN_OFF = false;
        for (int i = sections.length - 1; i >= 0; i--)
        {
            LevelChunkSection section = sections[i];
            if (section == null || section.hasOnlyAir() || TURN_OFF)
                continue;

            //iterate over x, y, z
            PalettedContainer<BlockState> states = section.getStates();

            for (int x = 0; x < SECTION_SZ; x++)
            {
                for (int y = 0; y < SECTION_SZ; y++)
                {
                    for (int z = 0; z < SECTION_SZ; z++)
                    {
                        BlockState blockState = states.get(x, y, z).getBlock().defaultBlockState();
                        if ( blockState == seeking )
                        {
                            //If there is an identical block to the right, take the position of this block
                            HBUtil.TripleInt relativePos = HBUtil.TripleInt.of(x, y, z);
                            HBUtil.WorldPos worldPos = new HBUtil.WorldPos(relativePos, y, c);
                            return  worldPos.getWorldPos();
                        }

                    }
                }
            }
            //END 3D iteration

        }
        //END SECTIONS LOOP

        return null;
    }


    public static boolean candidateChunkForTemple(ChunkAccess c) {
        return GridStructurePlacement.isInGrid(c.getPos().x, c.getPos().z);
    }
}
