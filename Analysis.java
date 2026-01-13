//Öykü Karaduman - 22050111033
//Ruslan Ibragimov - 21050141029

import java.util.ArrayList;
import java.util.List;

class Naive extends Solution {
    static {
        SUBCLASSES.add(Naive.class);
        System.out.println("Naive registered");
    }

    public Naive() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }
}

class KMP extends Solution {
    static {
        SUBCLASSES.add(KMP.class);
        System.out.println("KMP registered");
    }

    public KMP() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Compute LPS (Longest Proper Prefix which is also Suffix) array
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return indicesToString(indices);
    }

    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}

class RabinKarp extends Solution {
    static {
        SUBCLASSES.add(RabinKarp.class);
        System.out.println("RabinKarp registered.");
    }

    public RabinKarp() {
    }

    private static final int PRIME = 101; // A prime number for hashing

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        int d = 256; // Number of characters in the input alphabet
        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Calculate h = d^(m-1) % PRIME
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % PRIME;
        }

        // Calculate hash value for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (d * textHash + text.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (patternHash == textHash) {
                // Check characters one by one
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    indices.add(i);
                }
            }

            // Calculate hash value for next window
            if (i < n - m) {
                textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // Convert negative hash to positive
                if (textHash < 0) {
                    textHash = textHash + PRIME;
                }
            }
        }

        return indicesToString(indices);
    }
}

/**
 * TODO: Implement Boyer-Moore algorithm
 * This is a homework assignment for students
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    // Helper method to make the Bad Character Table
    private int[] makeBadCharTable(String pattern) {
        // I used 65536 instead of 256 to cover all Unicode characters
        // This fixes the error I got in the Unicode test case
        int[] table = new int[65536]; 
        int m = pattern.length();

        // Fill table with -1 first
        for (int i = 0; i < table.length; i++) {
            table[i] = -1;
        }

        // Save the last position of each character
        for (int i = 0; i < m; i++) {
            table[pattern.charAt(i)] = i;
        }
        
        return table;
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Check if pattern is empty
        if (m == 0) {
            for (int i = 0; i <= n; i++)
                indices.add(i);
            return indicesToString(indices);
        }

        // Prepare the bad character table
        int[] badCharTable = makeBadCharTable(pattern);

        int shift = 0; // how much we shift the pattern

        while (shift <= (n - m)) {
            int j = m - 1;

            // Compare pattern and text from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                // j < 0 means we found a full match
                indices.add(shift);

                // Move to the next position
                // If we are not at the end, shift based on the next character in text
                if (shift + m < n) {
                    shift += m - badCharTable[text.charAt(shift + m)];
                } else {
                    shift += 1;
                }
            } else {
                // Mismatch case:
                // Use the bad character rule to shift
                // I use Math.max to make sure we don't shift backwards
                shift += Math.max(1, j - badCharTable[text.charAt(shift + j)]);
            }
        }

        return indicesToString(indices);
    }
}

/**
 * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 * Be creative! Try to make it efficient for specific cases
 */
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // If pattern is empty, it matches everywhere
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        
        // If pattern is longer than text, it can't match
        if (m > n) return "";

        // Strategy: Save the first and last chars
        char start = pattern.charAt(0);
        char end = pattern.charAt(m - 1);

        // Loop through the text
        for (int i = 0; i <= n - m; i++) {
            
            // OPTIMIZATION: Check the LAST character first.
            // Most mismatches happen here, so we save time.
            if (text.charAt(i + m - 1) != end) {
                continue; // Last char didn't match, skip!
            }

            // Then check the FIRST character.
            if (text.charAt(i) != start) {
                continue; // First char didn't match, skip!
            }

            // If edges match, then check the middle part
            boolean fullMatch = true;
            for (int k = 1; k < m - 1; k++) {
                if (text.charAt(i + k) != pattern.charAt(k)) {
                    fullMatch = false;
                    break;
                }
            }

            if (fullMatch) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }
}
