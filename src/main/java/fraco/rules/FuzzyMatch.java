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
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author inder
 *
 */
public class FuzzyMatch implements Rules {

	private List<String> _tokens;
	private Comments _comment;
	private Identifier _identifier;
	private int _lineNumberMatch = 0;
	private List<Match> _matches;
	private static PreProcessing _preProcessing = new PreProcessing();
	private static CommonRules commonRules = new CommonRules();

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
		_lineNumberMatch = 0;

		// check if the comment is a copyright comment
		if (commonRules.copyrightComment(_comment))
			return false;
		else {

			initCommentProcessing();
			if (!_matches.isEmpty()) {
				_comment.set_matches(_matches);
				return true;
			}

		}

		return false;
	}

	private void initCommentProcessing() {
		_matches = new ArrayList<Match>();
		if (_comment.get_type() == CommentType.JDOCFREE || _comment.get_type() == CommentType.JDOCTAG)
			_lineNumberMatch = 1;
		else
			_lineNumberMatch = 0;
		Matcher regMatcher = _preProcessing.splitCommentIntoLines(_comment);
		while (regMatcher.find()) {
			String group = regMatcher.group();
			if (!group.isEmpty()) {
				Matcher innerMatcher = _preProcessing.splitCommentIntoLines(_comment);

				while (innerMatcher.find()) {
					if (_comment.get_type() != CommentType.LINE)
						_lineNumberMatch++;
					_tokens = _preProcessing.tokenizeComments(innerMatcher.group());
					compareTokens();
				}
			}
		}

	}

	private void compareTokens() {
		matchTokens(_identifier.get_name());
	}

	private void matchTokens(String term) {

		for (String token : _tokens) {
			// check if the lemma of the token is contained in the term, do not
			// process as it will generate false positives.
			String lemma = Lemmatizer.getInstance().lemmatize(token);
			if (!term.contains(lemma)) {
				double comparison = SimilarityTool.CompareStrings(term, token);
				if (comparison > 0.85 && comparison < 1) {
					_preProcessing.addMatchInstance(_matches, token, _lineNumberMatch);
				}
			}

		}

	}

}
