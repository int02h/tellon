package com.dpforge.mailnotifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readResource(String name) throws IOException {
        InputStream resourceStream = FileUtils.class.getClassLoader().getResourceAsStream(name);
        if (resourceStream == null) {
            throw new IOException("Could not find resource " + name);
        }
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }

        return builder.toString();
    }
}
