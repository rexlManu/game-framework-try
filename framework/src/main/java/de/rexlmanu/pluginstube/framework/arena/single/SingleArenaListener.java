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

package de.rexlmanu.pluginstube.framework.arena.single;

import de.rexlmanu.pluginstube.framework.MiniGame;
import de.rexlmanu.pluginstube.framework.events.arena.ArenaGameStateSwitchEvent;
import de.rexlmanu.pluginstube.framework.events.arena.countdown.ArenaLobbyCountdownOverEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.UserEnterArenaEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.UserLeftArenaEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.block.UserBlockBreakEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.block.UserBlockPlaceEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.damage.UserDamageEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.inventory.UserInventoryClickEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.item.UserDropItemEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.item.UserPickupItemEvent;
import de.rexlmanu.pluginstube.framework.events.arena.user.world.UserInteractionEvent;
import de.rexlmanu.pluginstube.framework.events.bukkit.PlayerDamageEvent;
import de.rexlmanu.pluginstube.framework.events.game.UserJoinEvent;
import de.rexlmanu.pluginstube.framework.events.game.UserQuitEvent;
import de.rexlmanu.pluginstube.framework.gamestate.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

class SingleArenaListener implements Listener {

  private MiniGame miniGame;
  private SingleArenaProvider arenaProvider;

  public SingleArenaListener(MiniGame miniGame, SingleArenaProvider arenaProvider) {
    this.miniGame = miniGame;
    this.arenaProvider = arenaProvider;
  }

  @EventHandler
  public void handle(UserJoinEvent event) {
    this.arenaProvider.arena().users().add(event.user());
    this.miniGame.sync(() -> Bukkit.getPluginManager().callEvent(new UserEnterArenaEvent(event.user(), this.arenaProvider.arena())));
  }

  @EventHandler
  public void handle(UserQuitEvent event) {
    this.miniGame.sync(() -> {
      Bukkit.getPluginManager().callEvent(new UserLeftArenaEvent(event.user(), event.getPlayer(), this.arenaProvider.arena()));
      this.arenaProvider.arena().users().remove(event.user());
    });
  }

  @EventHandler
  public void handle(PlayerInteractEvent event) {
    this.miniGame.userController().search(event.getPlayer().getUniqueId()).ifPresent(user -> {
      UserInteractionEvent userInteractionEvent = new UserInteractionEvent(
        user,
        this.arenaProvider.arena(),
        event.getPlayer(),
        event.getAction(),
        event.getItem(),
        event.getClickedBlock(),
        event.getBlockFace()
      );
      Bukkit.getPluginManager().callEvent(userInteractionEvent);
      event.setCancelled(userInteractionEvent.isCancelled());
      event.setUseInteractedBlock(userInteractionEvent.useInteractedBlock());
      event.setUseItemInHand(userInteractionEvent.useItemInHand());
    });
  }

  @EventHandler
  public void handle(PlayerDropItemEvent event) {
    this.miniGame.userController().search(event.getPlayer().getUniqueId()).ifPresent(user -> {
      UserDropItemEvent userDropItemEvent = new UserDropItemEvent(
        user,
        this.arenaProvider.arena(),
        event.getPlayer(),
        event.getItemDrop()
      );
      Bukkit.getPluginManager().callEvent(userDropItemEvent);
      event.setCancelled(userDropItemEvent.isCancelled());
    });
  }

  @EventHandler
  public void handle(PlayerPickupItemEvent event) {
    this.miniGame.userController().search(event.getPlayer().getUniqueId()).ifPresent(user -> {
      UserPickupItemEvent userPickupItemEvent = new UserPickupItemEvent(
        user,
        this.arenaProvider.arena(),
        event.getPlayer(),
        event.getItem(),
        event.getRemaining()
      );
      Bukkit.getPluginManager().callEvent(userPickupItemEvent);
      event.setCancelled(userPickupItemEvent.isCancelled());
    });
  }

  @EventHandler
  public void handle(BlockBreakEvent event) {
    this.miniGame.userController().search(event.getPlayer().getUniqueId()).ifPresent(user -> {
      UserBlockBreakEvent userBlockBreakEvent = new UserBlockBreakEvent(
        user,
        this.arenaProvider.arena(),
        event.getBlock(),
        event.getPlayer()
      );
      Bukkit.getPluginManager().callEvent(userBlockBreakEvent);
      event.setCancelled(userBlockBreakEvent.isCancelled());
      event.setExpToDrop(userBlockBreakEvent.getExpToDrop());
    });
  }

  @EventHandler
  public void handle(BlockPlaceEvent event) {
    this.miniGame.userController().search(event.getPlayer().getUniqueId()).ifPresent(user -> {
      UserBlockPlaceEvent userBlockPlaceEvent = new UserBlockPlaceEvent(
        user,
        this.arenaProvider.arena(),
        event.getBlockPlaced(),
        event.getBlockReplacedState(),
        event.getBlockAgainst(),
        event.getItemInHand(),
        event.getPlayer(),
        event.canBuild()
      );
      Bukkit.getPluginManager().callEvent(userBlockPlaceEvent);
      event.setCancelled(userBlockPlaceEvent.isCancelled());
      event.setBuild(userBlockPlaceEvent.canBuild());
    });
  }

  @EventHandler
  public void handle(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) return;
    this.miniGame.userController().search(event.getWhoClicked().getUniqueId()).ifPresent(user -> {
      UserInventoryClickEvent userInventoryClickEvent = new UserInventoryClickEvent(
        user,
        this.arenaProvider.arena(),
        event.getView(),
        event.getSlotType(),
        event.getSlot(),
        event.getClick(),
        event.getAction(),
        event.getHotbarButton()
      );
      Bukkit.getPluginManager().callEvent(userInventoryClickEvent);
      event.setCancelled(userInventoryClickEvent.isCancelled());
      if (userInventoryClickEvent.modifiedCurrentItemStack()) {
        event.setCurrentItem(userInventoryClickEvent.getCurrentItem());
      }
      if (userInventoryClickEvent.modifiedResult()) {
        event.setResult(userInventoryClickEvent.getResult());
      }
      if (userInventoryClickEvent.modifiedCursor()) {
        event.setCursor(userInventoryClickEvent.getCursor());
      }
    });
  }

  @EventHandler
  public void handle(PlayerDamageEvent event) {
    this.miniGame.userController().search(event.player().getUniqueId()).ifPresent(user -> {
      UserDamageEvent userDamageEvent = new UserDamageEvent(
        user,
        this.arenaProvider.arena(),
        event.player(),
        event.getEntity(),
        event.getCause(),
        event.getDamage()
      );
      Bukkit.getPluginManager().callEvent(userDamageEvent);
      event.setCancelled(userDamageEvent.isCancelled());
      event.setDamage(userDamageEvent.getDamage());
    });
  }

  @EventHandler
  public void handle(ArenaLobbyCountdownOverEvent event) {
    if (this.miniGame.playingStates().isEmpty()) {
      return;
    }
    GameState gameState = this.miniGame.playingStates().get(0);
    Bukkit.getPluginManager().callEvent(new ArenaGameStateSwitchEvent(event.arena(), event.arena().currentState(), gameState));
    event.arena().countdown().destroy();
    event.arena().currentState(gameState);
  }

  @EventHandler
  public void handle(ArenaGameStateSwitchEvent event) {
    event.newGameState().setup(event.arena());
  }

}
