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

package de.rexlmanu.pluginstube.framework.countdown;

import de.rexlmanu.pluginstube.framework.arena.Arena;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

@Accessors(fluent = true)
@Data
public class Countdown implements Runnable {

  private Arena arena;
  private Consumer<Integer> tickConsumer;
  private Consumer<Boolean> finishConsumer;

  private final int startSeconds;
  private int seconds;
  private BukkitTask bukkitTask;

  public Countdown(Arena arena, Consumer<Integer> tickConsumer, Consumer<Boolean> finishConsumer) {
    this.arena = arena;
    this.tickConsumer = tickConsumer;
    this.finishConsumer = finishConsumer;

    this.startSeconds = this.arena.template().properties().get("lobby-countdown").getAsInt();
    this.seconds = startSeconds;
  }

  @Override
  public void run() {
    this.tickConsumer.accept(this.seconds);
    if (this.seconds <= 0) {
      this.finishConsumer.accept(true);
      return;
    }
    this.seconds--;

    if (this.arena.users().size() < this.arena.template().properties().get("min-players").getAsInt()) {
      this.seconds = this.startSeconds;
    }
  }

  public void start(Plugin plugin) {
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 20);
  }

  public void destroy() {
    this.bukkitTask.cancel();
  }

  public void abort() {
    this.destroy();
    this.finishConsumer.accept(false);
  }
}
