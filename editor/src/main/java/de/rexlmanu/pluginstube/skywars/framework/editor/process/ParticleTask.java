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

package de.rexlmanu.pluginstube.skywars.framework.editor.process;

import com.google.inject.Inject;
import de.rexlmanu.pluginstube.skywars.framework.editor.EditorPlugin;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreationFactory;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleEffect;

@Accessors(fluent = true)
public class ParticleTask implements Runnable {

  private EditorPlugin plugin;
  private ArenaCreationFactory arenaCreationFactory;

  @Inject
  public ParticleTask(EditorPlugin plugin, ArenaCreationFactory arenaCreationFactory) {
    this.plugin = plugin;
    this.arenaCreationFactory = arenaCreationFactory;

    this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0, 1);
  }

  @Override
  public void run() {
    return;
//    this.arenaCreationFactory
//      .processes()
//      .stream()
//      .filter(arenaCreationProcess -> Objects.nonNull(arenaCreationProcess.firstLocation()))
//      .forEach(this::drawParticle);
  }

  private void drawParticle(ArenaCreationProcess arenaCreationProcess) {
    Location firstLocation = arenaCreationProcess.firstLocation();
    Player player = arenaCreationProcess.arenaCreation().player();
    Location secondLocation = arenaCreationProcess.secondLocation() == null ?
      player.getEyeLocation()
      : arenaCreationProcess.secondLocation();

    double minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
    double minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
    double minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
    double maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX()) + 1;
    double maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY()) + 1;
    double maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ()) + 1;
    World world = firstLocation.getWorld();

    double particleDistance = 0.25d;
    for (double x = minX; x <= maxX; x += particleDistance) {
      for (double y = minY; y <= maxY; y += particleDistance) {
        for (double z = minZ; z <= maxZ; z += particleDistance) {
          int components = 0;
          if (x == minX || x == maxX) components++;
          if (y == minY || y == maxY) components++;
          if (z == minZ || z == maxZ) components++;
          if (components >= 2) {
            spawnParticle(new Location(world, x, y, z), player);
          }
        }
      }
    }
  }

  private void spawnParticle(Location location, Player player) {
    if (player.getLocation().distance(location) < 20)
      ParticleEffect.FLAME.display(location, player);
  }
}
