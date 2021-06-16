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

package de.rexlmanu.pluginstube.framework.gamestate;

import de.rexlmanu.pluginstube.framework.arena.Arena;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifier;
import de.rexlmanu.pluginstube.framework.modifier.event.EventModifierImpl;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * This class should be implemented to represent a state
 */
public interface GameState extends Listener {
  List<EventModifierImpl<?>> eventModifiers();

  /**
   * Registers a {@link EventModifier} for the game state
   *
   * @param eventModifier the modifier
   */
  void eventModifier(EventModifierImpl<?> eventModifier);

  void setup(Arena arena);

  default boolean isState(Arena arena) {
    return arena.currentState().equals(this);
  }

  default boolean isNotState(Arena arena) {
    return !this.isNotState(arena);
  }
}
