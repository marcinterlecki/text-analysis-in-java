import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Wyświetlenia powitania dla użytkownika
        System.out.println("\nWitaj w programie do podsumowywania dokumentów!");
        System.out.println("\nProgram ten pozwala na podsumowanie dokumentu tekstowego w formacie .txt.\n");
         // Pobieranie od użytkownika nazwy pliku tekstowego do przetworzenia
        String fileName = getInput("Podaj pełną nazwę pliku tekstowego (np. regresja.txt) do przetworzenia:", scanner,
                input -> input.endsWith(".txt"));
        // Odczyt pliku tekstowego        
        String document = readTextFile(fileName);
        while (document == null) {
            // W przypadku błędu odczytu pliku, użytkownik jest proszony o podanie nazwy pliku ponownie
            System.out.println("Błąd odczytu pliku. Spróbuj ponownie.");
            fileName = getInput("Podaj pełną nazwę pliku tekstowego (np. regresja.txt) do przetworzenia:", scanner,
                    input -> input.endsWith(".txt"));
            document = readTextFile(fileName);
        }
        // Wyświetlenie zawartości pliku tekstowego
        System.out.println("\n");
        System.out.println("Zawartość pliku: " + fileName + "\n-------------------------------\n" );
        System.out.println(document + "\n-------------------------------\n");
        // Podział dokumentu na zdania
        List<String> sentences = splitIntoSentences(document);
        // Tokenizacja zdań (podział na słowa)
        List<List<String>> tokenizedSentences = tokenizeSentences(sentences);
        // Usunięcie słów stopu
        List<List<String>> cleanedSentences = removeStopWords(tokenizedSentences);

        // Obliczanie statystyk dla dokumentu
        int totalWords = calculateTotalWords(cleanedSentences);
        double averageWordsPerSentence = calculateAverageWordsPerSentence(cleanedSentences);
        int sentenceCount = cleanedSentences.size();
        int uniqueWordCount = calculateUniqueWordCount(cleanedSentences);
        int totalCharacters = calculateTotalCharacters(cleanedSentences);
        int nonEmptySentenceCount = countNonEmptySentences(cleanedSentences);
        int longestSentenceLength = findLongestSentenceLength(cleanedSentences);
        int shortestSentenceLength = findShortestSentenceLength(cleanedSentences);
        String mostFrequentWord = findMostFrequentWord(cleanedSentences);
        String leastFrequentWord = findLeastFrequentWord(cleanedSentences);

        // Wyświetlanie statystyk opisowych
        System.out.println("Statystyki opisowe:\n");
        System.out.println("Liczba słów w całym tekście: " + totalWords);
        System.out.println("Średnia liczba słów w zdaniu: " + averageWordsPerSentence);
        System.out.println("Liczba zdań: " + sentenceCount);
        System.out.println("Liczba niepustych zdań: " + nonEmptySentenceCount);
        System.out.println("Liczba unikalnych słów: " + uniqueWordCount);
        System.out.println("Liczba znaków (bez spacji): " + totalCharacters);
        System.out.println("Najdłuższe zdanie: " + longestSentenceLength + " słów");
        System.out.println("Najkrótsze zdanie: " + shortestSentenceLength + " słów");
        System.out.println("Najpopularniejsze słowo: " + mostFrequentWord);
        System.out.println("Najmniej popularne słowo: " + leastFrequentWord);
        System.out.println("");

        // Pobranie liczby potrzebnych wyników TF-IDF do wyświetlenia od użytkownika
        String tfidfLimitInput = getInput("Podaj liczbę wyników TF-IDF do wyświetlenia (np. 3 lub wpisz 'all' dla wyświetlenia wszystkich):", scanner,
                input -> input.equals("all") || input.matches("\\d+"));
        // Jeśli użytkownik podał 'all', to ustawiamy limit na maksymalną wartość
        int tfidfLimit = Integer.MAX_VALUE;
        if (!tfidfLimitInput.equals("all")) {
            // Jeśli uzyskany input nie jest 'all', to parsujemy go na liczbę
            tfidfLimit = Integer.parseInt(tfidfLimitInput);
        }
         // Oblicznie TF-IDF i wyświetlenie wyników
        if (tfidfLimit > 0) {
            Map<String, Integer> termFrequency = calculateTermFrequency(cleanedSentences);
            Map<String, Double> inverseDocumentFrequency = calculateInverseDocumentFrequency(cleanedSentences);
            Map<String, Double> tfidf = calculateTFIDF(termFrequency, inverseDocumentFrequency);
            // Wyświetlenie wyników TF-IDF według wybranego przez użytkownika rodzaju sortowania
            int sortOption = getIntegerInput("Wybierz rodzaj sortowania wyników TF-IDF:\n1. Malejąco\n2. Rosnąco", scanner);
            if (sortOption == 1) {
                System.out.println("Ważne słowa według TF-IDF (malejąco):\n");
                printTFIDFDescending(tfidf, tfidfLimit);
            } else if (sortOption == 2) {
                System.out.println("Ważne słowa według TF-IDF (rosnąco):\n");
                printTFIDFAscending(tfidf, tfidfLimit);
            } else {
                System.out.println("Nieprawidłowy wybór sortowania.");
            }
        }
        // Pożegnanie użytkownika
        System.out.println("\nDziękuje za skorzystanie z programu. Do zobaczenia!\n");

        scanner.close();
    }

    // Metoda do pobierania danych od użytkownika z walidacją
    // prompt - informacja, co ma wprowadzić użytkownik
    // scanner - obiekt do wprowadzania danych przez użytkownika
    // validator - obiekt walidujący dane wprowadzone przez użytkownika
    public static String getInput(String prompt, Scanner scanner, InputValidator validator) {
        String input = "";
        boolean isValid;
        do {    
            System.out.println(prompt);
            input = scanner.nextLine();
            isValid = validator.validate(input);
            if (!isValid) {
                System.out.println("Nieprawidłowy format. Spróbuj ponownie.");
            }
        } while (!isValid);
        return input;
    }

    // Metoda do pobierania danych dla sortowania od użytkownika z walidacją dla liczb całkowitych
    public static int getIntegerInput(String prompt, Scanner scanner) {
        int input = 0;
        boolean isValid;
        do {
            System.out.println(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine());
                isValid = true;
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format liczby. Spróbuj ponownie.");
                isValid = false;
            }
        } while (!isValid);
        return input;
    }

    // Metoda do odczytu pliku tekstowego
    public static String readTextFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    // Metoda do podziału dokumentu na zdania
    public static List<String> splitIntoSentences(String document) {
        // Implementacja podziału dokumentu na zdania
        // W tej implementacji zdania są podzielone po kropce, wykrzykniku lub znaku zapytania
        // Zdania są usuwane, jeśli są puste

        String[] sentencesArray = document.split("[.!?]");
        List<String> sentences = new ArrayList<>(Arrays.asList(sentencesArray));
        sentences.removeIf(String::isBlank);
        return sentences;
    }

    // Metoda do tokenizacji zdania
    public static List<List<String>> tokenizeSentences(List<String> sentences) {
        List<List<String>> tokenizedSentences = new ArrayList<>();
        for (String sentence : sentences) {
            // Implementacja tokenizacji zdania
            // 

            String[] tokensArray = sentence.toLowerCase().split("\\s+");
            List<String> tokens = new ArrayList<>(Arrays.asList(tokensArray));
            tokens.removeIf(String::isEmpty);
            tokenizedSentences.add(tokens);
        }
        return tokenizedSentences;
    }

    // Usuwania słów typu stop-word
    public static List<List<String>> removeStopWords(List<List<String>> tokenizedSentences) {
        List<List<String>> cleanedSentences = new ArrayList<>();
        // Lista słów typu stop-word
        // Zawiera słowa, które nie mają wpływu na znaczenie zdania
        // Przykładowe stop-word pobrane ze strony https://www.ranks.nl/stopwords/polish

        List<String> stopWords = Arrays.asList(
        "ach", "aj", "albo", "bardzo", "bez", "bo", "być", "ci", "cię", "ciebie", "co", 
        "czy", "daleko", "dla", "dlaczego", "dlatego", "do", "dobrze", "dokąd", "dość", "dużo", 
        "dwa", "dwaj", "dwie", "dwoje", "dziś", "dzisiaj", "gdyby", "gdzie", "go", "ich", "ile", 
        "im", "inny", "ja", "ją", "jak", "jakby", "jaki", "je", "jeden", "jedna", "jedno", "jego", 
        "jej", "jemu", "jeśli", "jest", "jestem", "jeżeli	już", "każdy", "kiedy", "kierunku", 
        "kto", "ku", "lub", "ma", "mają", "mam", "mi", "mną", "mnie", "moi", "mój", "moja", "moje", 
        "może", "mu", "my", "na", "nam", "nami", "nas", "nasi", "nasz", "nasza", "nasze", "natychmiast", 
        "nią", "nic", "nich", "nie", "niego", "niej", "niemu", "nigdy", "nim", "nimi", "niż", "obok", "od", 
        "około", "on", "ona", "one", "oni", "ono", "owszem", "po	pod", "ponieważ", "przed", "przedtem", 
        "są", "sam", "sama", "się", "skąd", "tak", "taki", "tam", "ten", "to", "tobą", "tobie", "tu", "tutaj", 
        "twoi", "twój", "twoja", "twoje", "ty", "wam", "wami", "was", "wasi", "wasz", "wasza", "wasze", "we", 
        "więc", "wszystko", "wtedy", "wy", "żaden", "zawsze", "że"); 

        // Usuwanie słów typu stop-word z każdego zdania
        for (List<String> tokens : tokenizedSentences) {
            List<String> cleanedTokens = new ArrayList<>(tokens);
            cleanedTokens.removeAll(stopWords);
            cleanedSentences.add(cleanedTokens);
        }
        return cleanedSentences;
    }

    // Metoda do obliczania liczby słów w całym tekście
    // Jest to suma słów we wszystkich zdaniach
    public static int calculateTotalWords(List<List<String>> cleanedSentences) {
        int totalWords = 0;
        for (List<String> tokens : cleanedSentences) {
            totalWords += tokens.size();
        }
        return totalWords;
    }

    // Metoda do obliczania średniej liczby słów w zdaniu
    // Jest to suma słów we wszystkich zdaniach podzielona przez liczbę zdań
    public static double calculateAverageWordsPerSentence(List<List<String>> cleanedSentences) {
        int totalSentences = cleanedSentences.size();
        int totalWords = calculateTotalWords(cleanedSentences);
        if (totalSentences > 0) {
            return (double) totalWords / totalSentences;
        } else {
            return 0.0;
        }
    }

    // Metoda do obliczania liczby unikalnych słów
    // Jest to liczba słów, które występują tylko raz w tekście
    public static int calculateUniqueWordCount(List<List<String>> cleanedSentences) {
        Set<String> uniqueWords = new HashSet<>();
        for (List<String> tokens : cleanedSentences) {
            uniqueWords.addAll(tokens);
        }
        return uniqueWords.size();
    }

    // Metoda do obliczania liczby znaków w tekście (bez spacji)
    public static int calculateTotalCharacters(List<List<String>> cleanedSentences) {
        int totalCharacters = 0;
        for (List<String> tokens : cleanedSentences) {
            for (String token : tokens) {
                totalCharacters += token.length();
            }
        }
        return totalCharacters;
    }

    // Metoda do zliczania niepustych zdań
    public static int countNonEmptySentences(List<List<String>> cleanedSentences) {
        int count = 0;
        for (List<String> tokens : cleanedSentences) {
            if (!tokens.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    // Metoda do znajdowania długości najdłuższego zdania
    public static int findLongestSentenceLength(List<List<String>> cleanedSentences) {
        int maxLength = 0;
        for (List<String> tokens : cleanedSentences) {
            maxLength = Math.max(maxLength, tokens.size());
        }
        return maxLength;
    }

    // Metoda do znajdowania długości najkrótszego zdania
    public static int findShortestSentenceLength(List<List<String>> cleanedSentences) {
        int minLength = Integer.MAX_VALUE;
        for (List<String> tokens : cleanedSentences) {
            minLength = Math.min(minLength, tokens.size());
        }
        return minLength;
    }

    // Metoda do znajdowania najpopularniejszego słowa
    public static String findMostFrequentWord(List<List<String>> cleanedSentences) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (List<String> tokens : cleanedSentences) {
            for (String token : tokens) {
                wordFrequency.put(token, wordFrequency.getOrDefault(token, 0) + 1);
            }
        }
        int maxFrequency = 0;
        String mostFrequentWord = "";
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentWord = entry.getKey();
            }
        }
        return mostFrequentWord;
    }

    // Metoda do znajdowania najmniej popularnego słowa
    public static String findLeastFrequentWord(List<List<String>> cleanedSentences) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (List<String> tokens : cleanedSentences) {
            for (String token : tokens) {
                wordFrequency.put(token, wordFrequency.getOrDefault(token, 0) + 1);
            }
        }
        int minFrequency = Integer.MAX_VALUE;
        String leastFrequentWord = "";
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            if (entry.getValue() < minFrequency) {
                minFrequency = entry.getValue();
                leastFrequentWord = entry.getKey();
            }
        }
        return leastFrequentWord;
    }

    // Metoda do obliczania częstości wystąpień terminów (term frequency)
    public static Map<String, Integer> calculateTermFrequency(List<List<String>> cleanedSentences) {
        Map<String, Integer> termFrequency = new HashMap<>();
        for (List<String> tokens : cleanedSentences) {
            for (String token : tokens) {
                termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
            }
        }
        return termFrequency;
    }

    // Metoda do obliczania odwrotności częstości dokumentów (inverse document frequency)
    public static Map<String, Double> calculateInverseDocumentFrequency(List<List<String>> cleanedSentences) {
        Map<String, Integer> documentFrequency = new HashMap<>();
        int totalDocuments = cleanedSentences.size();

        for (List<String> tokens : cleanedSentences) {
            Set<String> uniqueTokens = new HashSet<>(tokens);
            for (String token : uniqueTokens) {
                documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1);
            }
        }
        // Dodaje 1 do licznika i mianownika, aby uniknąć dzielenia przez 0
        Map<String, Double> inverseDocumentFrequency = new HashMap<>();
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            String term = entry.getKey();
            int documentCount = entry.getValue();
            double idf = Math.log((double) totalDocuments / (documentCount + 1));
            inverseDocumentFrequency.put(term, idf);
        }

        return inverseDocumentFrequency;
    }

    // Metoda do obliczania wartości TF-IDF (term frequency-inverse document frequency)
    public static Map<String, Double> calculateTFIDF(Map<String, Integer> termFrequency, Map<String, Double> inverseDocumentFrequency) {
        Map<String, Double> tfidf = new HashMap<>();
        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            double idf = inverseDocumentFrequency.getOrDefault(term, 0.0);
            double tfidfValue = tf * idf;
            tfidf.put(term, tfidfValue);
        }
        return tfidf;
    }

    // Metoda do wyświetlania wyników TF-IDF w porządku malejącym
    public static void printTFIDFDescending(Map<String, Double> tfidf, int limit) {
        List<Map.Entry<String, Double>> entries = new ArrayList<>(tfidf.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int count = 0;
        for (Map.Entry<String, Double> entry : entries) {
            if (count >= limit && limit > 0) {
                break;
            }
            System.out.printf("%-15s %.2f\n", entry.getKey(), entry.getValue());
            count++;
        }
    }

    // Metoda do wyświetlania wyników TF-IDF w porządku rosnącym
    public static void printTFIDFAscending(Map<String, Double> tfidf, int limit) {
        List<Map.Entry<String, Double>> entries = new ArrayList<>(tfidf.entrySet());
        entries.sort(Map.Entry.comparingByValue());
        int count = 0;
        for (Map.Entry<String, Double> entry : entries) {
            if (count >= limit && limit > 0) {
                break;
            }
            System.out.printf("%-15s %.2f\n", entry.getKey(), entry.getValue());
            count++;
        }
    }
}

// Interfejs do walidacji danych wejściowych od użytkownika
interface InputValidator {
    boolean validate(String input);
}
