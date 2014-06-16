package uk.ac.standrews.cs.usp.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Adds a spelling mistake to a string by swapping the two 'middle' characters
 * in a randomly string.
 * 
 * @author jkc25
 */
public class SpellingMistakeFactory {

	/** The Constant ASCIIOFFSET. */
	static final int ASCIIOFFSET = 97;

	/**
	 * Add spelling mistake to input string.
	 * 
	 * @param input
	 *            input string to add mistake to.
	 * @return input string with random spelling mistake introduced.
	 */
	public String addMistake(final String input) {

		int a, b = 0;
		String[] inputSplit = input.split("\\s");
		String output = "";
		int noOfWords = inputSplit.length;
		int word = randomInt(0, noOfWords - 1);
		String wordToChange = inputSplit[word];

		char[] c = wordToChange.toCharArray();

		if (c.length > 2) {
			// Replace with a "swap" function, if desired:
			a = c.length / 2;
			b = a + 1;
		} else if (c.length == 1) {
			a = 0;
			b = 0;
		} else {
			a = 0;
			b = 1;
		}
		char temp = c[a];
		c[a] = c[b];
		c[b] = temp;

		String swappedString = new String(c);

		inputSplit[word] = swappedString;
		for (int i = 0; i < inputSplit.length; i++) {
			output = output.concat(inputSplit[i] + " ");
		}
		System.out.println(output.trim());
		return output.trim();
	}

	/**
	 * Add spelling mistake to input string based on likely typos.
	 * 
	 * @param input
	 *            input string to add mistake to.
	 * @return input string with random spelling mistake introduced.
	 */
	public String addMistakeTypo(final String input) {

		char[][] charMap = { { 's', 'w' }, // a
				{ 'v', 'g' }, // b
				{ 'x', 'd' }, // c
				{ 's', 'e' }, // d
				{ 'w', 'r' }, // e
				{ 'd', 'g' }, // f
				{ 'f', 't' }, // g
				{ 'g', 'j' }, // h
				{ 'u', 'j' }, // i
				{ 'k', 'i' }, // j
				{ 'l', 'o' }, // k
				{ 'k', 'o' }, // l
				{ 'n', 'k' }, // m
				{ 'm', 'j' }, // n
				{ 'l', 'p' }, // o
				{ 'l', 'o' }, // p
				{ 'w', 'a' }, // q
				{ 'e', 'd' }, // r
				{ 'a', 'e' }, // s
				{ 'r', 'f' }, // t
				{ 'i', 'j' }, // u
				{ 'c', 'f' }, // v
				{ 'q', 'e' }, // w
				{ 'z', 's' }, // x
				{ 't', 'u' }, // y
				{ 'x', 'a' }, // z
		};

		String[] inputSplit = input.split("\\s");
		String output = "";
		int noOfWords = inputSplit.length;
		int word = randomInt(0, noOfWords - 1);
		String wordToChange = inputSplit[word];

		char[] c = wordToChange.toCharArray();

		int letterToChange = randomInt(0, c.length - 1);
		int indexInCharMap = (int) c[letterToChange] - ASCIIOFFSET;
		c[letterToChange] = charMap[indexInCharMap][randomInt(0, 1)];

		String swappedString = new String(c);

		inputSplit[word] = swappedString;
		for (int i = 0; i < inputSplit.length; i++) {
			output = output.concat(inputSplit[i] + " ");
		}
		System.out.println(output.trim());
		return output.trim();
	}

	/**
	 * Returns a random int between min and max.
	 * 
	 * @param min
	 *            min value of random value
	 * @param max
	 *            max value of random int
	 * @return random int between min and max
	 */
	private int randomInt(final int min, final int max) {

		return min + (int) (Math.random() * (max - min + 1));
	}

	/**
	 * Adds random spelling mistakes to a csv file.
	 * 
	 * @param fileIn
	 *            CSV file to add mistakes to.
	 * @param spellingMistakePct
	 *            Percetnage of mistakes to introduce in terms of lines (ie 10%
	 *            of lines will have a mistakes)
	 * @param fileOut
	 *            where to write new file to.
	 * @return success.
	 */
	public boolean addMistakesToFile(final File fileIn,
			final int spellingMistakePct, final String fileOut) {

		boolean complete = true;
		int docID = 100000;
		int i = 0;
		int changed = 0;
		SpellingMistakeFactory spf = new SpellingMistakeFactory();
		String output = "";
		StringBuilder stringBuilder = new StringBuilder();

		if (fileIn.isFile()) {
			try {
				Scanner s = new Scanner(fileIn, "UTF-8");
				while (s.hasNextLine()) {
					i++;
					String line = s.nextLine();
					String feature = line.split("\t")[0];
					String content = line.split("\t")[1];
					int decisionInt = (int) Math.rint(Math.random() * 100);
					if (decisionInt < spellingMistakePct) {
						String newContent = spf.addMistake(content);
						output += feature + "\t" + newContent + "\t"
								+ (docID + i) + "\n";
						System.out.println(changed++);
					} else {
						stringBuilder.append(feature + "\t" + content + "\t"
								+ (docID + i) + "\n");
					}
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			output = stringBuilder.toString();
			Utils.writeToFile(output, fileOut);

		}

		return complete;

	}
}
