package com.yfzm.security.wordladder.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.*;

public class WordLadder {

    private Set<String> dictionary;
    private Set<String> neighbourWords;
    private Set<String> reachedWords;
    private Stack<String> ladders;
    private Set<String> endWords;
    private Deque<Stack<String>> words_queue;

    private String first;
    private String last;
    private Boolean same_len;
    private Boolean is_reverse;

    public static void main(String[] args) {
        WordLadder wl = new WordLadder();
        if (wl.createLadder("kwyjibo", "fluxbar")) {
            System.out.println("OK");
        }
        System.out.println(wl.getLadderStep());

        wl.run();
    }

    public WordLadder() {
        dictionary = new HashSet<>();
        neighbourWords = new HashSet<>();
        reachedWords = new HashSet<>();

        ladders = new Stack<>();
        endWords = new HashSet<>();
        words_queue = new ArrayDeque<>();

        setDictionary();
    }

    public boolean createLadder(String firstString, String lastString) {
        clear();

        firstString = firstString.toLowerCase();
        lastString = lastString.toLowerCase();

        if (firstString.equals("") || lastString.equals("")) {
            System.out.println("The word can't be void.");
            return false;
        }

        if (firstString.equals(lastString)) {
            System.out.println("The two words must be different.");
            return false;
        }

        first = firstString;
        last = lastString;

        initWords();
        setEndWords();

        return findLadder();
    }

    public int getLadderStep() {
        if (ladders == null) {
            return 0;
        } else {
            return ladders.size();
        }
    }

    public Stack<String> getLadderStack() {
        if (ladders == null) {
            return null;
        }

        Stack<String> ret_stack = new Stack<>();

        if (!is_reverse) {
            ret_stack.addAll(ladders);
        }
        else {
            Stack<String> result = new Stack<>();
            while (!ladders.empty()) {
                result.push(ladders.pop());
            }
            ret_stack.addAll(result);
        }

        return ret_stack;
    }

    private void initWords() {
        same_len = first.length() == last.length();

        is_reverse = false;
        getNeighbourWords(first);
        int nFirst = neighbourWords.size();
        getNeighbourWords(last);
        int nLast = neighbourWords.size();
        if (nFirst > nLast) {
            String temp = first;
            first = last;
            last = temp;
            is_reverse = true;
        }

        // put the first string into dictionary. It works when the first and the last words are both out of dictionary.
        dictionary.add(first);
    }

    public void run() {
        setDictionary();
        while (true) {
            clear();
            // only when the input is '\n', function setWords will return false
            if (setWords()) {
                setEndWords();  // added.
                // the function "findLadder" will search for the ladder, then the function "outputLadder" print the result
                outputLadder(findLadder());
            }
            else
                break;
        }
    }

    private void setDictionary() {
//        InputStream in = WordLadder.class.getClassLoader().getResourceAsStream("dictionary.txt");
//        InputStream in = WordLadder.class.getClassLoader().getResourceAsStream("smalldict1.txt");
        FileInputStream in = null;
        try {
            Resource resource = new ClassPathResource("dictionary.txt");
            File file = resource.getFile();
            in = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                dictionary.add(line.trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//        InputStream in = WordLadder.class.getResourceAsStream("dictionary.txt");

    }

    private void clear() {
        reachedWords.clear();
        ladders = new Stack<>();
        words_queue.clear();
    }

    private Boolean setWords() {
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            System.out.println();
            System.out.print("Word #1 (or Enter to quit): ");
            first = scanner.nextLine();
            if (first.equals("")) {
                System.out.println("Have a nice day.");
                return false;
            }
            first = first.toLowerCase();
            System.out.print("Word #2 (or Enter to quit): ");
            last = scanner.nextLine();
            if (last.equals("")) {
                System.out.println("Have a nice day.\n");
                return false;
            }
            last = last.toLowerCase();

            same_len = first.length() == last.length();

            is_reverse = false;
            getNeighbourWords(first);
            int nFirst = neighbourWords.size();
            getNeighbourWords(last);
            int nLast = neighbourWords.size();
            if (nFirst > nLast) {
                String temp = first;
                first = last;
                last = temp;
                is_reverse = true;
            }

            // put the first string into dictionary. It works when the first and the last words are both out of dictionary.
            dictionary.add(first);

		/*if (!isInDictionary(first) || !isInDictionary(last))
			cerr << "The two words must be found in the dictionary." << endl;
		else */if (first.equals(last))
            System.out.println("The two words must be different.");
        else
            break;
        }
        return true;
    }

    private void setEndWords() {
        getNeighbourWords(last);
//        endWords = neighbourWords;
        endWords.clear();
        endWords.addAll(neighbourWords);
    }

    private Boolean findLadder() {
        Stack<String> first_stack = new Stack<>();
        first_stack.push(first);
        // handle an extreme case: only in ONE step can the first change to the last
        for (String word : endWords) {
            if (word.equals(first)) {
                first_stack.push(last);
                ladders = new Stack<>();
                ladders.addAll(first_stack);
                return true;
            }
        }

        words_queue.push(first_stack);
        reachedWords.add(first);  // mark the first word to avoid being used later

        while (!words_queue.isEmpty()) {
            Stack<String> head_stack = words_queue.getLast();  // use reference to improve performance
            Stack<String> new_tail_stack;  // a new stack that will append to the end of the queue

            getNeighbourWords(head_stack.peek());
            for (String s : neighbourWords) {
                new_tail_stack = new Stack<>();
                new_tail_stack.addAll(head_stack);

                if (!isReached(s)) {
                    new_tail_stack.push(s);
                    if (endWords.contains(s)) {  // if matched successfully // lllllllllllllllllllllllllllllllll
                        new_tail_stack.push(last);
                        ladders.clear();
                        ladders.addAll(new_tail_stack);
                        return true;
                    }

                    words_queue.push(new_tail_stack);
                    reachedWords.add(s);
                }
            }
            words_queue.removeLast();
        }
        ladders = null;
        return false;

    }

    private Boolean isReached(String s) {
        return reachedWords.contains(s);
    }

    private void getNeighbourWords(String s) {
        neighbourWords.clear();
        StringBuilder changed_word;
        // the same length with a changed letter
        for (int i = 0; i < s.length(); ++i) {
            for (char c = 'a'; c <= 'z'; ++c) {
                if (c == s.charAt(i)) continue;
                changed_word = new StringBuilder(s);
                changed_word.setCharAt(i, c);
                if (isInDictionary(changed_word.toString()))
                    neighbourWords.add(changed_word.toString());
            }
        }
        // added. It works when the lengths of first and last words are not equal
        if (!same_len) {
            // add a letter
            for (int i = 0; i <= s.length(); ++i) {
                for (char c = 'a'; c <= 'z'; ++c) {
                    changed_word = new StringBuilder(s);
                    changed_word.insert(i, c);
                    if (isInDictionary(changed_word.toString()))
                        neighbourWords.add(changed_word.toString());
                }
            }
            // erase a letter
            for (int i = 0; i < s.length(); ++i) {
                changed_word = new StringBuilder(s);
                changed_word.deleteCharAt(i);
                if (isInDictionary(changed_word.toString()))
                    neighbourWords.add(changed_word.toString());
            }
        }
    }

    private Boolean isInDictionary(String s) {
        return dictionary.contains(s);
    }

    private void outputLadder(Boolean isFound) {
        // if the ladder is NOT found
        if (!isFound) {
            if(!is_reverse)
                System.out.println("No word ladder found from " + last + " back to " + first + ".");
            else
                System.out.println("No word ladder found from " + first + " back to " + last + ".");
            return;
        }

        // if the ladder has been found
        if (!is_reverse) {
            System.out.println("A ladder from " + last + " back to " + first + ":");
            while (!ladders.empty()) {
                System.out.print(ladders.pop() + " ");
            }
            System.out.println();
        }
        else {
            System.out.println("A ladder from " + first + " back to " + last + ":");
            Stack<String> result = new Stack<>();
            while (!ladders.empty()) {
                result.push(ladders.pop());
            }
            while (!result.empty()) {
                System.out.print(result.pop() + " ");
            }
            System.out.println();
        }
    }

}
