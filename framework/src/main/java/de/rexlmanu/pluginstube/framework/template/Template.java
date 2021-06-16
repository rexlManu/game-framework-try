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

package de.rexlmanu.pluginstube.framework.template;

import com.google.gson.JsonObject;
import de.rexlmanu.pluginstube.framework.template.configuration.ArenaConfiguration;
import de.rexlmanu.pluginstube.framework.template.configuration.LobbyGameStateConfiguration;
import de.rexlmanu.pluginstube.framework.utility.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@AllArgsConstructor
@Data
public class Template {
  public static TemplateBuilder builder() {
    return new TemplateBuilder();
  }

  public static ArenaConfiguration arena() {
    return new ArenaConfiguration(20);
  }

  public static LobbyGameStateConfiguration lobbyState() {
    return new LobbyGameStateConfiguration(1, 60);
  }

  private String name;

  private JsonObject properties;

  @Accessors(fluent = true)
  @Setter
  public static class TemplateBuilder implements Builder<Template> {
    private String name;
    private JsonObject jsonObject;

    public TemplateBuilder() {
      this.jsonObject = new JsonObject();
    }

    public TemplateBuilder include(TemplateConfiguration configuration) {
      configuration.apply(this.jsonObject);
      return this;
    }

    @Override
    public Template build() {
      return new Template(this.name, this.jsonObject);
    }
  }
}
