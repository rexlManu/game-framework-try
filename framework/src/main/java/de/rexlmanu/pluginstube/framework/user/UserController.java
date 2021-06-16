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

package de.rexlmanu.pluginstube.framework.user;

import de.rexlmanu.pluginstube.framework.GameInitializer;
import de.rexlmanu.pluginstube.framework.GameTerminator;
import de.rexlmanu.pluginstube.framework.MiniGame;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Accessors(fluent = true)
public class UserController implements GameInitializer, GameTerminator {

  private List<User> users;

  public UserController() {
    this.users = new CopyOnWriteArrayList<>();
  }

  @Override
  public void init(MiniGame miniGame) {
    miniGame.register(new UserEventListener(this));
  }

  @Override
  public void terminate(MiniGame miniGame) {
    this.users.forEach(this::saveAndRemove);
  }

  public CompletableFuture<User> load(UUID uniqueId) {
    // todo implement database loading later here
    return CompletableFuture.supplyAsync(() -> new GameUser(uniqueId));
  }

  public CompletableFuture<User> loadAndRegister(UUID uniqueId) {
    return this.load(uniqueId).thenApply(user -> {
      this.users.add(user);
      return user;
    });
  }

  public Optional<User> search(UUID uniqueId) {
    return this.users.stream().filter(user -> user.uniqueId().equals(uniqueId)).findAny();
  }

  public void saveAndRemove(User user) {
    // todo save player in database
    this.remove(user);
  }

  private void remove(User user) {
    this.users.remove(user);
  }
}
