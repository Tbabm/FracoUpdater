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

import fraco.utils.Comments;
import fraco.utils.Identifier;
import fraco.utils.Type;

import java.util.*;

public class CommonRules {

	@SuppressWarnings("serial")
	public static final List<String> _stopWords = Collections.unmodifiableList(new ArrayList<String>() {
		{
			add("is");
			add("a");
			add("this");
			add("that");
			add("on");
			add("in");
			add("the");
			add("of");
			add("are");
			add("and");
			add("or");
			add("get");
			add("return");
			add("@return");
			add("@returns");
			add("returns");
		}
	});

	@SuppressWarnings("serial")
	public static final List<String> _getterWords = Collections.unmodifiableList(new ArrayList<String>() {
		{
			add("get");
			add("gets");
			add("return");
			add("returns");
			add("@return");
			add("@returns");
		}
	});

	/*
	 * @SuppressWarnings("serial") private static final HashMap<Character,
	 * String> PLURAL_RULES_MAP=new HashMap<Character,String>(){{ put('y',
	 * "ies"); put('s', "es"); put('z',"es"); put('x',"es"); }};
	 */
	private boolean _isCopyrightBlock = false;

	@SuppressWarnings("serial")
	private static final LinkedHashMap<String, String> ABERRANT_RULES_MAP = new LinkedHashMap<String, String>() {
		{
			put("appendix", "appendices");
			put("barracks", "barracks");
			put("child", "children");
			put("criterion", "criteria");
			put("focus", "foci");
			put("index", "indices");
			put("leaf", "leaves");
			put("phenomenon", "phenomena");
			put("spectrum", "speactra");
		}
	};

	@SuppressWarnings("serial")
	private static final List<Character> VOWELS = new ArrayList<Character>() {
		{
			add('a');
			add('e');
			add('i');
			add('o');
			add('u');
		}
	};

	public synchronized boolean copyrightComment(Comments comment) {
		if (comment.get_comment().toLowerCase().contains("copyright")
				|| comment.get_comment().toLowerCase().contains("license"))
			return true;

		if ((comment.get_comment().contains("////")) && (comment.get_lineNumber() == 1 || _isCopyrightBlock)) {
			if (!_isCopyrightBlock)
				_isCopyrightBlock = true;
			else if (_isCopyrightBlock)
				_isCopyrightBlock = false;
			return true;
		} else if (_isCopyrightBlock)
			return true;

		return false;
	}

	public synchronized boolean checkProximity(Comments comment) {
		if (DetectFragileComments._filterProximity && comment.is_isProximityComment())
			return true;
		else if (!DetectFragileComments._filterProximity)
			return true;
		return false;
	}

	public synchronized boolean containsReturnTag(String token, Collection<String> terms,
			LinkedHashSet<String> matchedTerms) {
		if (Collections.disjoint(matchedTerms, _getterWords) && terms.contains("get")
				&& (token.equalsIgnoreCase("@return") || token.equalsIgnoreCase("returns")
						|| token.equalsIgnoreCase("return"))) {
			matchedTerms.add(token);
			return true;

		}
		return false;
	}

	public synchronized boolean matchLocalInJavaDoc(List<String> tokens, Identifier identifier) {
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).equalsIgnoreCase("@param") && i + 1 >= tokens.size()
					&& tokens.get(i + 1).equalsIgnoreCase(identifier.get_name())) {
				return true;
			}
		}

		return false;
	}

	public synchronized boolean processMethodJavadoc(String comment, Identifier id) {
		if (id.get_name().contains("get") && comment.contains("@return"))
			return true;
		else if (id.get_name().contains("is") && comment.contains("@return")) {
			return true;
		}
		return false;
	}

	public synchronized boolean containsStopWords(LinkedHashSet<String> matchedTerms) {
		if (!matchedTerms.isEmpty()) {
			/*
			 * if(matchedTerms.size()==1) { if(matchedTerms.contains("is")||
			 * matchedTerms.contains("get")|| matchedTerms.contains("@return")||
			 * matchedTerms.contains("returns")||
			 * matchedTerms.contains("return")) return true; }
			 */
			// else if(matchedTerms.size()==2)
			// {
			for (String word : _stopWords) {
				if (matchedTerms.contains(word))
					return true;
			}
			// }
		}
		return false;

	}

	public synchronized boolean filterStopWords(LinkedHashSet<String> matchTerms) {
		if (matchTerms.size() == 1) {
			for (String term : matchTerms) {
				if (_stopWords.contains(term.toLowerCase()))
					return false;
			}
		}
		return true;
	}

	protected synchronized String getPluralIdenticalMatch(Identifier identifier) {
		if (identifier.get_type() == Type.CLASS) {
			return convertSingularToPlural(identifier.get_name());
		}

		return null;
	}

	public synchronized String convertSingularToPlural(String word) {
		String singular = word.toLowerCase();
		String stringToCompare = null;
		char secondlastLetter = 0;
		String lastThreeLetters = "";
		String lastTwoLetters = "";
		stringToCompare = ABERRANT_RULES_MAP.get(singular);
		if (stringToCompare == null) {
			char lastLetter = singular.charAt(singular.length() - 1);
			if (singular.length() - 2 >= 0) {
				secondlastLetter = singular.charAt(singular.length() - 2);
				lastTwoLetters = singular.substring(singular.length() - 2);
			}
			if (singular.length() - 3 >= 0)
				lastThreeLetters = singular.substring(singular.length() - 3);

			if (lastLetter == 'y' && !VOWELS.contains(secondlastLetter)) {
				String prefix = (word).substring(0, word.length() - 1);
				return stringToCompare = prefix + "ies";
			} else if (lastLetter == 's') {
				if (VOWELS.contains(secondlastLetter)) {
					if (lastThreeLetters.equals("ius")) {
						String prefix = (word).substring(0, word.length() - 2);
						return stringToCompare = prefix + "i";
					} else {
						String prefix = (word).substring(0, word.length() - 1);
						return stringToCompare = prefix + "ses";
					}
				} else {
					return stringToCompare = word + "ses";
				}

			} else if (lastTwoLetters.equals("ch") || lastTwoLetters.equals("sh")) {
				return stringToCompare = word + "es";
			} else {
				return stringToCompare = word + "s";
			}
		}
		return stringToCompare;
	}

}
