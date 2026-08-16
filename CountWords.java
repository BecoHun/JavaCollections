import java.util.*;

public class CountWords {
	public static void main(String[] args) {
		System.out.println("=== START THE COUNTER ===\n");

		runEnglishTextTest();

		System.out.println("\n=== TEST COMPLETED ===");
	}
	private static void runEnglishTextTest() {
		System.out.println("Preparing sample text...");

		List<String> inputLines = Arrays.asList(
		    "Java programming language is powerful and Java is fast.",
		    "Developers love Java because Java provides great tools.",
		    "Code quality matters in Java software development.",
		    "Java is everywhere: backend Java, mobile Java, and cloud Java.",
		    "Writing clean code in Java is essential for every developer.",
		    "Java developers write high quality code every day.",
		    "Testing code ensures that Java applications run smoothly.",
		    "Java is reliable and Java remains top choice for enterprizes."
		);

		System.out.println(" -> Number of input lines: " + inputLines.size());
		System.out.println(" -> We count the words of a text with the Words().countWords() method...");

		Words words = new Words();
		String actualResult = words.countWords(inputLines);

		String expectedResult = "java - 14";

		System.out.println("\n--- EXPECTED RESULT ---");
		System.out.println(expectedResult);

		System.out.println("\n--- RESULT OBTAINED ---");
		System.out.println(actualResult);

		System.out.println("\n-----------------------------------");
		if (expectedResult.equals(actualResult)) {
		    System.out.println("--- SUCCESSFUL COMPLIANCE ---");
		} else {
		    System.err.println(" [ERROR] The result obtained is different from what was expected!");
		}
    }
    
    static class Words {
	    public String countWords(List<String> lines) {
		Map<String, Integer> wordCounts = new HashMap<>();

		for (String line : lines) {
		    String[] words = line.toLowerCase().split("[^\\p{L}\\p{N}]+");

		    for (String word : words) {
		        if (word.length() >= 4) {
		            Integer count = wordCounts.get(word);
		            if (count == null) {
		                wordCounts.put(word, 1);
		            } else {
		                wordCounts.put(word, count + 1);
		            }
		        }
		    }
		}

		List<Map.Entry<String, Integer>> filteredList = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
		    if (entry.getValue() >= 10) {
		        filteredList.add(entry);
		    }
		}

		Collections.sort(filteredList, new Comparator<Map.Entry<String, Integer>>() {
		    @Override
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		        int freqCompare = o2.getValue().compareTo(o1.getValue());
		        if (freqCompare != 0) {
		            return freqCompare;
		        }
		        return o1.getKey().compareTo(o2.getKey());
		    }
		});

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < filteredList.size(); i++) {
		    Map.Entry<String, Integer> entry = filteredList.get(i);
		    sb.append(entry.getKey()).append(" - ").append(entry.getValue());
		    if (i < filteredList.size() - 1) {
		        sb.append("\n");
		    }
		}

		return sb.toString();
	    }
	}	
}
/*
Program Overview
This Java module processes text to generate sorted word-frequency statistics based on specific filtering rules.
Key Logic & Rules
Text Normalization:
Converts text to lowercase and splits lines using Unicode-aware regular expressions (\p{L}), ensuring full support for English,
Cyrillic, and other alphabets while stripping punctuation.
Length Filter: Keeps only words with 4 or more characters (filtering out short words like "and", "the", "is").
Frequency Filter: Retains only words that appear at least 10 times across the input text.
Sorting Order: Sorts the resulting words primarily by descending frequency (most frequent first), and secondarily in alphabetical order for ties.
*/

