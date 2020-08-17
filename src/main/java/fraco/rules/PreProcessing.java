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
package fraco.rules;

import fraco.utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessing {

	private final String _regex = "(?<=[a-z])((?=[A-Z])|(?=[0-9])|(?=[/_]))|(?<=[A-Z])((?=[A-Z][a-z])|(?=[/_])|(?=[0-9]))|(?<=[/_])((?=[a-z])|(?=[0-9])|(?=[A-Z]))|(?<=[0-9])((?=[a-z])|(?=[/_])|(?=[A-Z]))|([,])";

	public synchronized List<String> tokenizeComments(String line) {

		List<String> tokens = new ArrayList<String>();
		// tokens=Lemmatizer.getInstance().tokenize(line);
		line = line.replaceAll("(?<= )([(])|(?<= )([)])", " ");
		line = line.replaceAll("\\(", "~(~");
		line = line.replaceAll("\\)", "~)~");
		String[] splitTokens = line.split("[^a-zA-Z_0-9()]+");
		/*
		 * StringTokenizer multiTokenizer = new StringTokenizer(line,
		 * ",://.-()#[]{}'?!\" ");
		 */
		for (String splittoken : splitTokens) {
			if (splittoken.matches("[a-zA-Z_0-9()]+"))
				tokens.add(splittoken);
		}

		return tokens;

	}

	public synchronized LinkedHashSet<String> tokenizeIdentifier(String identifier) {
		LinkedHashSet<String> iTokens = new LinkedHashSet<String>();
		if (identifier != null) {
			for (String token : (identifier).split(_regex)) {
				// if(!iTokens.contains(token)&&
				// token.matches("([A-Z]+)|([a-z]+)|([A-Z]+[a-z]+)"))
				if (!iTokens.contains(token) && token.matches("[a-zA-Z_0-9]+"))
					iTokens.add(token);

			}
		}
		return iTokens;
	}

	public synchronized HashSet<String> processOneWordIdentifier(Identifier identifier) {
		if (identifier != null && (identifier.get_type() == Type.METHOD || identifier.get_type() == Type.FIELD)) {
			return tokenizeIdentifier(identifier.get_className());

		}
		return null;

	}

	public synchronized Matcher splitCommentIntoLines(Comments comment) {
		Pattern pattern = null;
		if (comment.get_type() == CommentType.BLOCK)
			pattern = Pattern.compile("(.*)", Pattern.MULTILINE | Pattern.COMMENTS);
		else
			pattern = Pattern.compile("(.*)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher regMatcher = pattern.matcher(comment.get_comment());
		return regMatcher;
	}

	public synchronized Matcher splitCommentTextUsingDelimiters(String text) {
		Pattern pattern = null;
		pattern = Pattern.compile("[^.!?;\\s][^.!?;]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
				Pattern.MULTILINE | Pattern.COMMENTS);

		Matcher regMatcher = pattern.matcher(text);
		return regMatcher;
	}

	public synchronized void addMatchInstance(List<Match> matches, String matchInstance, int lineNumberMatch) {
		Match match = new Match();
		match.set_matchId(matches.size() + 1);
		match.set_matchString(matchInstance + " #" + match.get_matchId());
		match.set_matchPosition(lineNumberMatch);
		matches.add(match);
	}

	public synchronized void addMatchInstance(List<Match> matches, Identifier identifier, int lineNumberMatch) {
		Match match = new Match();
		match.set_matchId(matches.size() + 1);
		match.set_matchString(identifier.get_name() + " #" + match.get_matchId());
		match.set_matchPosition(lineNumberMatch);
		matches.add(match);
	}

	public synchronized String getMatchString(int size, LinkedHashSet<String> matchedTerms) {
		String matched = "";
		int i = 0;
		for (String match : matchedTerms) {
			if (i < size) {
				matched += match + " ";
			}
			i++;
		}
		return matched;
	}

	public synchronized String[] checkForTagLineBreak(String line) {
		String[] lines = null;
		int numberOfTags = 0;
		String[] tokens = line.split(" ");
		for (String token : tokens) {
			if (token.contains("@"))
				numberOfTags++;
		}
		if (numberOfTags > 1) {
			lines = line.split("@");
		} else {
			lines = new String[1];
			lines[0] = line;
		}
		return lines;
	}

}
