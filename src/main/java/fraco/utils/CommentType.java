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

public enum CommentType {
	JDOCFREE,
	JDOCTAG,
	JDOCTAGVALUE,
	LINE,
	BLOCK
}
