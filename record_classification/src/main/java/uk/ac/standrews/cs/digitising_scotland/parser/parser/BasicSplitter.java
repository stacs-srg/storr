package uk.ac.standrews.cs.digitising_scotland.parser.parser;

public class BasicSplitter {

    public String[] splitString(final String line) {

        String[] splitStrings;
        String newLine = removeQuotes(line);
        newLine = newLine.replaceAll("both\\s*", "");
        newLine = newLine.replaceAll("all", "");

        splitStrings = newLine.split("[;,&12/]");
        splitStrings = trimAndLowercase(splitStrings);

        for (int i = 0; i < splitStrings.length; i++) {
            System.out.println(splitStrings[i]);
        }

        splitStrings = moveAllStringsToStartOfArray(splitStrings);

        return splitStrings;
    }

    private String[] moveAllStringsToStartOfArray(final String[] splitStrings) {

        String[] shuntedArray = new String[splitStrings.length];
        int realSizeCounter = 0;
        for (int i = 0; i < splitStrings.length; i++) {
            if (!splitStrings[i].equals("")) {
                shuntedArray[realSizeCounter] = splitStrings[i];
                realSizeCounter++;
            }
        }

        return shuntedArray;
    }

    private String[] trimAndLowercase(final String[] splitStrings) {

        for (int i = 0; i < splitStrings.length; i++) {
            splitStrings[i] = splitStrings[i].trim().toLowerCase();
        }
        return splitStrings;
    }

    private String removeQuotes(final String line) {

        return line.replaceAll("\"", "");
    }
}
