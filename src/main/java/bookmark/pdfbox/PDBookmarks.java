package bookmark.pdfbox;

import bookmark.Bookmark;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDBookmarks {
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

    public static PDOutlineItem toOutlineItem(Bookmark bookmark, PDDocument doc) {
        var item = new PDOutlineItem();

        var dest = new PDPageFitDestination();
        if (bookmark.getPage() > 0) {
            dest.setPage(doc.getPage(bookmark.getPage() - 1));
        }

        item.setTitle(bookmark.getTitle());
        item.setDestination(dest);
        for (var sub : bookmark.getSubs()) {
            item.addLast(toOutlineItem(sub, doc));
        }

        item.openNode();

        return item;
    }

    public static PDDocumentOutline toOutline(List<Bookmark> bookmarks, PDDocument doc) {
        var outline = new PDDocumentOutline();
        doc.getDocumentCatalog().setDocumentOutline(outline);
        for (var bookmark : bookmarks) {
            outline.addLast(toOutlineItem(bookmark, doc));
        }

        outline.openNode();

        return outline;
    }

    public static List<Bookmark> loadFromPDF(String file) throws IOException {
        try (var doc = PDDocument.load(new File(file))) {
            var outline = doc.getDocumentCatalog().getDocumentOutline();
            return fromOutline(outline, doc, 1);
        }
    }

    public static void saveToPDF(List<Bookmark> bookmarks, String infile, String outfile) throws IOException {
        try (var doc = PDDocument.load(new File(infile))) {
            toOutline(bookmarks, doc);
            doc.save(outfile);
        }
    }
}
