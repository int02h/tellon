package com.dpforge.tellon.app.config;

import java.io.IOException;

public abstract class AppConfigReader {

    abstract AppConfig readConfig(final String path) throws IOException;

    public static AppConfig read(final String path) throws IOException {
        final int dotIndex = path.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex + 1 < path.length()) {
            final String extension = path.substring(dotIndex + 1);
            AppConfigReader config = createReader(extension);
            return config.readConfig(path);
        }
        throw new IOException("Empty file extension");
    }

    private static AppConfigReader createReader(final String extension) throws IOException {
        switch (extension) {
            case "xml":
                return new XmlAppConfigReader();
            default:
                throw new IOException("Unsupported config format: " + extension);
        }
    }
}
