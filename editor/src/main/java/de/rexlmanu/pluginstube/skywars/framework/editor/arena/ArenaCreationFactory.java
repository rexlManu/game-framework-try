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

package de.rexlmanu.pluginstube.skywars.framework.editor.arena;

import com.google.inject.Singleton;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.menu.MenuArenaCreation;
import de.rexlmanu.pluginstube.skywars.framework.editor.process.ArenaCreationProcess;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * The factory for {@link ArenaCreation}
 */
@Singleton
@Accessors(fluent = true)
@Getter
public class ArenaCreationFactory {

  private List<ArenaCreationProcess> processes;

  public ArenaCreationFactory() {
    this.processes = new ArrayList<>();
  }

  /**
   * Create s a {@link ArenaCreation} implementation
   *
   * @param name the name for the arena
   * @return the arena creation process
   */
  public ArenaCreation create(String name) {
    return new MenuArenaCreation(name);
  }

  public void abort(ArenaCreationProcess arenaCreationProcess) {
    this.processes.remove(arenaCreationProcess);
  }
}
