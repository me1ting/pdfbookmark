package bookmark;

import java.util.ArrayList;
import java.util.List;

public class Bookmark {
    private int level;
    private String title;
    private int page;
    private List<Bookmark> subs;

    public Bookmark() {
        this.subs = new ArrayList<>();
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var i = 0; i < level - 1; i++) {
            builder.append('\t');
        }
        builder.append(title);
        builder.append('\t');
        builder.append(page);
        builder.append('\n');
        for (var sub : subs) {
            builder.append(sub.toString());
        }

        return builder.toString();
    }

    public static Bookmark fromString(String content) throws IllegalArgumentException {
        var i = 0;
        while (i < content.length() && content.charAt(i) == '\t') {
            i++;
        }
        var level = i + 1;

        var begin = i;
        while (i < content.length() && content.charAt(i) != '\t') {
            i++;
        }
        var title = content.substring(begin, i);
        while (i < content.length() && content.charAt(i) == '\t') {
            i++;
        }

        int page = -1;
        if (content.charAt(i) != '-') {
            begin = i;
            while (i < content.length() && '0' <= content.charAt(i) && content.charAt(i) <= '9') {
                i++;
            }
            page = Integer.parseInt(content.substring(begin, i));
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setTitle(title);
        bookmark.setPage(page);
        bookmark.setLevel(level);

        return bookmark;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Bookmark> getSubs() {
        return subs;
    }

    public void setSubs(List<Bookmark> subs) {
        this.subs = subs;
    }
}
