import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class JAsmIO {
    private static final String ext = ".jasm";

    // method to open a file and to check its extension
    public static String[] OpenFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found");
        }
        if (!file.getName().endsWith(ext)) {
            throw new IOException("File extension not supported");
        }
        return Parse(file);
    }

    private static String[] Parse(File file) {
        try {
            return Files.readAllLines(file.toPath()).toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
