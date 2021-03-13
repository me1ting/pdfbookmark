package bookmark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bookmarks {
    private Bookmarks() {
    }

    public static List<Bookmark> fromString(String content) throws IllegalArgumentException {
        var scanner = new Scanner(content);
        List<Bookmark> bookmarks = new ArrayList<>();
        var stack = new ArrayDeque<Bookmark>();
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (!line.trim().isEmpty()) {
                var item = Bookmark.fromString(line);
                if (item.getLevel() == 1) {
                    stack.clear();
                    stack.push(item);
                    bookmarks.add(item);
                } else {
                    while (stack.peek().getLevel() >= item.getLevel()) {
                        stack.pop();
                    }
                    var top = stack.peek();
                    if (top.getLevel() + 1 != item.getLevel()) {
                        throw new IllegalArgumentException(String.format(
                                "\r\n%s\r\n, followed with \r\n%s\r\n,the level difference is bigger than 1",
                                top.toString(),
                                item.toString()
                        ));
                    }
                    top.getSubs().add(item);
                    stack.push(item);
                }
            }
        }
        return bookmarks;
    }

    public static String toString(List<Bookmark> bookmarks) {
        var builder = new StringBuilder();
        for (var bookmark : bookmarks) {
            builder.append(bookmark.toString());
        }
        return builder.toString();
    }

    public static List<Bookmark> loadFromFile(String file) throws IOException {
        String content = Files.readString(Path.of(file), StandardCharsets.UTF_8);
        return fromString(content);
    }

    public static void saveToFile(List<Bookmark> bookmarks, String file) throws IOException {
        Files.writeString(Path.of(file), toString(bookmarks), StandardCharsets.UTF_8);
    }
}
