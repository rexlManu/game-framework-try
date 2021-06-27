/*
 * Copyright (c) 2021 Emmanuel Lampe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.rexlmanu.pluginstube.framework.map.paster.types;

import de.rexlmanu.pluginstube.framework.map.format.MapIngredient;
import de.rexlmanu.pluginstube.framework.map.format.MapLayout;
import de.rexlmanu.pluginstube.framework.map.format.MapSchema;
import de.rexlmanu.pluginstube.framework.map.paster.MapPaster;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Accessors(fluent = true)
public class ChunkMapPaster implements MapPaster {

  private MapSchema mapSchema;
  private List<Chunk> unloadedChunks;

  public ChunkMapPaster(MapSchema mapSchema) {
    this.mapSchema = mapSchema;
    this.unloadedChunks = new CopyOnWriteArrayList<>();
  }

  @Override
  public void paste(Location position) {
    MapLayout layout = this.mapSchema.layout();
    int[][][] blockLayout = layout.blockLayout();
    for (int x = 0; x < blockLayout.length; x++) {
      System.out.println(x);
      for (int y = 0; y < blockLayout[x].length; y++) {
        for (int z = 0; z < blockLayout[x][y].length; z++) {
          int ingredientIndex = blockLayout[x][y][z];
          MapIngredient ingredient = layout.ingredients().get(ingredientIndex);
          Chunk chunk = position.getWorld().getChunkAt((position.getBlockX() + x) >> 4, (position.getBlockZ() + z) >> 4);
          if (!this.unloadedChunks.contains(chunk)) {
            this.unloadedChunks.add(chunk);
          }
          if (chunk.isLoaded()) {
//            chunk.unload(false, false);
          }

          CraftChunk craftChunk = (CraftChunk) chunk;
          craftChunk.getHandle().a(
            new BlockPosition(position.getBlockX() + x, position.getBlockY() + y, position.getBlockZ() + z),
            Block.getByCombinedId(Material.valueOf(ingredient.material().toUpperCase()).getId() + (ingredient.data() << 12))
          );
        }
      }
    }
  }

  public void load(Location position) {
    this.unloadedChunks.forEach(chunk -> {
//      chunk.load(false);
      position.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
    });
  }
}
