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

package de.rexlmanu.pluginstube.skywars.framework.editor;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.rexlmanu.pluginstube.skywars.framework.editor.command.EditorCommand;
import de.rexlmanu.pluginstube.skywars.framework.editor.process.ParticleTask;
import de.rexlmanu.pluginstube.skywars.framework.editor.process.ProcessHandler;
import de.rexlmanu.pluginstube.skywars.framework.editor.task.TaskScheduler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@Accessors(fluent = true)
@Getter
public class EditorPlugin extends JavaPlugin implements TaskScheduler {

  private CommandManager<Player> commandManager;

  private Injector injector;
  private ProcessHandler processHandler;

  @SneakyThrows
  @Override
  public void onEnable() {
    this.getDataFolder().mkdir();
    this.commandManager = new BukkitCommandManager<>(
      this,
      CommandExecutionCoordinator.simpleCoordinator(),
      commandSender -> ((Player) commandSender),
      player -> player
    );

    this.injector = Guice.createInjector(new EditorModule(this));

    this.injector.getInstance(EditorCommand.class);
    this.processHandler = this.injector.getInstance(ProcessHandler.class);

    Bukkit.getPluginManager().registerEvents(this.processHandler, this);
    ParticleTask task = this.injector.getInstance(ParticleTask.class);
  }

  @Override
  public void onDisable() {
  }

  @Override
  public void sync(Runnable runnable) {
    Bukkit.getScheduler().runTask(this, runnable);
  }

  @Override
  public void async(Runnable runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
  }

  @Override
  public BukkitTask timer(Runnable runnable, long seconds) {
    return Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, 0, seconds * 20);
  }
}
