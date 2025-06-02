package ru.jetlabs.core.util.data;

import ru.jetlabs.core.objects.components.ComponentGrid;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GridSavesManager {
    private final static String location = "src/main/resources/saves/grid";
    private final static Map<Integer, String> foundedSaves = init();

    private static Map<Integer, String> init() {
        Map<Integer, String> saves = new HashMap<>();
        File folder = new File(location);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
            return saves;
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".grid"));
        if (files != null) {
            int index = 1;
            for (File file : files) {
                saves.put(index++, file.getName());
            }
        }
        return saves;
    }

    private File getFile(String name) {
        return new File(location, name);
    }

    public void writeFile(String name, ComponentGrid grid) {
        File file = getFile(name);

        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(grid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ComponentGrid getSave(Integer i) {
        String fileName = foundedSaves.get(i);
        if (fileName == null) {
            System.out.println("Сохранение с номером " + i + " не найдено.");
            return null;
        }
        File file = getFile(fileName);
        if (!file.exists()) {
            System.out.println("Файл " + file.getAbsolutePath() + " не существует.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof ComponentGrid) {
                return (ComponentGrid) obj;
            } else {
                System.out.println("Ошибка: объект в файле не является ComponentGrid.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, String> getFoundedSaves() {
        return foundedSaves;
    }
}
