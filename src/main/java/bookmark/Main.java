package bookmark;

import bookmark.pdfbox.PDBookmarks;

import java.util.List;

public class Main {
    public static final String BOOKMARKS_FILE_SUFFIX = ".bookmarks";
    public static final String USAGE = "PDF bookmarks management tool.\r\n" +
            "  Usage:\r\n" +
            "    pdfbookmark load IN.pdf    load bookmarks from IN file\r\n" +
            "    pdfbookmark save IN.pdf [OUT.pdf]    save bookmarks to IN file, save as OUT\r\n";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            usage();
        } else {
            var command = args[0];
            var inFile = args[1];
            switch (command) {
                case "load": {
                    loadBookmarks(inFile);
                    break;
                }
                case "save": {
                    var outFile = args.length > 2 ? args[2] : inFile;
                    saveBookmarks(inFile, outFile);
                    break;
                }
                default:
                    usage();
            }
        }
    }

    public static void loadBookmarks(String inFile) throws Exception {
        List<Bookmark> bookmarks = PDBookmarks.loadFromPDF(inFile);
        Bookmarks.saveToFile(bookmarks, inFile + BOOKMARKS_FILE_SUFFIX);
    }

    public static void saveBookmarks(String inFile, String outFile) throws Exception {
        List<Bookmark> bookmarks = Bookmarks.loadFromFile(inFile + BOOKMARKS_FILE_SUFFIX);
        PDBookmarks.saveToPDF(bookmarks, inFile, outFile);
    }

    private static void usage() {
        System.out.println(USAGE);
    }
}
