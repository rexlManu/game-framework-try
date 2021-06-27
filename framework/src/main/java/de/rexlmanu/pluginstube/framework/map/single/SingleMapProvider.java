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

package de.rexlmanu.pluginstube.framework.map.single;

import de.rexlmanu.pluginstube.framework.MiniGame;
import de.rexlmanu.pluginstube.framework.arena.Arena;
import de.rexlmanu.pluginstube.framework.map.MapProvider;
import de.rexlmanu.pluginstube.framework.map.format.MapFormatFactory;
import de.rexlmanu.pluginstube.framework.map.format.MapPosition;
import de.rexlmanu.pluginstube.framework.map.format.MapSchema;
import de.rexlmanu.pluginstube.framework.map.paster.types.ChunkMapPaster;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Accessors(fluent = true)
@Data
public class SingleMapProvider implements MapProvider {

  private static final String GAME_WORLD_NAME = "game_world";

  private Path lobbyMap, mapDirectory;
  private World gameWorld;

  public SingleMapProvider(Path lobbyMap, Path mapDirectory) {
    this.lobbyMap = lobbyMap;
    this.mapDirectory = mapDirectory;

    this.gameWorld = Bukkit.createWorld(
      WorldCreator
        .name(GAME_WORLD_NAME)
        .environment(World.Environment.NORMAL)
        .generator(new SingleGameChunkGenerator())
        .generateStructures(false)
    );

    this.gameWorld.setDifficulty(Difficulty.NORMAL);
    this.gameWorld.setFullTime(2000);
    this.gameWorld.setGameRuleValue("doDaylightCycle", "false");
    this.gameWorld.setGameRuleValue("doMobSpawning", "false");
    this.gameWorld.setGameRuleValue("mobGriefing", "false");
    this.gameWorld.setGameRuleValue("doFireTick", "false");
    this.gameWorld.setGameRuleValue("randomTickSpeed", "0");
    this.gameWorld.setAutoSave(false);
  }

  @Override
  public void init(MiniGame miniGame) {
    try {
      MapSchema mapSchema = MapFormatFactory.read(Files.readAllBytes(this.lobbyMap));

      ChunkMapPaster paster = new ChunkMapPaster(mapSchema);
      Bukkit.getScheduler().runTask(miniGame.plugin(), () -> {
        Location position = new Location(this.gameWorld, 0, 60, 0);
        paster.paste(position);
        Bukkit.getScheduler().runTask(miniGame.plugin(), () -> {
          paster.load(position);
          System.out.println("wartelobby pasted.");

          for (Arena arena : miniGame.arenaProvider().arenaContainer().arenas()) {
            MapPosition spawn = mapSchema.positions().get("spawn");
            Location lobbySpawn = position.clone().add(spawn.x(), spawn.y(), spawn.z());
            lobbySpawn.setPitch(spawn.pitch());
            lobbySpawn.setYaw(spawn.yaw());
            arena.lobbySpawn(lobbySpawn);
          }
        });
      });

    } catch (IOException e) {
      throw new IllegalStateException("Could not read lobby map.");
    }
  }

  @Override
  public void terminate(MiniGame miniGame) {

  }
}
