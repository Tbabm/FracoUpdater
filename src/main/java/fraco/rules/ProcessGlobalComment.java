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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author inder
 *
 */
public class ProcessGlobalComment {

	private List<String> _tokens;
	private LinkedHashSet<String> _iTokens;
	private boolean _isOneWord;
	private static PreProcessing _preProcessing = new PreProcessing();
	private Identifier _identifier;
	private int _lineNo;
	private LinkedHashSet<String> _matchedTerms = new LinkedHashSet<String>();
	private static CommonRules commonRules = new CommonRules();
	private String posTagIdentifier = "";
	private String posTagIdLowerCase = "";
	private static final String OPENING_BRACE = "(";
	private static final String CLOSING_BRACE = ")";

	private static Comments _comment;
	private static String type_Plural_s;
	private static String type_plural_es;
	private static String pluralIdentifier;

	public ProcessGlobalComment(Comments comment, boolean isOneWordIdentifier, Identifier identifier, int lineNo,
								List<String> tokens) {
		this._isOneWord = isOneWordIdentifier;
		_comment = comment;
		this._identifier = identifier;
		this._lineNo = lineNo;
		this._tokens = tokens;

		type_Plural_s = _identifier.get_type().toString() + "s";
		type_plural_es = _identifier.get_type().toString() + "es";
		String id = _identifier.get_name();
		this.posTagIdentifier = Lemmatizer.getInstance().PosTagging(id);
		this.posTagIdLowerCase = Lemmatizer.getInstance().PosTagging(id.toLowerCase());
		pluralIdentifier = commonRules.getPluralIdenticalMatch(_identifier);

	}

	private void processIdentifier() {
		_iTokens = _preProcessing.tokenizeIdentifier(_identifier.get_name());
		/*
		 * check if the identifier consists of one term or many. If one, process
		 * method type tokens to get additional tokens by processing class name
		 */
		if (_iTokens != null && _iTokens.size() == 1) {
			_isOneWord = true;
			HashSet<String> additionalTokens = _preProcessing.processOneWordIdentifier(_identifier);
			if (additionalTokens != null && !additionalTokens.isEmpty())
				_iTokens.addAll(additionalTokens);

		}

	}

	public void obtainMatchInstance(List<Match> matches) {
		processIdentifier();
		_matchedTerms = new LinkedHashSet<String>();
		for (int i = 0; i < _tokens.size(); i++) {
			String currentToken = _tokens.get(i);
			if (currentToken == null || _identifier.get_name() == null)
				continue;

			if (i > 0 && _tokens.get(i - 1).equals("link")) {
				continue;
			}
			if (!isExactMatch(currentToken, matches)) {
				if (!_isOneWord && !DetectFragileComments._identicalMatch) {
					compareTerms(i, matches);
					// add code for splitting identifier and checking the
					// sequence

				} else if (_isOneWord
						&& (_identifier.get_type() == Type.METHOD || _identifier.get_type() == Type.FIELD)) {
					getClassNameAndMatch(_tokens.get(i), matches);
				}

			}

		}
	}

	private void getClassNameAndMatch(String token, List<Match> matches) {
		if (_identifier.get_className() == null
				|| commonRules._stopWords.contains(_identifier.get_name().toLowerCase()))
			return;
		for (String term : _iTokens) {
			if (token.equals(term)) {
				if (!_matchedTerms.contains(token.toLowerCase())) {
					_matchedTerms.add(token.toLowerCase());
				}
				/*
				 * if(_iTokens.size()>2)
				 * checkAndAddMatches(_iTokens.size()-1,matches); else
				 */
				checkAndAddMatches(_iTokens.size(), matches);

			}
		}
	}

	private void compareTerms(int index, List<Match> matches) {
		int noOfMatchedTerms = 0;
		int matchedIndex = index;
		HashSet<String> termsList = _preProcessing.tokenizeIdentifier(_identifier.get_name());
		if (termsList == null)
			return;
		for (String term : termsList) {
			if (noOfMatchedTerms == 0 && term.equalsIgnoreCase(_tokens.get(index))) {
				noOfMatchedTerms++;
				matchedIndex++;
			} else if (noOfMatchedTerms > 0) {
				if (matchedIndex < _tokens.size() && term.equalsIgnoreCase(_tokens.get(matchedIndex))) {
					noOfMatchedTerms++;
					matchedIndex++;
				}
			}
		}

		// check the final number of terms matched
		if (noOfMatchedTerms == termsList.size()) {
			if (matchedIndex < _tokens.size()
					&& (_tokens.get(matchedIndex).equalsIgnoreCase(_identifier.get_type().toString())
							|| _tokens.get(matchedIndex).equalsIgnoreCase(type_plural_es)
							|| _tokens.get(matchedIndex).equalsIgnoreCase(type_Plural_s))) {
				_matchedTerms.addAll(termsList);
				_matchedTerms.add(_identifier.get_type().toString());
				checkAndAddMatches(noOfMatchedTerms + 1, matches);
			} else if (index > 0 && (_tokens.get(index - 1).equalsIgnoreCase(_identifier.get_type().toString())
					|| _tokens.get(index - 1).equalsIgnoreCase(type_plural_es)
					|| _tokens.get(index - 1).equalsIgnoreCase(type_Plural_s))) {
				_matchedTerms.addAll(termsList);
				_matchedTerms.add(_identifier.get_type().toString());
				checkAndAddMatches(noOfMatchedTerms + 1, matches);
			}
		}
	}

	private boolean isExactMatch(String token, List<Match> matches) {
		if (_isOneWord) {
			return matchOneWordIdentifiers(token, matches);
		} else if (_identifier.get_type() == Type.FIELD) {
			if (token.equals(_identifier.get_name()) && !_tokens.get(0).equals("param")) {
				_preProcessing.addMatchInstance(matches, token, _lineNo);
				return true;
			}
		} else if (_identifier.get_type() == Type.METHOD) {
			if (token.equals(_identifier.get_name()) && (!_tokens.get(0).equals("param")
					|| (_tokens.get(0).equals("param") && !_tokens.get(1).equals(token)))) {
				_preProcessing.addMatchInstance(matches, token, _lineNo);
				return true;
			}
		} else if (token.equals(_identifier.get_name()) || token.equals(pluralIdentifier)) {
			// if the identifier is not one word ,match the exact identifier and
			// class names , if yes add the match

			_preProcessing.addMatchInstance(matches, token, _lineNo);
			return true;
		}

		return false;
	}

	private boolean matchOneWordIdentifiers(String token, List<Match> matches) {
		if (_identifier.get_type() == Type.CLASS) {
			if (token.equals(_identifier.get_name()) || token.equals(pluralIdentifier)) {
				if ((posTagIdentifier.equals("NN") || posTagIdentifier.equals("NNP"))
						&& (posTagIdLowerCase.equals("NN") || posTagIdLowerCase.equals("NNP"))) {
					_preProcessing.addMatchInstance(matches, token, _lineNo);
					return true;
				}

			}
		} else if (_identifier.get_type() == Type.METHOD && token.equals(_identifier.get_name())) {
			if (applyBiGramRules(token, matches))
				return true;

		} else if (_identifier.get_type() == Type.FIELD) {
			if (_identifier.get_name().matches("[A-Z_0-9]+") && token.equals(_identifier.get_name())
					&& !_tokens.get(0).equals("param")) {
				_preProcessing.addMatchInstance(matches, token, _lineNo);
				return true;
			}

		}
		return false;
	}

	private boolean applyBiGramRules(String token, List<Match> matches) {

		int idIndex = _tokens.indexOf(token);
		// if the exact match is found along with the use of parenthesis
		if (_tokens.size() > idIndex + 1 && _tokens.get(idIndex + 1) != null
				&& _tokens.get(idIndex + 1).equals(OPENING_BRACE)) {
			int positionOfClosingBrace = 0;
			if (_identifier.get_parameterNames() != null)
				positionOfClosingBrace = _identifier.get_parameterNames().size() + 2 + idIndex;
			else
				positionOfClosingBrace = 2 + idIndex;
			if (_tokens.size() > positionOfClosingBrace && _tokens.get(positionOfClosingBrace) != null
					&& _tokens.get(positionOfClosingBrace).equals(CLOSING_BRACE)) {
				if (containsParentName(idIndex)) {
					_preProcessing.addMatchInstance(matches, token + OPENING_BRACE, _lineNo);
					return true;
				}
			}

		}
		// check if the identifier type appears after the occurrence of
		// identifier
		/*
		 * else if(_tokens.size()>idIndex+1 && _identifier.get_type()!=null &&
		 * !commonRules._stopWords.contains(_identifier.get_name().toLowerCase()
		 * ) && _tokens.get(idIndex+1)!=null &&
		 * _tokens.get(idIndex+1).equalsIgnoreCase(_identifier.get_type().
		 * toString())) { _matchedTerms.add(_identifier.get_name());
		 * _matchedTerms.add(_tokens.get(idIndex+1));
		 * checkAndAddMatches(2,matches); return true; } //check if the
		 * identifier type appears before the occurrence of identifier else
		 * if(_tokens.size()>2 && idIndex>0 && _identifier.get_type()!=null &&
		 * !commonRules._stopWords.contains(_identifier.get_name().toLowerCase()
		 * ) && _tokens.get(idIndex-1)!=null &&
		 * _tokens.get(idIndex-1).equalsIgnoreCase(_identifier.get_type().
		 * toString())) { _matchedTerms.add(_tokens.get(idIndex-1));
		 * _matchedTerms.add(_identifier.get_name());
		 * checkAndAddMatches(2,matches); return true; }
		 */
		return false;
	}

	private boolean containsParentName(int index) {
		if (_identifier.get_className() != null && _comment.get_className() != null) {
			if (!_comment.get_className().equals(_identifier.get_className())) {
				if (_tokens.get(index - 1).equals(_identifier.get_className()))
					return true;
			} else
				return true;
		}
		return false;

	}

	// check matched instances
	private void checkAndAddMatches(int requiredSize, List<Match> matches) {
		String matchInstance = "";
		if (!_matchedTerms.isEmpty() && commonRules.filterStopWords(_matchedTerms)) {
			if (_matchedTerms.size() == requiredSize) {
				matchInstance = _preProcessing.getMatchString(requiredSize, _matchedTerms);
				_preProcessing.addMatchInstance(matches, matchInstance, _lineNo);
				_matchedTerms = new LinkedHashSet<String>();
			}

		}
	}
}
