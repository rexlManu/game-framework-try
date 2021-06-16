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

package de.rexlmanu.pluginstube.framework;

import de.rexlmanu.pluginstube.framework.arena.ArenaProvider;
import de.rexlmanu.pluginstube.framework.gamestate.GameState;
import de.rexlmanu.pluginstube.framework.map.MapProvider;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifier;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifierImpl;
import de.rexlmanu.pluginstube.framework.team.TeamProvider;
import de.rexlmanu.pluginstube.framework.template.TemplateProvider;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class MiniGameFramework {

  public static MiniGameFramework create(JavaPlugin javaPlugin) {
    return new MiniGameFramework(javaPlugin);
  }

  @Setter
  private ArenaProvider arenaProvider;
  @Setter
  private MapProvider mapProvider;
  @Setter
  private TemplateProvider templateProvider;
  @Setter
  private TeamProvider teamProvider;
  @Setter
  private GameState lobbyState, finishState;

  private JavaPlugin plugin;
  private List<GameState> playingStates;
  private List<EventModifierImpl<?>> eventModifiers;

  private MiniGameFramework(JavaPlugin plugin) {
    this.plugin = plugin;
    this.playingStates = new ArrayList<>();
    this.eventModifiers = new ArrayList<>();
  }

  public MiniGameFramework registerPlayingState(GameState gameState) {
    this.playingStates.add(gameState);
    return this;
  }

  /**
   * Adds a {@link EventModifier} globally, that means that his modify
   * is for the whole server. For only states depended modifiers,
   * please add them in {@link GameState#eventModifier(EventModifierImpl)} ()}
   *
   * @param eventModifier the event modifier
   * @return the builder instance for chaining
   */
  public MiniGameFramework eventModifier(EventModifierImpl<?> eventModifier) {
    this.eventModifiers.add(eventModifier);
    return this;
  }

  public MiniGame build() {
    MiniGame miniGame = new MiniGame(
      this.plugin,
      this.arenaProvider,
      this.mapProvider,
      this.templateProvider,
      this.lobbyState,
      this.finishState,
      this.playingStates
    );
    miniGame.teamProvider(this.teamProvider);
    miniGame.eventModifiers(this.eventModifiers);
    return miniGame;
  }
}
