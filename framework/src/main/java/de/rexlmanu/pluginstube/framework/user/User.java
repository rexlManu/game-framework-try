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

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The user interface
 */
public interface User {

  /**
   * User id represents the id from the actuall {@link Player}
   *
   * @return the uuid from the user
   */
  UUID uniqueId();

  /**
   * Wrapper to access the bukkit player
   *
   * @return wrapped {@link Player}
   */
  default Player player() {
    return Bukkit.getPlayer(this.uniqueId());
  }

  /**
   * For better chaining methods together
   * Plays a sound at the eye location of the player with the default volume 1
   *
   * @param sound the sound that should be played
   * @param pitch the pitch which the sound should be
   * @return the user instance
   */
  default User playSound(Sound sound, float pitch) {
    this.player().playSound(this.player().getEyeLocation(), sound, 1f, pitch);
    return this;
  }
}
