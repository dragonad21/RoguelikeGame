package datalayer.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void save(String filename, Object data) {
        try {
            File file = new File(filename);

            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                if (!created) {
                    System.err.println("Failed to create directory: " + parent.getAbsolutePath());
                }
            }

            mapper.writeValue(new File(filename), data);
        } catch (IOException e) {
            System.err.println("Failed to save: " + e.getMessage());
        }
    }

    public static <T> T load(String filename, Class<T> clazz) {
        try {
            File file = new File(filename);
            if (!file.exists()) return null;
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            System.err.println("Failed to load: " + e.getMessage());
            return null;
        }
    }
}
