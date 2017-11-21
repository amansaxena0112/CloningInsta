package com.example.user.insta.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by USER on 11/18/2017.
 */

public class FileSearch {

    //search a directory and return a list of directories contained inside
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (int i=0; i<listFiles.length; i++){
            if (listFiles[i].isDirectory()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    //search a directory and return a list of files contained inside
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (int i=0; i<listFiles.length; i++){
            if (listFiles[i].isFile()){
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
