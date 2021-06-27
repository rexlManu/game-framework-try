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

package de.rexlmanu.pluginstube.skywars.plugin;

import de.rexlmanu.pluginstube.framework.MiniGame;
import de.rexlmanu.pluginstube.framework.MiniGameFramework;
import de.rexlmanu.pluginstube.framework.arena.ArenaProvider;
import de.rexlmanu.pluginstube.framework.gamestate.finish.FinishGameState;
import de.rexlmanu.pluginstube.framework.gamestate.lobby.LobbyCountdown;
import de.rexlmanu.pluginstube.framework.gamestate.lobby.LobbyGameState;
import de.rexlmanu.pluginstube.framework.gamestate.lobby.LobbyItem;
import de.rexlmanu.pluginstube.framework.map.MapProvider;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifier;
import de.rexlmanu.pluginstube.framework.template.Template;
import de.rexlmanu.pluginstube.framework.template.single.SingleTemplateProvider;
import de.rexlmanu.pluginstube.framework.utility.itemstack.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyWarsPlugin extends JavaPlugin {

  private MiniGame miniGame;

  public SkyWarsPlugin() {
    this.getDataFolder().mkdir();
    this.miniGame = MiniGameFramework
      .create(this)
      .arenaProvider(ArenaProvider.single())
      .lobbyState(LobbyGameState
        .builder()
        .lobbyItem(LobbyGameState.QUIT_LOBBY_ITEM)
        .lobbyItem(new LobbyItem(ItemStackBuilder
          .of(Material.CHEST)
          .name("&bKitauswahl")
          .build(),
          0,
          user -> user.playSound(Sound.CHEST_OPEN, 1.6f)
        ))
        .countdownSupplier(LobbyCountdown::new)
        .eventModifier(EventModifier.DENY_FOOD_CHANGE)
        .build()
      )
      .finishState(new FinishGameState())
      .eventModifier(EventModifier.DENY_MOB_SPAWNING)
      .templateProvider(new SingleTemplateProvider(Template
        .builder()
        .name("8x1")
        .include(Template.arena().maximalPlayers(8))
        .include(Template.lobbyState().countdown(60).minPlayers(1))
        .build()))
      .mapProvider(MapProvider.single(
        this.getDataFolder().toPath().resolve("wartelobby.map"),
        this.getDataFolder().toPath().resolve("maps")
      ))
      .build();
  }

  @Override
  public void onEnable() {
    this.miniGame.init();
  }

  @Override
  public void onDisable() {
    this.miniGame.terminate();
  }
}
