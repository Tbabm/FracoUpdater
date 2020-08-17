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

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Lemmatizer is used to find the lemma of words which is further used in
 * matching ca.mcgill.cs.stg.fraco.rules. This class is used to find POS tags
 * and to tokenize the sentences as well.
 * 
 * @author inder
 *
 */
public class Lemmatizer {
	@SuppressWarnings("serial")
	protected final static StanfordCoreNLP pipeline = new StanfordCoreNLP(new Properties() {
		{
			put("annotators", "tokenize,ssplit, pos, lemma");
		}
	});

	private static Lemmatizer _lemmatizer = new Lemmatizer();

	private Lemmatizer() {
	}

	/* Static 'instance' method */
	public static Lemmatizer getInstance() {
		return _lemmatizer;
	}

	/**
	 * @param token
	 *            the token which we need to lemmatize
	 * @return the lemma of the provided token
	 */
	public String lemmatize(String token) {

		Annotation tokenAnnotation = new Annotation(token);
		pipeline.annotate(tokenAnnotation); // necessary for the LemmaAnnotation
											// to be set.
		List<CoreLabel> list = tokenAnnotation.get(TokensAnnotation.class);
		String tokenLemma = null;
		if (list != null && list.size() >= 1) {
			tokenLemma = list.get(0).lemma();
		}

		return tokenLemma;
	}

	/**
	 * @param line
	 *            split the sentence/line into tokens.
	 * @return th list of tokens
	 */
	public List<String> tokenize(String line) {

		Annotation tokenAnnotation = new Annotation(line);
		pipeline.annotate(tokenAnnotation);
		List<CoreLabel> tokens = tokenAnnotation.get(TokensAnnotation.class);
		List<String> commentTokens = new ArrayList<String>();
		// traversing the words in the current sentence
		// a CoreLabel is a CoreMap with additional token-specific methods
		for (CoreLabel token : tokens) {
			// this is the text of the token
			String word = token.get(TextAnnotation.class);
			commentTokens.add(word);

		}
		return commentTokens;
	}

	/**
	 * @param token
	 *            for which we need to find the POS tag.
	 * @return return the POS tag for token.
	 */
	public String PosTagging(String token) {

		Annotation annotation = new Annotation(token);
		pipeline.annotate(annotation);
		List<CoreLabel> list = annotation.get(TokensAnnotation.class);
		String posTag = null;
		if (list != null && list.size() >= 1) {
			posTag = list.get(0).get(PartOfSpeechAnnotation.class);
		}
		return posTag;

	}

}
