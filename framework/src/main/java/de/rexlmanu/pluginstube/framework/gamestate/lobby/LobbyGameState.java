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

import de.rexlmanu.pluginstube.framework.arena.Arena;
import de.rexlmanu.pluginstube.framework.countdown.CountdownSupplier;
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
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifierImpl;
import de.rexlmanu.pluginstube.framework.utility.Builder;
import de.rexlmanu.pluginstube.framework.utility.PlayerUtils;
import de.rexlmanu.pluginstube.framework.utility.itemstack.ItemStackBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The main entry for a game
 */
@Accessors(fluent = true)
public class LobbyGameState implements GameState {

  public static final LobbyItem QUIT_LOBBY_ITEM = new LobbyItem(
    ItemStackBuilder.of(Material.IRON_DOOR).name("&bSpiel verlassen").build(),
    8,
    user -> user.playSound(Sound.LEVEL_UP, 1f).player().kickPlayer("")
  );

  public static LobbyGameStateBuilder builder() {
    return new LobbyGameStateBuilder();
  }

  private List<LobbyItem> lobbyItems;
  @Getter
  private List<EventModifierImpl<?>> eventModifiers;
  private CountdownSupplier countdownSupplier;

  public LobbyGameState(List<LobbyItem> lobbyItems, List<EventModifierImpl<?>> eventModifiers, CountdownSupplier countdownSupplier) {
    this.lobbyItems = lobbyItems;
    this.eventModifiers = eventModifiers;
    this.countdownSupplier = countdownSupplier;
  }

  @Override
  public void setup(Arena arena) {
    arena.countdown(this.countdownSupplier.supply(arena));
  }

  @Override
  public void eventModifier(EventModifierImpl<?> eventModifier) {
    this.eventModifiers.add(eventModifier);
  }

  @EventHandler
  public void handle(UserEnterArenaEvent event) {
    if (!this.isState(event.arena())) return;
    Player player = event.getPlayer();
    event.arena().broadcast(player.getName() + " hat das Spiel betreten.");
    PlayerUtils.resetPlayer(player);
    player.setPlayerWeather(WeatherType.CLEAR);
    player.setPlayerTime(2000, false);
    player.teleport(event.arena().lobbySpawn());

    this.lobbyItems.forEach(lobbyItem -> player.getInventory().setItem(lobbyItem.slot(), lobbyItem.itemStack()));
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

    this.lobbyItems
      .stream()
      .filter(lobbyItem -> lobbyItem.itemStack().equals(event.getItem()))
      .findAny()
      .ifPresent(lobbyItem -> lobbyItem.userConsumer().accept(event.user()));
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

  @Accessors(fluent = true)
  public static class LobbyGameStateBuilder implements Builder<LobbyGameState> {

    private List<LobbyItem> lobbyItems;
    private List<EventModifierImpl<?>> eventModifiers;
    @Setter
    private CountdownSupplier countdownSupplier;

    private LobbyGameStateBuilder() {
      this.lobbyItems = new ArrayList<>();
      this.eventModifiers = new ArrayList<>();
    }

    public LobbyGameStateBuilder lobbyItem(LobbyItem lobbyItem) {
      this.lobbyItems.add(lobbyItem);
      return this;
    }

    public LobbyGameStateBuilder eventModifier(EventModifierImpl<?> eventModifier) {
      this.eventModifiers.add(eventModifier);
      return this;
    }

    @Override
    public LobbyGameState build() {
      return new LobbyGameState(
        this.lobbyItems,
        this.eventModifiers,
        this.countdownSupplier
      );
    }
  }
}
