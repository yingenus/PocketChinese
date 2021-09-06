package com.yingenus.pocketchinese.model.dictionary.search.chn;

import org.apache.commons.text.similarity.LevenshteinDetailedDistance;
import org.apache.commons.text.similarity.LevenshteinResults;

import java.util.Arrays;

public class ChnLevenshteinDetailedDistance  extends LevenshteinDetailedDistance {


    private final int substituteWeight;
    private final int deleteWeight;
    private final int insertWeight;

    ChnLevenshteinDetailedDistance(Integer threshold, int substituteWeight, int deleteWeight, int insertWeight){
        super(threshold);
        this.substituteWeight = substituteWeight;
        this.deleteWeight = deleteWeight;
        this.insertWeight = insertWeight;
    }
    ChnLevenshteinDetailedDistance(int substituteWeight, int deleteWeight, int insertWeight){
        this((Integer)null, substituteWeight, deleteWeight, insertWeight);
    }

    ChnLevenshteinDetailedDistance(Integer threshold){
        this(threshold, 1, 1, 1);
    }

    ChnLevenshteinDetailedDistance(){
        this(1, 1, 1);
    }

    public int getDeleteWeight() {
        return deleteWeight;
    }

    public int getInsertWeight() {
        return insertWeight;
    }

    public int getSubstituteWeight() {
        return substituteWeight;
    }


    private LevenshteinResults limitedCompare(CharSequence left, CharSequence right, final int threshold) { //NOPMD
        if (left == null || right == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }

        /*
         * This implementation only computes the distance if it's less than or
         * equal to the threshold value, returning -1 if it's greater. The
         * advantage is performance: unbounded distance is O(nm), but a bound of
         * k allows us to reduce it to O(km) time by only computing a diagonal
         * stripe of width 2k + 1 of the cost table. It is also possible to use
         * this to compute the unbounded Levenshtein distance by starting the
         * threshold at 1 and doubling each time until the distance is found;
         * this is O(dm), where d is the distance.
         *
         * One subtlety comes from needing to ignore entries on the border of
         * our stripe eg. p[] = |#|#|#|* d[] = *|#|#|#| We must ignore the entry
         * to the left of the leftmost member We must ignore the entry above the
         * rightmost member
         *
         * Another subtlety comes from our stripe running off the matrix if the
         * strings aren't of the same size. Since string s is always swapped to
         * be the shorter of the two, the stripe will always run off to the
         * upper right instead of the lower left of the matrix.
         *
         * As a concrete example, suppose s is of length 5, t is of length 7,
         * and our threshold is 1. In this case we're going to walk a stripe of
         * length 3. The matrix would look like so:
         *
         * <pre>
         *    1 2 3 4 5
         * 1 |#|#| | | |
         * 2 |#|#|#| | |
         * 3 | |#|#|#| |
         * 4 | | |#|#|#|
         * 5 | | | |#|#|
         * 6 | | | | |#|
         * 7 | | | | | |
         * </pre>
         *
         * Note how the stripe leads off the table as there is no possible way
         * to turn a string of length 5 into one of length 7 in edit distance of
         * 1.
         *
         * Additionally, this implementation decreases memory usage by using two
         * single-dimensional arrays and swapping them back and forth instead of
         * allocating an entire n by m matrix. This requires a few minor
         * changes, such as immediately returning when it's detected that the
         * stripe has run off the matrix and initially filling the arrays with
         * large values so that entries we don't compute are ignored.
         *
         * See Algorithms on Strings, Trees and Sequences by Dan Gusfield for
         * some discussion.
         */

        int n = left.length(); // length of left
        int m = right.length(); // length of right

        // if one string is empty, the edit distance is necessarily the length of the other
        if (n == 0) {
            return m * insertWeight <= threshold ? new LevenshteinResults(m, m, 0, 0) : new LevenshteinResults(-1, 0, 0, 0);
        } else if (m == 0) {
            return n * insertWeight <= threshold ? new LevenshteinResults(n, 0, n, 0) : new LevenshteinResults(-1, 0, 0, 0);
        }

        boolean swapped = false;
        if (n > m) {
            // swap the two strings to consume less memory
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
            swapped = true;
        }

        int[] p = new int[n + 1]; // 'previous' cost array, horizontally
        int[] d = new int[n + 1]; // cost array, horizontally
        int[] tempD; // placeholder to assist in swapping p and d
        final int[][] matrix = new int[m + 1][n + 1];

        //filling the first row and first column values in the matrix
        for (int index = 0; index <= n; index++) {
            matrix[0][index] = index;
        }
        for (int index = 0; index <= m; index++) {
            matrix[index][0] = index;
        }

        // fill in starting table values
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }
        // these fills ensure that the value above the rightmost entry of our
        // stripe will be ignored in following loop iterations
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        // iterates through t
        for (int j = 1; j <= m; j++) {
            final char rightJ = right.charAt(j - 1); // jth character of right
            d[0] = j;

            // compute stripe indices, constrain to array size
            final int min = Math.max(1, j - threshold);
            final int max = j > Integer.MAX_VALUE - threshold ? n : Math.min(
                    n, j + threshold);

            // the stripe may lead off of the table if s and t are of different sizes
            if (min > max) {
                return new LevenshteinResults(-1, 0, 0, 0);
            }

            // ignore entry left of leftmost
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            // iterates through [min, max] in s
            for (int i = min; i <= max; i++) {
                if (left.charAt(i - 1) == rightJ) {
                    // diagonally left and up
                    d[i] = p[i - 1];
                } else {
                    // 1 + minimum of cell to the left, to the top, diagonally left and up
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                }
                matrix[j][i] = d[i];
            }

            // copy current distance counts to 'previous row' distance counts
            tempD = p;
            p = d;
            d = tempD;
        }

        // if p[n] is greater than the threshold, there's no guarantee on it being the correct distance
        if (p[n] <= threshold) {
            return findDetailedResults(left, right, matrix, swapped);
        }
        return new LevenshteinResults(-1, 0, 0, 0);
    }

    private  LevenshteinResults findDetailedResults(final CharSequence left, final CharSequence right, final int[][] matrix, final boolean swapped) {

        int delCount = 0;
        int addCount = 0;
        int subCount = 0;

        int rowIndex = right.length();
        int columnIndex = left.length();

        int dataAtLeft = 0;
        int dataAtTop = 0;
        int dataAtDiagonal = 0;
        int data = 0;
        boolean deleted = false;
        boolean added = false;

        while (rowIndex >= 0 && columnIndex >= 0) {

            if (columnIndex == 0) {
                dataAtLeft = -1;
            } else {
                dataAtLeft = matrix[rowIndex][columnIndex - 1];
            }
            if (rowIndex == 0) {
                dataAtTop = -1;
            } else {
                dataAtTop = matrix[rowIndex - 1][columnIndex];
            }
            if (rowIndex > 0 && columnIndex > 0) {
                dataAtDiagonal = matrix[rowIndex - 1][columnIndex - 1];
            } else {
                dataAtDiagonal = -1;
            }
            if (dataAtLeft == -1 && dataAtTop == -1 && dataAtDiagonal == -1) {
                break;
            }
            data = matrix[rowIndex][columnIndex];

            // case in which the character at left and right are the same,
            // in this case none of the counters will be incremented.
            if (columnIndex > 0 && rowIndex > 0 && left.charAt(columnIndex - 1) == right.charAt(rowIndex - 1)) {
                columnIndex--;
                rowIndex--;
                continue;
            }

            // handling insert and delete cases.
            deleted = false;
            added = false;
            if (data - 1 == dataAtLeft && (data <= dataAtDiagonal && data <= dataAtTop)
                    || (dataAtDiagonal == -1 && dataAtTop == -1)) { // NOPMD
                columnIndex--;
                if (swapped) {
                    addCount++;
                    added = true;
                } else {
                    delCount++;
                    deleted = true;
                }
            } else if (data - 1 == dataAtTop && (data <= dataAtDiagonal && data <= dataAtLeft)
                    || (dataAtDiagonal == -1 && dataAtLeft == -1)) { // NOPMD
                rowIndex--;
                if (swapped) {
                    delCount++;
                    deleted = true;
                } else {
                    addCount++;
                    added = true;
                }
            }

            // substituted case
            if (!added && !deleted) {
                subCount++;
                columnIndex--;
                rowIndex--;
            }
        }
        return new LevenshteinResults(addCount*insertWeight + delCount*deleteWeight + subCount*substituteWeight,
                addCount, delCount, subCount);
    }



}
