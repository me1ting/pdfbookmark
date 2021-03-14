package bookmark.pdfbox;

import bookmark.Bookmark;
import bookmark.Bookmarks;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDBookmarkUtils {
    public static Bookmark fromOutlineItem(PDOutlineItem item, PDDocument doc, int level) {
        int page = -1;
        try {
            if (item.getDestination() instanceof PDPageDestination) {
                var pd = (PDPageDestination) item.getDestination();
                page = pd.retrievePageNumber() + 1;
            } else if (item.getDestination() instanceof PDNamedDestination) {
                var pd = doc.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) item.getDestination());
                if (pd != null) {
                    page = pd.retrievePageNumber() + 1;
                }
            }
        } catch (IOException e) {
            //page = -1;
        }

        var bookmark = new Bookmark();
        bookmark.setPage(page);
        bookmark.setLevel(level);
        bookmark.setTitle(item.getTitle());
        bookmark.setSubs(fromOutline(item, doc, level + 1));

        return bookmark;
    }

    public static List<Bookmark> fromOutline(PDOutlineNode node, PDDocument doc, int level) {
        if (node == null) {
            return Collections.emptyList();
        }

        var list = new ArrayList<Bookmark>();
        var current = node.getFirstChild();
        while (current != null) {
            var bookmark = fromOutlineItem(current, doc, level);
            list.add(bookmark);
            current = current.getNextSibling();
        }

        return list;
    }

    public static PDOutlineItem toOutlineItem(Bookmark bookmark, PDDocument doc, Bookmarks bookmarks, Map<String, Integer> pageMap) {
        var item = new PDOutlineItem();

        var dest = new PDPageFitDestination();

        var zeroBasedPage = bookmark.getPage() - 1;
        if (bookmarks.isLabel()) {
            var label = bookmark.getLabel();
            if (!label.isEmpty()) {
                zeroBasedPage = pageMap.getOrDefault(label, -1);
            }
        }

        if (zeroBasedPage >= 0) {
            dest.setPage(doc.getPage(zeroBasedPage));
        }

        item.setTitle(bookmark.getTitle());
        item.setDestination(dest);
        for (var sub : bookmark.getSubs()) {
            item.addLast(toOutlineItem(sub, doc, bookmarks, pageMap));
        }

        item.openNode();

        return item;
    }

    public static PDDocumentOutline toOutline(Bookmarks bookmarks, PDDocument doc) throws IOException {
        var outline = new PDDocumentOutline();
        doc.getDocumentCatalog().setDocumentOutline(outline);
        var bookmarkList = bookmarks.getRoot();
        Map<String, Integer> pageMap = Collections.emptyMap();
        if (bookmarks.isLabel()) {
            pageMap = new HashMap<String, Integer>();
            var labelMap = doc.getDocumentCatalog().getPageLabels().getLabelsByPageIndices();
            for (var i = 0; i < labelMap.length; i++) {
                var label = labelMap[i];
                if (!label.isEmpty()) {
                    pageMap.put(label, i);
                }
            }
        }
        for (var bookmark : bookmarkList) {
            outline.addLast(toOutlineItem(bookmark, doc, bookmarks, pageMap));
        }

        outline.openNode();

        return outline;
    }

    public static Bookmarks loadFromPDF(String file) throws IOException {
        try (var doc = PDDocument.load(new File(file))) {
            return doLoadFromPDF(doc, checkLabel(doc));
        }
    }

    public static boolean checkLabel(PDDocument doc) throws IOException {
        var labels = doc.getDocumentCatalog().getPageLabels();
        var map = labels.getLabelsByPageIndices();
        if (map == null) {
            return false;
        }
        for (var label : map) {
            if (label.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static Bookmarks loadFromPDF(String file, boolean label) throws IOException {
        try (var doc = PDDocument.load(new File(file))) {
            return doLoadFromPDF(doc, label);
        }
    }

    private static Bookmarks doLoadFromPDF(PDDocument doc, boolean label) throws IOException {
        var outline = doc.getDocumentCatalog().getDocumentOutline();
        List<Bookmark> bookmarkList = fromOutline(outline, doc, 1);
        if (label) {
            var labelMap = doc.getDocumentCatalog().getPageLabels().getLabelsByPageIndices();
            fillLabels(bookmarkList, labelMap);
        }

        Bookmarks bookmarks = new Bookmarks();
        bookmarks.setRoot(bookmarkList);
        bookmarks.setLabel(label);
        bookmarks.setOffset(0);

        return bookmarks;
    }

    private static void fillLabels(List<Bookmark> list, String[] labelMap) {
        for (var bookmark : list) {
            var zeroBasedPage = bookmark.getPage() - 1;
            if (zeroBasedPage >= 0) {
                if (labelMap.length > zeroBasedPage) {
                    bookmark.setLabel(labelMap[zeroBasedPage]);
                }
            }
            fillLabels(bookmark.getSubs(), labelMap);
        }
    }


    public static void saveToPDF(Bookmarks bookmarks, String infile, String outfile) throws IOException {
        try (var doc = PDDocument.load(new File(infile))) {
            toOutline(bookmarks, doc);
            doc.save(outfile);
        }
    }
}
