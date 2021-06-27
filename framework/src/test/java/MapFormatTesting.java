///*
// * Copyright (c) 2021 Emmanuel Lampe
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
// * OR OTHER DEALINGS IN THE SOFTWARE.
// */
//
//import com.github.luben.zstd.Zstd;
//import com.google.gson.GsonBuilder;
//import de.rexlmanu.pluginstube.framework.map.format.MapFormatFactory;
//import de.rexlmanu.pluginstube.framework.map.format.MapIngredient;
//import de.rexlmanu.pluginstube.framework.map.format.MapPosition;
//import de.rexlmanu.pluginstube.framework.map.format.MapSchema;
//import lombok.experimental.Accessors;
//import org.bukkit.Material;
//
//import java.io.*;
//import java.util.*;
//import java.util.zip.DataFormatException;
//import java.util.zip.Deflater;
//import java.util.zip.Inflater;
//
//@Accessors(fluent = true)
//public class MapFormatTesting {
//  public static void main(String[] args) {
//    Map<String, MapPosition> positionMap = new HashMap<>();
//    Random random = new Random();
//    for (int i = 0; i < 30; i++) {
//      positionMap.put(UUID.randomUUID().toString(), new MapPosition(
//        random.nextInt(100),
//        random.nextInt(100),
//        random.nextInt(100),
//        random.nextFloat(),
//        random.nextFloat())
//      );
//    }
//    List<MapIngredient> ingredients = new ArrayList<>();
//    for (int i = 0; i < 10; i++) {
//      ingredients.add(new MapIngredient(
//        Material.values()[random.nextInt(Material.values().length)].name().toLowerCase(),
//        random.nextInt(3)
//      ));
//    }
//    int[][][] layout = new int[200][60][100];
//    for (int x = 0; x < 100; x++) {
//      for (int y = 0; y < 60; y++) {
//        for (int z = 0; z < 100; z++) {
//          layout[x][y][z] = random.nextInt(10);
//        }
//      }
//    }
//    MapSchema mapSchema = new MapSchema(
//      "Example",
//      Map.of("descriptionĳ", "Example"),
//      positionMap,
//      ingredients,
//      layout
//    );
//
//    // gzip
//    try {
//      byte[] write = MapFormatFactory.write(mapSchema);
//      FileOutputStream fileOutputStream = new FileOutputStream(new File("example-gzip.map"));
//      fileOutputStream.write(write);
//      fileOutputStream.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    // base64
//    try {
//      byte[] write = MapFormatFactory.write(mapSchema);
//      FileOutputStream fileOutputStream = new FileOutputStream(new File("example-base64.map"));
//      fileOutputStream.write(Base64.getEncoder().encode(write));
//      fileOutputStream.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    // json
//    try {
//      FileOutputStream fileOutputStream = new FileOutputStream(new File("example-json.map"));
//      fileOutputStream.write(new GsonBuilder().serializeNulls().create().toJson(mapSchema).getBytes());
//      fileOutputStream.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    // zlib
//    try {
//      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//
//      objectOutputStream.writeObject(mapSchema);
//      byteArrayOutputStream.flush();
//
//      byte[] bytes = byteArrayOutputStream.toByteArray();
//      objectOutputStream.close();
//
//      FileOutputStream fileOutputStream = new FileOutputStream(new File("example-zlib.map"));
//      fileOutputStream.write(compress(bytes));
//      fileOutputStream.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    // gzip + zlib
//    try {
//      byte[] write = MapFormatFactory.write(mapSchema);
//      FileOutputStream fileOutputStream = new FileOutputStream(new File("example-zlib-gzip.map"));
//      fileOutputStream.write(compress(write));
//      fileOutputStream.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    // zstd
//    try {
//      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//
//      objectOutputStream.writeObject(mapSchema);
//      byteArrayOutputStream.flush();
//
//      byte[] bytes = byteArrayOutputStream.toByteArray();
//      objectOutputStream.close();
//
//      FileOutputStream fileOutputStream = new FileOutputStream(new File("example-zstd.map"));
//      fileOutputStream.write(Zstd.compress(bytes));
//      fileOutputStream.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static byte[] compress(byte[] data) throws IOException {
//    Deflater deflater = new Deflater();
//    deflater.setInput(data);
//
//    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
//
//    deflater.finish();
//    byte[] buffer = new byte[16];
//    while (!deflater.finished()) {
//      int count = deflater.deflate(buffer); // returns the generated code... index
//      outputStream.write(buffer, 0, count);
//    }
//    outputStream.close();
//    byte[] output = outputStream.toByteArray();
//
//    System.out.println("Original: " + data.length / 1024 + " Kb");
//    System.out.println("Compressed: " + output.length / 1024 + " Kb");
//    return output;
//  }
//
//  public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
//    Inflater inflater = new Inflater();
//    inflater.setInput(data);
//
//    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
//    byte[] buffer = new byte[1024];
//    while (!inflater.finished()) {
//      int count = inflater.inflate(buffer);
//      outputStream.write(buffer, 0, count);
//    }
//    outputStream.close();
//    byte[] output = outputStream.toByteArray();
//
//    System.out.println("Original: " + data.length);
//    System.out.println("Compressed: " + output.length);
//    return output;
//  }
//}
