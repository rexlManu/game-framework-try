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

package de.rexlmanu.pluginstube.skywars.framework.editor.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import de.rexlmanu.pluginstube.framework.map.format.MapFormatFactory;
import de.rexlmanu.pluginstube.framework.map.format.MapSchema;
import de.rexlmanu.pluginstube.skywars.framework.editor.EditorPlugin;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreationFactory;
import de.rexlmanu.pluginstube.skywars.framework.editor.event.ArenaCreationRegisterEvent;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Accessors(fluent = true)
public class EditorCommand {

  private EditorPlugin plugin;
  private CommandManager<Player> manager;
  private ArenaCreationFactory factory;

  @Inject
  public EditorCommand(EditorPlugin plugin, CommandManager<Player> manager, ArenaCreationFactory factory) {
    this.plugin = plugin;
    this.manager = manager;
    this.factory = factory;

    this.register();
  }

  private void register() {
    Command.Builder<Player> builder = this.manager
      .commandBuilder("editor")
      .senderType(Player.class)
      .permission("framework.command.editor");

    manager.command(
      builder.literal("create", ArgumentDescription.of("Erstelle eine Map"))
        .argument(StringArgument.of("name"))
        .handler(this::create)
    );
//    manager.getParserRegistry().registerParserSupplier(TypeToken.get(ArenaCreationProcess.class), parserParameters -> new ArenaArgument<>(this.factory));
    manager.command(
      builder.literal("setposition")
        .argument(StringArgument.of("name", StringArgument.StringMode.SINGLE))
        .argument(StringArgument.of("key", StringArgument.StringMode.SINGLE))
        .handler(this::setPosition)
    );
    manager.command(
      builder.literal("setproperty")
        .argument(StringArgument.of("name", StringArgument.StringMode.SINGLE))
        .argument(StringArgument.of("key", StringArgument.StringMode.SINGLE))
        .argument(StringArgument.of("property", StringArgument.StringMode.GREEDY))
        .handler(this::setProperty)
    );

    manager.command(
      builder.literal("build")
        .argument(StringArgument.of("name", StringArgument.StringMode.SINGLE))
        .handler(this::build)
    );

    manager.command(
      builder.literal("abort")
        .handler(this::abort)
    );
    manager.command(
      builder.literal("import-world")
        .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY))
        .handler(this::importWorld)
    );
    manager.command(
      builder.literal("tp-world")
        .argument(StringArgument.of("name", StringArgument.StringMode.GREEDY))
        .handler(this::tpWorld)
    );
  }

  private void tpWorld(@NonNull CommandContext<Player> context) {
    String worldName = context.get("name");
    if (Bukkit.getWorld(worldName) == null) {
      return;
    }
    context.getSender().teleport(Bukkit.getWorld(worldName).getSpawnLocation());
    context.getSender().sendMessage("Du wurdest teleportiert.");
  }

  private void importWorld(@NonNull CommandContext<Player> context) {
    String worldName = context.get("name");
    if (Bukkit.getWorld(worldName) != null) {
      return;
    }
    World world = Bukkit.createWorld(new WorldCreator(worldName));
    world.setGameRuleValue("mobGriefing", "false");
    world.setGameRuleValue("doMobSpawning", "false");
    world.setGameRuleValue("doFireTick", "false");
    world.setGameRuleValue("doDaylightCycle", "false");
    world.setGameRuleValue("randomTickSpeed", "0");
    world.setFullTime(2000);
    world.setAutoSave(false);
    context.getSender().sendMessage("Welt wurde importiert.");
  }

  private void abort(@NonNull CommandContext<Player> context) {
    this.factory.processes().stream()
      .filter(arenaCreationProcess -> arenaCreationProcess.arenaCreation().player().equals(context.getSender()))
      .findAny()
      .ifPresent(arenaCreationProcess -> {
        this.factory.abort(arenaCreationProcess);
        context.getSender().sendMessage("Erstellung abgebrochen.");
      });
  }

  private void build(@NonNull CommandContext<Player> context) {
    String name = context.get("name");

    this.factory
      .processes()
      .stream()
      .filter(arenaCreationProcess -> arenaCreationProcess.arenaCreation().name().equals(name))
      .findAny()
      .ifPresent(arenaCreationProcess -> {
        MapSchema schema = arenaCreationProcess.build();
        try {
          Files.write(plugin.getDataFolder().toPath().resolve(schema.name() + ".map"), MapFormatFactory.write(schema), StandardOpenOption.CREATE_NEW);
          context.getSender().sendMessage("Die Map wurde erstellt als " + schema.name() + ".map.");
          this.factory.abort(arenaCreationProcess);
        } catch (IOException e) {
          context.getSender().sendMessage("Die Map konnte nicht erstellt werden. Fehler: " + e.getMessage());
        }
      });
  }

  private void setPosition(@NonNull CommandContext<Player> context) {
    String name = context.get("name");
    String key = context.get("key");
    this.factory
      .processes()
      .stream()
      .filter(arenaCreationProcess -> arenaCreationProcess.arenaCreation().name().equals(name))
      .findAny()
      .ifPresent(arenaCreationProcess -> {
        arenaCreationProcess.setPosition(key, context.getSender().getLocation());
        context.getSender().sendMessage(String.format("Du hast die Position %s gesetzt.", key));
      });
  }

  private void setProperty(@NonNull CommandContext<Player> context) {
    String name = context.get("name");
    String key = context.get("key");
    this.factory
      .processes()
      .stream()
      .filter(arenaCreationProcess -> arenaCreationProcess.arenaCreation().name().equals(name))
      .findAny()
      .ifPresent(arenaCreationProcess -> {
        String property = context.get("property");
        arenaCreationProcess.setProperty(key, property);
        context.getSender().sendMessage(String.format("Du hast die Property %s zu %s gesetzt.", key, property));
      });
  }

  private void create(@NonNull CommandContext<Player> context) {
    Bukkit
      .getPluginManager()
      .callEvent(new ArenaCreationRegisterEvent(this.factory
        .create(context.get("name"))
        .withPlayer(context.getSender())));
  }
}
