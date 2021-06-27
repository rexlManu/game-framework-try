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

package de.rexlmanu.pluginstube.framework.map.format;

import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSkull;

import java.io.Serializable;

/**
 * Used for storing the material name and data
 */
@Accessors(fluent = true)
@AllArgsConstructor
@Data
public class MapIngredient implements Serializable {

  public static MapIngredient of(Block block) {
    MapIngredient ingredient = new MapIngredient(block.getType().name().toLowerCase(), block.getData());
    if (!block.getType().equals(Material.SKULL)) return ingredient;

    CraftSkull state = (CraftSkull) block.getState();
    JsonObject object = new JsonObject();
    object.addProperty("skullType", state.getSkullType().name().toLowerCase());
    object.addProperty("rotation", state.getRotation().name().toLowerCase());
    JsonObject texturesObject = new JsonObject();
    Property textures = state.getTileEntity()
      .getGameProfile()
      .getProperties()
      .get("textures")
      .stream()
      .filter(property -> property.getName().equals("textures"))
      .findAny()
      .get();
    texturesObject.addProperty("value", textures.getValue());
    texturesObject.addProperty("signature", textures.getSignature());
    object.add("textures", texturesObject);
    ingredient.customPropertiesJson(object.toString());
    return ingredient;
  }

  /**
   * The bukkit {@link org.bukkit.Material} name as {@link String#toLowerCase()} variant
   */
  private String material;
  /**
   * The data of the material
   */
  private byte data;
  /**
   * Should be used for saving extra information like skull owner like for skulls or other blocks that has more then only data
   */
  private String customPropertiesJson;

  public MapIngredient(String material, byte data) {
    this.material = material;
    this.data = data;
    this.customPropertiesJson = "";
  }

  public JsonObject asJsonObject() {
    if (this.customPropertiesJson == null || this.customPropertiesJson.equals("")) return new JsonObject();
    return MapFormatFactory.PARSER.parse(this.customPropertiesJson).getAsJsonObject();
  }

  public boolean isSimilar(Block block) {
    if (!this.material.equals(block.getType().name().toLowerCase()) ||
      this.data != block.getData()) return false;
    if (block.getType().equals(Material.SKULL)) {
      CraftSkull state = (CraftSkull) block.getState();
      JsonObject object = this.asJsonObject();
      if (object.has("skullType")
        && object.getAsJsonPrimitive("skullType").getAsString()
        .equals(state.getSkullType().name().toLowerCase()))
        return false;
      if (object.has("rotation")
        && object.getAsJsonPrimitive("rotation").getAsString()
        .equals(state.getRotation().name().toLowerCase()))
        return false;
      if (object.has("properties")) {
        Property textures = state.getTileEntity()
          .getGameProfile()
          .getProperties()
          .get("textures")
          .stream()
          .filter(property -> property.getName().equals("textures"))
          .findAny()
          .get();

        JsonObject jsonObject = object.getAsJsonObject("textures");
        if (!jsonObject.getAsJsonPrimitive("value").getAsString().equals(textures.getValue())
          || !jsonObject.getAsJsonPrimitive("signature").getAsString().equals(textures.getSignature())) return false;
      }
    }
    return true;
  }
}
