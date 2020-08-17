/*******************************************************************************
 * Fraco - Eclipse plug-in to detect fragile comments
 *
 * Copyright (C) 2019 McGill University
 *     
 * Eclipse Public License - v 2.0
 *
 *  THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE
 *  PUBLIC LICENSE v2.0. ANY USE, REPRODUCTION OR DISTRIBUTION
 *  OF THE PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 *******************************************************************************/
package fraco.utils;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// This class implements string comparison algorithm
/// based on character pair similarity
/// Source: http://www.catalysoft.com/articles/StrikeAMatch.html
/// </summary>

public class SimilarityTool {

	private final static String _regex = "(?<=[a-z])((?=[A-Z])|(?=[0-9])|(?=[/_]))|(?<=[A-Z])((?=[A-Z][a-z])|(?=[/_])|(?=[0-9]))|(?<=[/_])((?=[a-z])|(?=[0-9])|(?=[A-Z]))|(?<=[0-9])((?=[a-z])|(?=[/_])|(?=[A-Z]))|([,])";

	private SimilarityTool() {
	}

	/// <summary>
	/// Compares the two strings based on letter pair matches
	/// </summary>
	/// <param name="str1"></param>
	/// <param name="str2"></param>
	/// <returns>The percentage match from 0.0 to 1.0 where 1.0 is
	/// 100%</returns>
	public static double CompareStrings(String str1, String str2) {
		if (str1.length() != str2.length())
			return 0;
		List<String> pairs1 = WordLetterPairs(str1);
		List<String> pairs2 = WordLetterPairs(str2);
		if (pairs1.size() != pairs2.size())
			return 0;
		int intersection = 0;
		int union = pairs1.size() + pairs2.size();
		for (int i = 0; i < pairs1.size(); i++) {
			int threePostions = i;
			for (int j = threePostions; j <= threePostions + 2; j++) {
				if (j < pairs2.size() && pairs1.get(i).equalsIgnoreCase(pairs2.get(j))) {
					intersection++;
					pairs2.remove(j);// Must remove the match to prevent "GGGG"
										// from appearing to match "GG" with
										// 100% success
					break;
				}
			}
		}
		return (2.0 * intersection) / union;
	}

	/// <summary>
	/// Gets all letter pairs for each
	/// individual word in the string
	/// </summary>
	/// <param name="str"></param>
	/// <returns></returns>
	private static List<String> WordLetterPairs(String word) {
		List<String> AllPairs = new ArrayList<String>();
		// Tokenize the string and put the tokens/words into an array
		// String[] words = str.split(_regex);

		// For each word
		// for (int w = 0; w < words.length; w++)
		{
			if (word != null || word != "") {
				// Find the pairs of characters
				AllPairs = LetterPairs(word);
				/*
				 * for (int p = 0; p < PairsInWord.length; p++) {
				 * AllPairs.add(PairsInWord[p].toLowerCase()); }
				 */
			}
		}
		return AllPairs;
	}

	/// <summary>
	/// Generates an array containing every
	/// two consecutive letters in the input string
	/// </summary>
	/// <param name="str"></param>
	/// <returns></returns>
	private static List<String> LetterPairs(String str) {
		int numPairs = str.length() - 1;
		List<String> pairs = new ArrayList<String>();
		for (int i = 0; i < numPairs; i++) {
			pairs.add(str.substring(i, i + 2));
		}
		return pairs;
	}

}
