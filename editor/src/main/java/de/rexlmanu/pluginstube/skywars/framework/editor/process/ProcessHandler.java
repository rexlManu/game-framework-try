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

package de.rexlmanu.pluginstube.skywars.framework.editor.process;

import com.google.inject.Inject;
import de.rexlmanu.pluginstube.framework.utility.itemstack.ItemStackBuilder;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreation;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreationFactory;
import de.rexlmanu.pluginstube.skywars.framework.editor.event.ArenaCreationRegisterEvent;
import de.rexlmanu.pluginstube.skywars.framework.editor.task.TaskScheduler;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
@Accessors(fluent = true)
public class ProcessHandler implements Listener {

  private static final ItemStack AXE = ItemStackBuilder
    .of(Material.GOLD_AXE)
    .name(ChatColor.GOLD + "Axt " + ChatColor.GRAY + "<Linksklick>")
    .build();

  private TaskScheduler scheduler;
  private ArenaCreationFactory factory;

  @Inject
  public ProcessHandler(TaskScheduler scheduler, ArenaCreationFactory factory) {
    this.scheduler = scheduler;
    this.factory = factory;
  }

  public void register(ArenaCreation arenaCreation) {
    this.factory.processes().add(new ArenaCreationProcess(arenaCreation));

    Player player = arenaCreation.player();
    player.sendMessage("Bitte markiere die erste Position.");
    player.getInventory().addItem(AXE);
  }

  @EventHandler
  public void handle(ArenaCreationRegisterEvent event) {
    this.register(event.arenaCreation());
  }

  @EventHandler
  public void handle(BlockBreakEvent event) {
    if (!AXE.equals(event.getPlayer().getItemInHand())) return;
    this.process(event.getPlayer()).ifPresent(creationProcess -> {
      event.setCancelled(true);
      if (creationProcess.firstLocation() == null) {
        creationProcess.firstLocation(event.getBlock().getLocation());
        event.getPlayer().sendMessage("Bitte markiere die zweite Position.");
        return;
      }
      if (creationProcess.secondLocation() == null) {
        creationProcess.secondLocation(event.getBlock().getLocation());
        creationProcess.calculatePoints();
        event.getPlayer().sendMessage("Du kannst nun via '/editor setposition <Name> <Key>' alle weiteren Positionen setzen.");
        return;
      }
    });
  }

  private Optional<ArenaCreationProcess> process(Player player) {
    return this.factory.processes()
      .stream()
      .filter(process -> process.arenaCreation().player().equals(player))
      .findAny();
  }
}
