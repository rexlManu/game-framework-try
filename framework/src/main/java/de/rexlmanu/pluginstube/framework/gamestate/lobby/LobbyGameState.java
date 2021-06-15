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

package de.rexlmanu.pluginstube.framework.gamestate.lobby;

import de.rexlmanu.pluginstube.framework.events.arena.user.UserEnterArenaEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.UserLeftArenaEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.block.UserBlockBreakEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.block.UserBlockPlaceEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.damage.UserDamageEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.inventory.UserInventoryClickEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.item.UserDropItemEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.item.UserPickupItemEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.world.UserInteractionEvent;
import de.rexlmanu.pluginstube.framework.gamestate.GameState;
import de.rexlmanu.pluginstube.framework.utility.PlayerUtils;
import de.rexlmanu.pluginstube.framework.utility.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class LobbyGameState implements GameState {

  private static final ItemStack QUIT_ITEM = ItemStackBuilder.of(Material.IRON_DOOR).name("&bSpiel verlassen").build();

  public LobbyGameState() {

  }

  @EventHandler
  public void handle(UserEnterArenaEvent event) {
    if (!this.isState(event.arena())) return;
    Player player = event.getPlayer();
    event.arena().broadcast(player.getName() + " hat das Spiel betreten.");
    PlayerUtils.resetPlayer(player);
    player.getInventory().setItem(8, QUIT_ITEM);
    player.setPlayerWeather(WeatherType.CLEAR);
    player.setPlayerTime(2000, false);
  }

  @EventHandler
  public void handle(UserLeftArenaEvent event) {
    if (!this.isState(event.arena())) return;
    event.arena().broadcast(event.getPlayer().getName() + " hat das Spiel verlassen.");
  }

  @EventHandler
  public void handle(UserBlockBreakEvent event) {
    if (!this.isState(event.arena())) return;
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(UserBlockPlaceEvent event) {
    if (!this.isState(event.arena())) return;
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(UserInteractionEvent event) {
    if (!this.isState(event.arena())) return;
    if (event.hasBlock() || event.hasItem())
      event.setCancelled(true);
    if (event.getAction().name().startsWith("RIGHT") && QUIT_ITEM.equals(event.getItem())) {
      event.getPlayer().kickPlayer("");
    }
  }

  @EventHandler
  public void handle(UserDamageEvent event) {
    if (!this.isState(event.arena())) return;
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(UserDropItemEvent event) {
    if (!this.isState(event.arena())) return;
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(UserPickupItemEvent event) {
    if (!this.isState(event.arena())) return;
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(UserInventoryClickEvent event) {
    if (!this.isState(event.arena())) return;
    event.setCancelled(true);
  }
}
