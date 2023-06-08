package com.rc.io.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFileCopy {
    public static void main(String[] args) throws Exception {
        String source = "/Users/yohanes/.config";
        String target = "world.txt";
        Files.walk(Paths.get(source)).forEach(path -> {
            String targetName = path.toString().replace(source, target);
            if (Files.isDirectory(path)) {
                //目录
                try {
                    Files.createDirectory(Paths.get(targetName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (Files.isRegularFile(path)) {
                //普通文件
                try {
                    Files.copy(path, Paths.get(targetName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
