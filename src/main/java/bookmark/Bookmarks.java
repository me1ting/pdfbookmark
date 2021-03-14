package bookmark;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bookmarks {
    private List<Bookmark> root;
    private int offset;
    private boolean label;

    public static Bookmarks fromString(String content) throws IllegalArgumentException {
        var scanner = new Scanner(content);
        List<Bookmark> root = new ArrayList<>();
        var stack = new ArrayDeque<Bookmark>();
        int offset = 0;
        boolean label = false;

        boolean metaEnd = false;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (!metaEnd) {
                if (!line.isEmpty()&&line.charAt(0) == '@') {
                    if (line.startsWith("@offset")) {
                        offset = Integer.parseInt(line.substring(("@offset").length()).trim());
                    } else if (line.startsWith("@label")) {
                        if (line.toLowerCase().contains("true")) {
                            label = true;
                        }
                    }
                    continue;
                } else if (line.trim().isEmpty()) {
                    metaEnd = true;
                }
            }

            if (!line.trim().isEmpty()) {
                var item = Bookmark.fromString(line, label);
                if (item.getLevel() == 1) {
                    stack.clear();
                    stack.push(item);
                    root.add(item);
                } else {
                    while (stack.peek().getLevel() >= item.getLevel()) {
                        stack.pop();
                    }
                    var top = stack.peek();
                    if (top.getLevel() + 1 != item.getLevel()) {
                        throw new IllegalArgumentException(String.format(
                                "\r\n%s\r\n, followed with \r\n%s\r\n, the level difference is bigger than 1",
                                top.toString(label),
                                item.toString(label)
                        ));
                    }
                    top.getSubs().add(item);
                    stack.push(item);
                }
            }
        }

        var bookmarks = new Bookmarks();
        bookmarks.root = root;
        bookmarks.offset = offset;
        bookmarks.label = label;

        return bookmarks;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        if (offset != 0) {
            builder.append("@offset ");
            builder.append(offset);
            builder.append("\r\n");
        }
        if (label) {
            builder.append("@label ");
            builder.append("true");
            builder.append("\r\n");
        }
        builder.append("\r\n");
        for (var bookmark : root) {
            builder.append(bookmark.toString(label));
        }
        return builder.toString();
    }

    public List<Bookmark> getRoot() {
        return root;
    }

    public void setRoot(List<Bookmark> root) {
        this.root = root;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isLabel() {
        return label;
    }

    public void setLabel(boolean label) {
        this.label = label;
    }
}
