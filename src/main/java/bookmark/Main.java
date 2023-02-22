package bookmark;

import bookmark.pdfbox.PDBookmarkUtils;

public class Main {
    public static final String BOOKMARKS_FILE_SUFFIX = ".bookmarks";
    public static final String USAGE = "PDF bookmarks management tool.\r\n" +
            "  Usage:\r\n" +
            "    pdfbookmark load IN.pdf    load bookmarks from IN.pdf, save as IN.pdf.bookmarks\r\n" +
            "    pdfbookmark save IN.pdf [OUT.pdf]    save bookmarks to IN.pdf, save as OUT.pdf\r\n";

    public static void main(String[] args) {
        try {
            work(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void work(String[] args) throws Exception {
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
                    var outFile = args.length > 2 ? args[2] : inFile + ".saved";
                    saveBookmarks(inFile, outFile);
                    break;
                }
                default:
                    usage();
            }
        }
    }

    public static void loadBookmarks(String inFile) throws Exception {
        Bookmarks bookmarks = PDBookmarkUtils.loadFromPDF(inFile);
        BookmarkUtils.saveToFile(bookmarks, inFile + BOOKMARKS_FILE_SUFFIX);
    }

    public static void saveBookmarks(String inFile, String outFile) throws Exception {
        Bookmarks bookmarks = BookmarkUtils.loadFromFile(inFile + BOOKMARKS_FILE_SUFFIX);
        PDBookmarkUtils.saveToPDF(bookmarks, inFile, outFile);
    }

    private static void usage() {
        System.out.println(USAGE);
    }
}
