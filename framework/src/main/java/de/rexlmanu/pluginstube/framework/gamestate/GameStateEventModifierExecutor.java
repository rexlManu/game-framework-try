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

package de.rexlmanu.pluginstube.framework.gamestate;

import de.rexlmanu.pluginstube.framework.arena.ArenaProvider;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifierImpl;
import de.rexlmanu.pluginstube.framework.user.UserController;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

@Accessors(fluent = true)
public class GameStateEventModifierExecutor implements EventExecutor {

  private UserController userController;
  private ArenaProvider arenaProvider;
  private JavaPlugin plugin;
  private GameState gameState;

  public GameStateEventModifierExecutor(UserController userController, ArenaProvider arenaProvider, JavaPlugin plugin, GameState gameState) {
    this.userController = userController;
    this.arenaProvider = arenaProvider;
    this.plugin = plugin;
    this.gameState = gameState;

    this.gameState
      .eventModifiers()
      .forEach(eventModifier ->
        Bukkit.getPluginManager().registerEvent(
          eventModifier.eventClass(),
          eventModifier,
          EventPriority.NORMAL,
          this,
          this.plugin
        ));
  }

  @Override
  public void execute(Listener listener, Event event) throws EventException {
    this
      .gameState
      .eventModifiers()
      .stream()
      .filter(eventModifier -> eventModifier.equals(listener))
      .filter(eventModifier -> eventModifier.eventClass().equals(event.getClass()))
      .forEach(eventModifier -> {
        try {
          for (Method method : event.getClass().getMethods()) {
            if (method.getName().equals("getEntity")) {
              Object object = method.invoke(event);
              if (!(object instanceof Player)) break;
              Player player = (Player) object;
              userController.search(player.getUniqueId()).flatMap(user -> this.arenaProvider.arenaContainer().arenaByUser(user)
                .filter(arena -> gameState.isState(arena))).ifPresent(arena -> {
                ((EventModifierImpl<Event>) eventModifier).eventConsumer().accept(event);
              });
            }
          }
        } catch (ReflectiveOperationException e) {
          e.printStackTrace();
        }
      });
  }
}
