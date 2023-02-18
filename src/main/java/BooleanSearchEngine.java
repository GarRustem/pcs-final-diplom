import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    protected String[] words; // Words from page text, divided by regex.
    protected Map<String, Integer> frequency = new HashMap<>(); // Key - word, value - frequency.
    protected List<PageEntry> entry = new ArrayList<>(); // Store each search result coated to PageEntry.
    protected Map<String, List<PageEntry>> result = new HashMap<>(); // Store search result and link it to PageEntry.

    public BooleanSearchEngine(File pdfsDir) throws IOException { // pdfsDir - directory for search, List<File> fileList - list for found files.
        try {
            if (pdfsDir.isDirectory()) { // Check if path lead to directory.
                System.out.println("Searching: " + pdfsDir.getAbsolutePath()); // Info.
                File[] directoryFiles = pdfsDir.listFiles(); // Create array of files from directory.
                if (directoryFiles != null) {
                    for (File file : directoryFiles) {
                        PdfDocument doc = new PdfDocument(new PdfReader(file)); // Create PDFReader.
                        int pages = doc.getNumberOfPages(); // Get the number of pages in pdf.
                        for (int i = 1; i <= pages; i++) { // Iterate the pdf through pages.
                            String pageContent = PdfTextExtractor.getTextFromPage(doc.getPage(i)); // Obtain the page content.
                            words = pageContent.split("\\P{IsAlphabetic}+"); // Store words from the page to array.
                            for (String word : words) {
                                if (word.isEmpty()) {
                                    continue;
                                }
                                word = word.toLowerCase();
                                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
                            }
                            for(Map.Entry<String, Integer> iterationSet : frequency.entrySet()) {
                                String keyWord = iterationSet.getKey();
                                if(!result.containsKey(keyWord)) {
                                    List<PageEntry> newEntry = new ArrayList<>();
                                    newEntry.add(new PageEntry(doc.getDocumentInfo().getTitle(), pages, iterationSet.getValue()));
                                    result.put(keyWord, newEntry);
                                } else {
                                    result.get(keyWord).add(new PageEntry(doc.getDocumentInfo().getTitle(), pages, iterationSet.getValue()));
                                }
                            }
                        }
                        doc.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        if (result.containsKey(word.toLowerCase())) {
            List<PageEntry> sort = result.get(word);
            return sort.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        } else {
            List<PageEntry> basicList = new ArrayList<>();
            basicList.add(new PageEntry("Basic.pdf", 0, 0));
            return basicList;
        }
    }
}
