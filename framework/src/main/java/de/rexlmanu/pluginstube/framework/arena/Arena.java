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

package de.rexlmanu.pluginstube.framework.arena;

import de.rexlmanu.pluginstube.framework.gamestate.GameState;
import de.rexlmanu.pluginstube.framework.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class Arena {

  private GameState currentState;
  private List<User> users;

  public Arena(GameState currentState) {
    this.currentState = currentState;
    this.users = new ArrayList<>();
  }

  public void broadcast(String message) {
    this.broadcast(() -> message);
  }

  public void broadcast(Supplier<String> stringSupplier) {
    this
      .users
      .stream()
      .map(User::player)
      .filter(Objects::nonNull)
      .forEach(player -> player.sendMessage(stringSupplier.get()))
    ;
  }
}
