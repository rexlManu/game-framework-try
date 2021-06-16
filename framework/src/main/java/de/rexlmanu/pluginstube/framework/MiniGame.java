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
import de.rexlmanu.pluginstube.framework.events.bukkit.ExtraBukkitEventListener;
import de.rexlmanu.pluginstube.framework.events.game.MiniGameReadyEvent;
import de.rexlmanu.pluginstube.framework.gamestate.GameState;
import de.rexlmanu.pluginstube.framework.gamestate.GameStateEventModifierExecutor;
import de.rexlmanu.pluginstube.framework.map.MapProvider;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifierExecutor;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifierImpl;
import de.rexlmanu.pluginstube.framework.team.TeamProvider;
import de.rexlmanu.pluginstube.framework.template.TemplateProvider;
import de.rexlmanu.pluginstube.framework.user.UserController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Accessors(fluent = true)
@Getter
@Setter
public class MiniGame {

  private JavaPlugin plugin;
  private ArenaProvider arenaProvider;
  private MapProvider mapProvider;
  private TemplateProvider templateProvider;

  private GameState lobbyState, finishState;
  private List<GameState> playingStates;
  private List<EventModifierImpl<?>> eventModifiers;

  private UserController userController;

  @Setter
  private TeamProvider teamProvider;

  MiniGame(JavaPlugin plugin, ArenaProvider arenaProvider, MapProvider mapProvider, TemplateProvider templateProvider, GameState lobbyState, GameState finishState, List<GameState> playingStates) {
    this.plugin = plugin;
    this.arenaProvider = arenaProvider;
    this.mapProvider = mapProvider;
    this.templateProvider = templateProvider;
    this.lobbyState = lobbyState;
    this.finishState = finishState;
    this.playingStates = playingStates;

    this.userController = new UserController();
    this.eventModifiers = new ArrayList<>();
  }

  /**
   * The main entrypoint for the {@link MiniGame}
   */
  public void init() {
    this.userController.init(this);
    this.arenaProvider.init(this);

    this.registerListeners();

    Bukkit.getPluginManager().callEvent(new MiniGameReadyEvent());
  }

  public void terminate() {
    this.arenaProvider.terminate(this);
    this.userController.terminate(this);
  }

  /**
   * Registers a {@link Listener}
   * For registering a listener on init please use addListener() instead
   *
   * @param listener which should be registered
   */
  public void register(Listener listener) {
    Bukkit.getPluginManager().registerEvents(listener, this.plugin);
  }

  public void sync(Runnable runnable) {
    Bukkit.getScheduler().runTask(this.plugin, runnable);
  }

  /**
   * Just to keep stuff clean, we're registering listeners here
   */
  private void registerListeners() {
    Stream
      .concat(Stream.of(
        this.lobbyState,
        this.finishState),
        this.playingStates.stream()
      )
      .peek(gameState -> new GameStateEventModifierExecutor(this.userController, this.arenaProvider, this.plugin, gameState))
      .forEach(this::register);

    EventModifierExecutor eventModifierExecutor = new EventModifierExecutor();
    this.eventModifiers.forEach(eventModifier -> Bukkit.getPluginManager().registerEvent(eventModifier.eventClass(),
      eventModifier,
      EventPriority.NORMAL,
      eventModifierExecutor,
      this.plugin
    ));
    this.register(new ExtraBukkitEventListener());
  }
}
