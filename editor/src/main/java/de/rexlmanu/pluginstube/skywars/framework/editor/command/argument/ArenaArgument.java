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

package de.rexlmanu.pluginstube.skywars.framework.editor.command.argument;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import de.rexlmanu.pluginstube.skywars.framework.editor.arena.ArenaCreationFactory;
import de.rexlmanu.pluginstube.skywars.framework.editor.process.ArenaCreationProcess;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.Queue;

@Accessors(fluent = true)
@AllArgsConstructor
public class ArenaArgument<C> implements ArgumentParser<C, ArenaCreationProcess> {
  private ArenaCreationFactory factory;

  @Override
  public @NonNull ArgumentParseResult<@NonNull ArenaCreationProcess> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
    String peek = inputQueue.peek();
    if (peek == null) {
      return ArgumentParseResult.failure(new NoInputProvidedException(ArenaCreationProcess.class, commandContext));
    }
    Optional<ArenaCreationProcess> creationProcess = factory.processes().stream().filter(arenaCreationProcess -> arenaCreationProcess.arenaCreation().name().equals(peek)).findFirst();
    if (creationProcess.isEmpty()) {
      return ArgumentParseResult.failure(new NoInputProvidedException(ArenaCreationProcess.class, commandContext));
    }

    return ArgumentParseResult.success(creationProcess.get());
  }
}
