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

package de.rexlmanu.pluginstube.framework.user;

import de.rexlmanu.pluginstube.framework.events.game.MiniGameReadyEvent;
import de.rexlmanu.pluginstube.framework.events.game.UserJoinEvent;
import de.rexlmanu.pluginstube.framework.events.game.UserQuitEvent;
import de.rexlmanu.pluginstube.framework.utility.ExceptionLogger;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Accessors(fluent = true)
public class UserEventListener implements Listener {

  private UserController controller;

  public UserEventListener(UserController controller) {
    this.controller = controller;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(PlayerJoinEvent event) {
    event.setJoinMessage(null);
    this.controller
      .loadAndRegister(event.getPlayer().getUniqueId())
      .thenAccept(user -> Bukkit.getPluginManager().callEvent(new UserJoinEvent(user)))
      .exceptionally(ExceptionLogger::handle)
    ;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(PlayerQuitEvent event) {
    event.setQuitMessage(null);
    this.controller.search(event.getPlayer().getUniqueId()).ifPresent(user -> {
      Bukkit.getPluginManager().callEvent(new UserQuitEvent(user, event.getPlayer()));
      this.controller.saveAndRemove(user);
    });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(MiniGameReadyEvent event) {
    // For handling reloads properly
    Bukkit
      .getOnlinePlayers()
      .forEach(player -> this
        .controller
        .loadAndRegister(player.getUniqueId())
        .thenAccept(user -> Bukkit
          .getPluginManager()
          .callEvent(new UserJoinEvent(user))
        ).exceptionally(ExceptionLogger::handle)
      );
  }
}
