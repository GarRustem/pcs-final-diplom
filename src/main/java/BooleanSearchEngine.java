import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    protected String[] words; // Заготовка. Будем собирать слова из текущей страницы, разделенные по regex, в массив.
    protected Map<String, List<PageEntry>> result = new HashMap<>(); // Заготовка. Результирующий Map. Будем собирать для каждого ключа-слова соответствующее значение-PageEntry().
    protected List<PageEntry> pageEntry;

    public BooleanSearchEngine(File pdfsDir) throws IOException { // pdfsDir - директория для поиска, List<File> fileList - list для найденных файлов.
        try {

            if (pdfsDir.isDirectory()) { // Проверка корректности пути - pdfsDir является папкой.

                System.out.println("Searching: " + pdfsDir.getAbsolutePath()); // Для справки.
                File[] directoryFiles = pdfsDir.listFiles(); // Собираем массив файлов из текущей папки.

                if (directoryFiles != null) {
                    for (File file : directoryFiles) {
                        PdfDocument doc = new PdfDocument(new PdfReader(file)); // Создаем PDFReader для каждого файла.
                        int pages = doc.getNumberOfPages(); // Получаем количество страниц для каждого файла.

                        for (int i = 1; i <= pages; i++) { // Итерируем по страницам каждого файла.
                            Map<String, Integer> frequency = new HashMap<>();
                            String pageContent = PdfTextExtractor.getTextFromPage(doc.getPage(i)); // Получаем контент каждой страницы файла.
                            words = pageContent.split("\\P{IsAlphabetic}+"); // Делим собранный текст на отдельные слова и собираем в массив.
                            for (String word : words) {
                                if (word.isEmpty()) {
                                    continue;
                                }
                                word = word.toLowerCase();
                                frequency.put(word, frequency.getOrDefault(word, 0) + 1); // Собираем промежуточный Map из слов и их количества в пределах каждого файла.
                            }

                            for (Map.Entry<String, Integer> iteration : frequency.entrySet()) {
                                String keyWord = iteration.getKey(); // Для каждой пары промежуточного Map "ключ (слово)- значение (количество)" извлекаем ключ.
                                if (!result.containsKey(keyWord)) { // Если результирующий Map поиска не содержит ключ промежуточного Map, заполняем новую пару "Слово - PageEntry"
                                    List<PageEntry> newEntry = new ArrayList<>();
                                    newEntry.add(new PageEntry(doc.getDocumentInfo().getTitle(), i, iteration.getValue()));
                                    result.put(keyWord, newEntry);
                                } else { // Если ключ промежуточного Map встретился в результирующем Map поиска (по результатам сканирования предыдущих файлов или страниц), обновляем List<PageEntry> для ключа.
                                    result.get(keyWord).add(new PageEntry(doc.getDocumentInfo().getTitle(), i, iteration.getValue()));
                                }
                                Collections.sort(result.get(keyWord));
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
            pageEntry = result.get(word.toLowerCase());
        } else {
            pageEntry = new ArrayList<>();
        }
        return pageEntry;
    }
}
