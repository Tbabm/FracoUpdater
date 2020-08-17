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

/**
 * @author inder
 *
 */
public class RulesFactory {

	// use getRules method to get object of type RuleTypes
	public final Rules getRule(RuleTypes ruleType) {

		switch (ruleType) {
		case IDENTICAL_MATCH:
			return new IdenticalMatch();
		case FUZZY_MATCH:
			return new FuzzyMatch();
		case LEMMA_TOKENS_SEMANTIC:
			return new LemmaAndTokensMatch();
		default:
			throw new IllegalArgumentException("Unknown ruleType" + ruleType.toString());
		}
	}

}
