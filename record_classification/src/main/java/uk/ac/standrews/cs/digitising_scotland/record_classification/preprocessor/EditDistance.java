package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;
//package uk.ac.standrews.cs.digitising_scotland.parser.preprocessor;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
///**
// * From http://www.cs.dartmouth.edu/~cbk/classes/10/notes/26/EditDistance.java
// * 
// * Computes the edit distance between pairs of words.  Can be used for applications
// * like finding near-match names in Kevin Bacon or spelling correction.
// * 
// * There are two versions: a recursive version and a dynamic programming version
// * that memoizes the function by storing previously solved problems in a map.
// * 
// * @author Scot Drysdale
// */
//public class EditDistance {
//
//    private Map<StringPair, Integer> solvedProblems;
//
//    /**
//     * Computes the edit distance between two strings.  The valid operations are:
//     *   1) Insert a character
//     *   2) Delete a character
//     *   3) Replace a character
//     *   4) Twiddle (Swap two characters to match the output).
//     * @param s1 The source string
//     * @param s2 The destination string
//     * @return the number of edit operations to turn s1 into s2
//     */
//    public static int naiveEditDistance(final String s1, final String s2) {
//
//        int matchDist; // Edit distance if first char. match or do a replace
//        int insertDist; // Edit distance if insert first char of s1 in front of s2.
//        int deleteDist; // Edit distance if delete first char of s2.
//        int swapDist; // edit distance for twiddle (first 2 char. must swap).
//
//        if (s1.length() == 0) {
//            return s2.length(); // Insert the remainder of s2
//        }
//        else if (s2.length() == 0) {
//            return s1.length(); // Delete the remainer of s1
//        }
//        else {
//            matchDist = naiveEditDistance(s1.substring(1), s2.substring(1));
//            if (s1.charAt(0) != s2.charAt(0)) {
//                matchDist++; // If first 2 char. don't match must replace
//            }
//
//            insertDist = naiveEditDistance(s1.substring(1), s2) + 1;
//            deleteDist = naiveEditDistance(s1, s2.substring(1)) + 1;
//
//            if (s1.length() > 1 && s2.length() > 1 && s1.charAt(0) == s2.charAt(1) && s1.charAt(1) == s2.charAt(0)) {
//                swapDist = naiveEditDistance(s1.substring(2), s2.substring(2)) + 1;
//            }
//            else {
//                swapDist = Integer.MAX_VALUE; // Can't swap if first 2 char. don't match
//            }
//
//            return Math.min(matchDist, Math.min(insertDist, Math.min(deleteDist, swapDist)));
//        }
//    }
//
//    /**
//     * Computes the edit distance between two strings.  The valid operations are:
//     *   1) Insert a character
//     *   2) Delete a character
//     *   3) Replace a character
//     *   4) Twiddle (Swap two characters to match the output).
//     * 
//     * This version is memoized to avoid re-solving problems.
//     *
//     * @param s1 The source string
//     * @param s2 The destination string
//     * @return the number of edit operations to turn s1 into s2
//     */
//    public int memoizedEditDist(final String s1, final String s2) {
//
//        solvedProblems = new HashMap<StringPair, Integer>();
//
//        return editDist(s1, s2);
//    }
//
//    /**
//     * A helper function for memoizedEditDistance that uses a Map
//     * to keep track of problems that have already been solved.
//     * 
//     * @param s1 The source string
//     * @param s2 The destination string
//     * @return the number of edit operations to turn s1 into s2
//     */
//    private int editDist(final String s1, final String s2) {
//
//        int matchDist; // Edit distance if first char. match or do a replace
//        int insertDist; // Edit distance if insert first char of s1 in front of s2.
//        int deleteDist; // Edit distance if delete first char of s2.
//        int swapDist; // edit distance for twiddle (first 2 char. must swap).
//
//        if (s1.length() == 0) {
//            return s2.length(); // Insert the remainder of s2
//        }
//        else if (s2.length() == 0) {
//            return s1.length(); // Delete the remainder of s1
//        }
//        else {
//            StringPair pair = new StringPair(s1, s2);
//            Integer result = solvedProblems.get(pair);
//
//            if (result != null) {
//                return result; // If so, return the answer
//            }
//            else {
//                matchDist = editDist(s1.substring(1), s2.substring(1));
//                if (s1.charAt(0) != s2.charAt(0)) {
//                    matchDist++; // If first 2 char. don't match must replace
//                }
//
//                insertDist = editDist(s1.substring(1), s2) + 1;
//                deleteDist = editDist(s1, s2.substring(1)) + 1;
//
//                if (s1.length() > 1 && s2.length() > 1 && s1.charAt(0) == s2.charAt(1) && s1.charAt(1) == s2.charAt(0)) {
//                    swapDist = editDist(s1.substring(2), s2.substring(2)) + 1;
//                }
//                else {
//                    swapDist = Integer.MAX_VALUE; // Can't swap if first 2 char. don't match
//                }
//
//                int dist = Math.min(matchDist, Math.min(insertDist, Math.min(deleteDist, swapDist)));
//
//                solvedProblems.put(pair, dist); // Save the result for future
//
//                return dist;
//            }
//        }
//    }
//
//    /**
//     * Testing program.
//     */
//    public static void main(final String[] args) {
//
//        Scanner input = new Scanner(System.in);
//        String s1, s2; // The strings to find the edit distance between
//        EditDistance calc = new EditDistance();
//
//        System.out.println("Enter two strings, one per line");
//        s1 = input.nextLine();
//        s2 = input.nextLine();
//
//        while (s1.length() > 0) {
//            System.out.println("The edit distance between \"" + s1 + "\" and \"" + s2 + "\" is " + naiveEditDistance(s1, s2));
//            System.out.println();
//
//            System.out.println("The edit distance between \"" + s1 + "\" and \"" + s2 + "\" is " + calc.memoizedEditDist(s1, s2));
//            System.out.println();
//
//            System.out.println("Enter two strings, one per line");
//            s1 = input.nextLine();
//            s2 = input.nextLine();
//        }
//    }
//}
