package com.dpforge.mailnotifier;

import java.io.*;

public class FileUtils {
    public static String readFile(final String fileName) throws IOException {
        return readFile(new File(fileName));
    }

    public static String readFile(final File file) throws IOException {
        if (file == null) {
            return null;
        }

        final BufferedReader reader = new BufferedReader(new FileReader(file));
        final StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }
}
