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

import de.rexlmanu.pluginstube.framework.map.format.MapIngredient;
import de.rexlmanu.pluginstube.framework.map.format.MapLayout;
import de.rexlmanu.pluginstube.framework.map.format.MapPosition;
import de.rexlmanu.pluginstube.framework.map.format.MapSchema;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreation;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Accessors(fluent = true)
@Data
public class ArenaCreationProcess {

  private Location firstLocation, secondLocation;

  private int minX, minY, minZ;
  private int maxX, maxY, maxZ;

  private ArenaCreation arenaCreation;

  public ArenaCreationProcess(ArenaCreation arenaCreation) {
    this.arenaCreation = arenaCreation;
  }

  public void calculatePoints() {
    this.minX = Math.min(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
    this.minY = Math.min(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
    this.minZ = Math.min(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());

    this.maxX = Math.max(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
    this.maxY = Math.max(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
    this.maxZ = Math.max(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
  }

  public void setPosition(String name, Location location) {
    this.arenaCreation.setPosition(name, new MapPosition(
      location.getBlockX() - this.minX,
      location.getBlockY() - this.minY,
      location.getBlockZ() - this.minZ,
      location.getYaw(),
      location.getPitch()
    ));
  }

  public void setProperty(String name, String property) {
    this.arenaCreation.setProperty(name, property);
  }

  public MapSchema build() {
    this.createMapLayout();
    return this.arenaCreation.build();
  }

  private void createMapLayout() {
    List<MapIngredient> ingredients = new ArrayList<>();

    int[][][] layout = new int[maxX - minX][maxY - minY][maxZ - minZ];

    for (int x = 0; x < (maxX - minX); x++) {
      for (int y = 0; y < (maxY - minY); y++) {
        for (int z = 0; z < (maxZ - minZ); z++) {
          Location location = new Location(this.firstLocation.getWorld(), minX + x, minY + y, minZ + z);

          Optional<MapIngredient> optional = ingredients.stream().filter(mapIngredient -> mapIngredient.isSimilar(location.getBlock())).findAny();
          if (optional.isPresent()) {
            layout[x][y][z] = ingredients.indexOf(optional.get());
          } else {
            MapIngredient ingredient = MapIngredient.of(location.getBlock());
            ingredients.add(ingredient);
            layout[x][y][z] = ingredients.indexOf(ingredient);
          }
        }
      }
    }
    this.arenaCreation.setLayout(new MapLayout(ingredients, layout));
  }
}
