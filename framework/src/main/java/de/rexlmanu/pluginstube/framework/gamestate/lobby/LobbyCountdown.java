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
import de.rexlmanu.pluginstube.framework.countdown.Countdown;
import de.rexlmanu.pluginstube.framework.events.arena.countdown.ArenaLobbyCountdownOverEvent;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Accessors(fluent = true)
public class LobbyCountdown extends Countdown {
  public LobbyCountdown(Arena arena) {
    super(
      arena,
      second -> {
        arena.users().forEach(user -> {
          Player player = user.player();
          player.setLevel(second);
          player.setExp(
            (float) second
              /
              (float) arena
                .template()
                .properties()
                .get("lobby-countdown")
                .getAsInt()
          );
          if (second < 11) {
            user.playSound(Sound.CHICKEN_EGG_POP, 1.2f);
          }
          if (second < 6) {
            player.sendMessage(String.format("Das Spiel fängt in %s Sekunde%s an.", second, second > 1 ? 'n' : ""));
          }
        });
      },
      finish -> {
        if (finish) Bukkit.getPluginManager().callEvent(new ArenaLobbyCountdownOverEvent(arena));
      }
    );
  }
}
