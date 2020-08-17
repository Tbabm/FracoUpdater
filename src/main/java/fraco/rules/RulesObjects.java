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

import fraco.utils.Comments;
import fraco.utils.Identifier;
import fraco.utils.Match;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author inder
 *
 */
public class RulesObjects {

	public List<String> _tokens;
	public LinkedHashSet<String> _iTokens;
	public Comments _comment;
	public Identifier _identifier;
	public int _lineNumberMatch = 0;
	public LinkedHashSet<String> _matchedTerms;
	public boolean _isOneWord;
	public List<Match> _matches;

	public static PreProcessing _preProcessing = new PreProcessing();
	public static CommonRules commonRules = new CommonRules();

}
