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
 * Enum used for the type of matching ca.mcgill.cs.stg.fraco.rules used.
 * 
 * @author inder
 *
 */
public enum RuleTypes {
	IDENTICAL_MATCH, FUZZY_MATCH, LEXICAL_TOKENS_MATCH, SEMANTIC_TOKENS_MATCH, LEMMA_TOKENS_LEXICAL, LEMMA_TOKENS_SEMANTIC
}
