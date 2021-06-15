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

package de.rexlmanu.pluginstube.framework.events.arena.user.inventory;

import de.rexlmanu.pluginstube.framework.arena.Arena;
import de.rexlmanu.pluginstube.framework.user.User;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

@Accessors(fluent = true)
@Getter
public class UserInventoryClickEvent extends InventoryClickEvent {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private User user;
  private Arena arena;
  private boolean modifiedCurrentItemStack, modifiedResult, modifiedCursor;

  public UserInventoryClickEvent(User user, Arena arena, InventoryView view, InventoryType.SlotType type, int slot, boolean right, boolean shift) {
    super(view, type, slot, right, shift);
    this.user = user;
    this.arena = arena;
  }

  public UserInventoryClickEvent(User user, Arena arena, InventoryView view, InventoryType.SlotType type, int slot, ClickType click, InventoryAction action) {
    super(view, type, slot, click, action);
    this.user = user;
    this.arena = arena;
  }

  public UserInventoryClickEvent(User user, Arena arena, InventoryView view, InventoryType.SlotType type, int slot, ClickType click, InventoryAction action, int key) {
    super(view, type, slot, click, action, key);
    this.user = user;
    this.arena = arena;
  }

  @Override
  public void setCurrentItem(ItemStack stack) {
    super.setCurrentItem(stack);
    this.modifiedCurrentItemStack = true;
  }

  @Override
  public void setResult(Result newResult) {
    super.setResult(newResult);
    this.modifiedResult = true;
  }

  @Override
  public void setCursor(ItemStack stack) {
    super.setCursor(stack);
    this.modifiedCursor = true;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
