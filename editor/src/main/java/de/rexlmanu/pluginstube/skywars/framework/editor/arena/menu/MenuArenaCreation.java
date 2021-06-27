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

package de.rexlmanu.pluginstube.skywars.framework.editor.arena.menu;

import de.rexlmanu.pluginstube.framework.map.format.MapLayout;
import de.rexlmanu.pluginstube.framework.map.format.MapPosition;
import de.rexlmanu.pluginstube.framework.map.format.MapSchema;
import de.rexlmanu.pluginstube.framework.utility.Builder;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreation;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
@Getter
public class MenuArenaCreation implements ArenaCreation, Builder<MapSchema> {

  private String name;
  private Map<String, String> properties;
  private Map<String, MapPosition> positions;
  private Player player;
  private MapLayout layout;

  public MenuArenaCreation(String name) {
    this.name = name;
    this.properties = new HashMap<>();
    this.positions = new HashMap<>();
  }

  @Override
  public ArenaCreation withPlayer(Player player) {
    this.player = player;
    return this;
  }

  @Override
  public void setPosition(String name, MapPosition position) {
    this.positions.put(name, position);
  }

  @Override
  public void setProperty(String name, String property) {
    this.properties.put(name, property);
  }

  @Override
  public void setLayout(MapLayout layout) {
    this.layout = layout;
  }

  @Override
  public MapSchema build() {
    return new MapSchema(this.name, this.properties, this.positions, layout);
  }
}
