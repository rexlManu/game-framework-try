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

package de.rexlmanu.pluginstube.framework.modifier.event;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * This can be used to modify events simple.
 * The main purpose for this class is to give already defined options,
 * so you can use them already without reclaim the work
 *
 * @param <E> a class that should be a {@link org.bukkit.event.Event}
 */
public interface EventModifier {

  /**
   * Prevents creatures to spawn, except they spawned by a plugin
   */
  EventModifierImpl<CreatureSpawnEvent> DENY_MOB_SPAWNING = new EventModifierImpl<>(
    CreatureSpawnEvent.class,
    event -> event.setCancelled(!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))
  );

  /**
   * Prevents players from changing they food level
   */
  EventModifierImpl<FoodLevelChangeEvent> DENY_FOOD_CHANGE = new EventModifierImpl<>(
    FoodLevelChangeEvent.class,
    event -> event.setCancelled(true)
  );

}
