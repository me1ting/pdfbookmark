package bookmark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BookmarkUtils {
    private BookmarkUtils() {
    }

    public static Bookmarks loadFromFile(String file) throws IOException {
        String content = Files.readString(Path.of(file), StandardCharsets.UTF_8);
        return Bookmarks.fromString(content);
    }

    public static void saveToFile(Bookmarks bookmarks, String file) throws IOException {
        Files.writeString(Path.of(file), bookmarks.toString(), StandardCharsets.UTF_8);
    }
}
