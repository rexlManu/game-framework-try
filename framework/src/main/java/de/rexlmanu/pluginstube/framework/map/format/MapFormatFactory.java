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

import lombok.experimental.Accessors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Accessors(fluent = true)
public class MapFormatFactory {

  public static byte[] write(MapSchema schema) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream);

      objectOutputStream.writeObject(schema);
      gzipOutputStream.finish();
      byteArrayOutputStream.flush();

      byte[] bytes = byteArrayOutputStream.toByteArray();
      objectOutputStream.close();
      return bytes;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public static MapSchema read(byte[] bytes) {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
      GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
      ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);
      MapSchema mapSchema = (MapSchema) objectInputStream.readObject();
      objectInputStream.close();
      return mapSchema;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

}
