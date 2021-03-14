package bookmark;

import java.util.ArrayList;
import java.util.List;

public class Bookmark {
    private int level;
    private String title;
    private int page;
    private String label;
    private List<Bookmark> subs;

    public Bookmark() {
        this.subs = new ArrayList<>();
        this.title = "";
        this.label = "";
    }

    @Override
    public String toString(){
        return "Please call toString(label)";
    }

    public String toString(boolean label) {
        var builder = new StringBuilder();
        for (var i = 0; i < level - 1; i++) {
            builder.append('\t');
        }
        builder.append(title);
        builder.append('\t');
        if (label) {
            builder.append(this.label);
        } else {
            builder.append(page);
        }
        builder.append('\n');
        for (var sub : subs) {
            builder.append(sub.toString(label));
        }

        return builder.toString();
    }

    public static Bookmark fromString(String content, boolean label) throws IllegalArgumentException {
        Bookmark bookmark = new Bookmark();

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

        if (label) {
            bookmark.setLabel(content.substring(i));
        } else {
            bookmark.setPage(Integer.parseInt(content.substring(i)));
        }

        bookmark.setTitle(title);
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Bookmark> getSubs() {
        return subs;
    }

    public void setSubs(List<Bookmark> subs) {
        this.subs = subs;
    }
}
