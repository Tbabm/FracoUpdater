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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.TagElement;

import java.util.List;

public class Comments {

	private String _comment;
	private int _lineNumber;
	private boolean _isProximityComment;
	private CommentType _type;
	private List<Match> _matches;

	public Comments(String comment, Boolean isProximity, CommentType type){
		this._comment = comment;
		this._isProximityComment = isProximity;
		this._type = type;
	}

	/**
	 * @return the comment string
	 */
	public String get_comment() {
		return _comment;
	}

	public void set_comment(String _comment) {
		this._comment = _comment;
	}

	public CommentType get_type() {
		return _type;
	}

	public void set_type(CommentType _type) {
		this._type = _type;
	}

	private String _className;

	public String get_className() {
		return _className;
	}

	public void set_className(String _className) {
		this._className = _className;
	}

	/**
	 * @return the _lineNumber
	 */
	public int get_lineNumber() {
		return _lineNumber;
	}

	/**
	 * @param _lineNumber
	 *            the _lineNumber to set
	 */
	public void set_lineNumber(int _lineNumber) {
		this._lineNumber = _lineNumber;
	}

	/**
	 * @return the _isProximityComment
	 */
	public boolean is_isProximityComment() {
		return _isProximityComment;
	}

	/**
	 * @param _isProximityComment
	 *            the _isProximityComment to set
	 */
	public void set_isProximityComment(boolean _isProximityComment) {
		this._isProximityComment = _isProximityComment;
	}

	/**
	 * @return the _matches
	 */
	public List<Match> get_matches() {
		return _matches;
	}

	/**
	 * @param _matches
	 *            the _matches to set
	 */
	public void set_matches(List<Match> _matches) {
		this._matches = _matches;
	}

}
