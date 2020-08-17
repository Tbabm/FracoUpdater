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
/**
 * 
 */
package fraco.rules;

import fraco.utils.*;
import org.eclipse.core.runtime.Assert;

import java.util.*;
import java.util.regex.Matcher;

/**
 * @author inder
 *
 */
public class LemmaAndTokensMatch extends RulesObjects implements Rules {

	private Map<String, String> _iTokensMap;

	private Map<String, String> _tokensMap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.mcgill.cs.stg.fraco.fraco.rules.Rules#ApplyRules(ca.mcgill.cs.stg.fraco.
	 * utils.Identifier, ca.mcgill.cs.stg.fraco.utils.Comments)
	 */
	@Override
	public boolean ApplyRules(Identifier identifier, Comments comment) {
		this._comment = comment;
		this._identifier = identifier;
		_isOneWord = false;
		_iTokens = null;
		_lineNumberMatch = 0;

		// check if the comment is a copyright comment
		if (commonRules.copyrightComment(_comment))
			return false;
		else {
			processIdentifier();
			if (!_isOneWord) {
				initCommentProcessing();
				return setMatches();
			}

		}

		return false;
	}

	private boolean setMatches() {
		if (!_matches.isEmpty()) {
			_comment.set_matches(_matches);
			return true;
		}
		return false;
	}

	/**
	 * Tokenize the identifier based on camel casing fraco.rules. If the identifier
	 * comprises of one word, add the name of the parent class to the tokenized
	 * words list. Apply Lemmatization after applying the tokenization.
	 * 
	 */
	private void processIdentifier() {
		_iTokens = _preProcessing.tokenizeIdentifier(_identifier.get_name());
		/*
		 * check if the identifier consists of one term or many. If one, process
		 * method type tokens to get additional tokens by processing class name
		 */
		if (_iTokens != null && _iTokens.size() == 1) {
			_isOneWord = true;
			addClassnameToTerms();
		}

		// lemmatize the Identifier to find the root of the words

		lemmatizeIdentifier();
	}

	private void addClassnameToTerms() {
		HashSet<String> additionalTokens = _preProcessing.processOneWordIdentifier(_identifier);
		if (additionalTokens != null && !additionalTokens.isEmpty())
			_iTokens.addAll(additionalTokens);
	}

	/**
	 * Find the lemma of the word by using StanfordCoreNLP methods and create a
	 * map containing both identifier terms and their lemmas.
	 */
	private void lemmatizeIdentifier() {
		if (_iTokens == null || _iTokens.isEmpty())
			return;
		_iTokensMap = new LinkedHashMap<String, String>();
		for (String iToken : _iTokens) {
			if (commonRules._stopWords.contains(iToken.toLowerCase()))
				_iTokensMap.put(iToken, iToken.toLowerCase());
			else
				_iTokensMap.put(iToken, Lemmatizer.getInstance().lemmatize(iToken.toLowerCase()));

		}
	}

	/**
	 * Call the process identifier and lemmatize method before calling this
	 * method. It starts the process of breaking comment into sentences and
	 * further into words. Depending upon the type of identifier, different
	 * procedure is applied to further analyze the comments.
	 */
	private void initCommentProcessing() {
		_matches = new ArrayList<Match>();
		_lineNumberMatch = -1;
		applyNewlineSplitPattern();

	}

	private void applyNewlineSplitPattern() {
		Matcher regMatcher = _preProcessing.splitCommentIntoLines(_comment);
		while (regMatcher.find()) {
			String group = regMatcher.group();
			// if(_comment.get_type()!=CommentType.LINE)
			// _lineNumberMatch++;
			if (!group.isEmpty()) {
				applySentenceSplitPattern(group);
			}
		}
	}

	private void applySentenceSplitPattern(String group) {
		Matcher innerMatcher = _preProcessing.splitCommentTextUsingDelimiters(group);
		while (innerMatcher.find()) {
			_tokens = _preProcessing.tokenizeComments(innerMatcher.group());
			lemmatizeTokens();
			incrementLineNumber();
			startComparison();
		}
	}

	private void incrementLineNumber() {
		if (_comment.get_type() != CommentType.LINE)
			_lineNumberMatch++;
	}

	private void startComparison() {
		if (_comment.get_type() == CommentType.JDOCTAG && _identifier.get_type() == Type.LOCAL) {
			processLocalIdentifier();
		} else if (_comment.get_type() == CommentType.JDOCTAG && _identifier.get_type() == Type.METHOD)
			removeParamInMethodJavadoc();
		else
			compareTokens();
	}

	/**
	 * Find the lemma of the words by using StanfordCoreNLP methods and create a
	 * map containing both comment tokens and their lemmas by removing any
	 * stopwords appearing in the comments.
	 */
	private void lemmatizeTokens() {
		if (_tokens == null || _tokens.isEmpty())
			return;
		_tokensMap = new LinkedHashMap<String, String>();
		for (String token : _tokens) {
			if (commonRules._stopWords.contains(token.toLowerCase()))
				_tokensMap.put(token, token.toLowerCase());
			else
				_tokensMap.put(token, (Lemmatizer.getInstance().lemmatize(token.toLowerCase())));
		}
	}

	/**
	 * If the identifier is of type local, analyze the comment by applying the
	 * fraco.rules specific to local variables. The fraco.rules are defined in CommonRules
	 * class.
	 */
	private void processLocalIdentifier() {

		if (commonRules.matchLocalInJavaDoc(_tokens, _identifier)) {
			_preProcessing.addMatchInstance(_matches, _identifier, _lineNumberMatch);
		}

	}

	/**
	 * If the number of tokens in a sentence is more than 2 and the sentence
	 * starts with a "@param" tag remove the parameter name written with the
	 * param tag in order to avoid matching the coincidental similarity between
	 * parameter name and method name.
	 */
	private void removeParamInMethodJavadoc() {

		// if(commonRules.processMethodJavadoc(_comment.get_comment(),
		// _identifier))
		Assert.isNotNull(_tokens);
		if (_tokens.size() > 2 && ((_tokens.get(0)).equalsIgnoreCase("param"))) {
			_tokens.remove(1);

		}
		compareTokens();

	}

	/**
	 * This method compares the tokens with identifier terms in order to find
	 * the semantic matches. Comparison is only applicable to local comments, so
	 * it is necessary to check the proximity of the comment.
	 */
	private void compareTokens() {
		_matchedTerms = new LinkedHashSet<String>();
		if (_iTokens == null || _iTokensMap == null)
			return;
		if (commonRules.checkProximity(_comment)) {
			matchTokens();
			checkForGetterAndSetter();
		}

	}

	// check matched instances
	/**
	 * It checks if the number of required matching terms of identifier is equal
	 * to the number of tokens matched in a comment if yes, it adds a match.
	 * 
	 * @param requiredSize
	 *            the number of tokens size required to consider it as a match.
	 */
	private void checkAndAddMatches(int requiredSize) {
		String matchInstance = "";
		if (!_matchedTerms.isEmpty() && commonRules.filterStopWords(_matchedTerms)) {
			if (_matchedTerms.size() == requiredSize) {
				matchInstance = _preProcessing.getMatchString(_iTokens.size(), _matchedTerms);
				_preProcessing.addMatchInstance(_matches, matchInstance, _lineNumberMatch);
				_matchedTerms = new LinkedHashSet<String>();
			}

		}
	}

	/**
	 * It Checks if it is a method starting with word "get" or "is". If yes, it
	 * marks the required size of tokens list as 1 less than the required to
	 * account for the semantically similar words. It is a work around to
	 * account for semantically similar words and can generate false positives
	 * as well.
	 * 
	 */
	private void checkForGetterAndSetter() {
		if (_iTokens.contains("get") || _iTokens.contains("is") || _iTokens.contains("Is")
				|| _iTokens.contains("Get")) {
			if (!commonRules.containsStopWords(_matchedTerms))
				checkAndAddMatches(_iTokens.size() - 1);
		}
	}

	/**
	 * it mathces the tokens against terms of the identifier. It also checks for
	 * the match between singular and plural forms of the identifier terms.
	 */
	private void matchTokens() {
		if (_tokens.isEmpty() || _tokensMap.isEmpty() || _iTokensMap.values().isEmpty())
			return;
		for (String token : _tokensMap.keySet()) {
			if (token == null)
				continue;

			if ((_tokensMap.get(token) != null && (_iTokensMap.values().contains(_tokensMap.get(token))
					|| _iTokensMap.keySet().contains(token)))) {
				if (!_matchedTerms.contains(token.toLowerCase()) && !_matchedTerms.contains(token.toLowerCase() + "s")
						&& stripLastLetter(token)) {
					_matchedTerms.add(token.toLowerCase());
				}
				checkAndAddMatches(_iTokens.size());
			} else {
				checkForReturnTag(token);
			}
		}
	}

	/**
	 * @param token
	 *            the token to be checked for plural match.
	 * @return false if the token/ tokens plural is already there in the matched
	 *         terms list.
	 */
	private boolean stripLastLetter(String token) {
		if (token.endsWith("s")) {
			if (_matchedTerms.contains(token.substring(0, token.length() - 1)))
				return false;
		}

		return true;
	}

	/**
	 * It matches the word "return" in place of "get" for the getter methods.
	 * 
	 * @param token
	 */
	private void checkForReturnTag(String token) {

		if (commonRules.containsReturnTag(token, _iTokensMap.values(), _matchedTerms))
			checkAndAddMatches(_iTokens.size());

	}

}
