public class PageEntry implements Comparable<PageEntry> {
    protected String pdfName;
    protected int page;
    protected int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public String toString() {
        return "PageEntry {"
                + "pdf = " + pdfName
                + ", page = " + page
                + ", count = " + count
                + "}";
    }

    @Override
    public int compareTo(PageEntry o) {
        return o.count - count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }
}
