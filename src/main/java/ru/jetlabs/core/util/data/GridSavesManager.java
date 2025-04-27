package ru.jetlabs.core.util.data;

import ru.jetlabs.core.util.data.dto.GridSave;


import java.io.File;
import java.util.Map;

public class GridSavesManager {
    private final static String location = "src/main/resources/saves/grid";
    private final static Map<Integer, String> foundedSaves = init();


    private static Map<Integer, String> init(){
        //ищет файлы, выводит список найденных, возвращает мап с найденными, или пустой мап
    };

    private File getFile(String name) {
        return null;
    }

    private void writeFile(String name, File file) {

    }

    public GridSave getSave(Integer i){
        return null;
    }

    public Map<Integer, String> getFoundedSaves(){
        return foundedSaves;
    }

}
