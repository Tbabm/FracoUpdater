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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;

// copy from Fraco package ca.mcgill.cs.stg.fraco.fraco.rules;

/**
 * @author inder
 *
 */
public class IdenticalMatch extends RulesObjects implements Rules {

	private static String _posTagIdentifier = "";
	private static String _pluralIdentifier;

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
		if (_identifier != null && _identifier.get_name() != null) {
			_posTagIdentifier = Lemmatizer.getInstance().PosTagging(_identifier.get_name().toLowerCase());
			_pluralIdentifier = commonRules.getPluralIdenticalMatch(_identifier);
			_isOneWord = false;
			_lineNumberMatch = 0;
			// check if the comment is a copyright comment
			if (commonRules.copyrightComment(_comment))
				return false;
			else {
				processIdentifier();
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

	private void processIdentifier() {
		_iTokens = _preProcessing.tokenizeIdentifier(_identifier.get_name());
		/*
		 * check if the identifier consists of one term or many. If one, process
		 * method type tokens to get additional tokens by processing class name
		 */
		if (_iTokens != null && _iTokens.size() == 1) {
			_isOneWord = true;
		}

	}

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

		// if(!isSeeTag(group) && _identifier.get_type()==Scope.METHOD)
		{
			Matcher innerMatcher = _preProcessing.splitCommentTextUsingDelimiters(group);
			while (innerMatcher.find()) {
				_tokens = _preProcessing.tokenizeComments(innerMatcher.group());
				incrementLineNumber();
				startComparison();

			}
		}
	}

	private boolean isSeeTag(String line) {
		_tokens = _preProcessing.tokenizeComments(line);
		line.replaceFirst("#", ".");
		if (_tokens.get(0).equals("see")) {
			if (_tokens.size() == 2 && _tokens.get(1).equalsIgnoreCase(_identifier.get_name()))
				_preProcessing.addMatchInstance(_matches, _identifier.get_name(), _lineNumberMatch);
			if (line.contains(_identifier.get_fullQualifiedName()))
				_preProcessing.addMatchInstance(_matches, _identifier.get_fullQualifiedName(), _lineNumberMatch);
			return true;
		}
		return false;
	}

	private void incrementLineNumber() {
		if (_comment.get_type() != CommentType.LINE)
			_lineNumberMatch++;
	}

	private void startComparison() {
		if (_comment.get_type() == CommentType.JDOCTAG && _identifier.get_type() == Type.LOCAL)
			processLocalIdentifier();
		else
			compareTokens();
	}

	private void processLocalIdentifier() {
		if (commonRules.matchLocalInJavaDoc(_tokens, _identifier)) {
			_preProcessing.addMatchInstance(_matches, _identifier, _lineNumberMatch);
		}
	}

	private void compareTokens() {
		_matchedTerms = new LinkedHashSet<String>();
		if (commonRules.checkProximity(_comment))
			matchTokens();
		else
			processNonProximityComment();
	}

	private void matchTokens() {

		for (String token : _tokens) {
			int index = _tokens.indexOf(token);
			if (index > 0 && _tokens.get(index - 1).equals("link")) {
				continue;
			} else if (_isOneWord) {
				matchOneWordIdentifiers(token);
			} else if (token.equals(_identifier.get_name())) {
				_preProcessing.addMatchInstance(_matches, token, _lineNumberMatch);
			}
		}
	}

	private void matchOneWordIdentifiers(String token) {
		if (token.equalsIgnoreCase(_identifier.get_name()) || token.equalsIgnoreCase(_pluralIdentifier)) {

			if (_identifier.get_type() == Type.CLASS) {
				if (_posTagIdentifier.equals("NN") || _posTagIdentifier.equals("NNP"))
					_preProcessing.addMatchInstance(_matches, token, _lineNumberMatch);
			} else
				_preProcessing.addMatchInstance(_matches, token, _lineNumberMatch);
		}

	}

	private void processNonProximityComment() {
		ProcessGlobalComment processGlobalComment = new ProcessGlobalComment(_comment, _isOneWord, _identifier,
				_lineNumberMatch, _tokens);
		processGlobalComment.obtainMatchInstance(_matches);
	}

}
