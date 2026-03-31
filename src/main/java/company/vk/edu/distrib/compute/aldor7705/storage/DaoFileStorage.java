package company.vk.edu.distrib.compute.aldor7705.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DaoFileStorage {

    private final Path filePath;

    public DaoFileStorage(Path filePath) {
        this.filePath = filePath;
    }

    public void clearFile() {
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при очистке файла", e);
        }
    }

    public void save(String key, byte[] value) {
        Map<String, byte[]> map = readAllFromFile();
        map.put(key, value);
        writeAllToFile(map);
    }

    public byte[] readFromFile(String key) {
        Map<String, byte[]> map = readAllFromFile();
        byte[] value = map.get(key);
        if (value == null) {
            throw new NoSuchElementException("Элемент с ключом " + key + " не найден");
        }
        return value;
    }

    public void deleteFromFile(String key) {
        Map<String, byte[]> map = readAllFromFile();
        map.remove(key);
        writeAllToFile(map);
    }

    private void writeAllToFile(Map<String, byte[]> map) {
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(filePath))) {
            for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                byte[] value = entry.getValue();
                byte[] keyBytes = entry.getKey().getBytes(UTF_8);
                dos.writeInt(keyBytes.length);
                dos.write(keyBytes);
                dos.writeInt(value.length);
                dos.write(value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи в файл", e);
        }
    }

    private Map<String, byte[]> readAllFromFile() {
        Map<String, byte[]> map = new ConcurrentHashMap<>();
        if (!Files.exists(filePath)) {
            return map;
        }

        try (DataInputStream dis = new DataInputStream(Files.newInputStream(filePath))) {
            while (dis.available() > 0) {
                byte[] keyBytes = new byte[dis.readInt()];
                dis.readFully(keyBytes);
                String key = new String(keyBytes, UTF_8);
                byte[] value = new byte[dis.readInt()];
                dis.readFully(value);
                map.put(key, value);
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }

}
