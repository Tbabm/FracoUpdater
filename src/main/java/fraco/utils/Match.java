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
package fraco.utils;

/**
 * Used to store the data related to match instances.
 * 
 * @author inder
 *
 */
public class Match {
	private String _matchString;
	private int _matchPosition;
	private int _matchId;

	/**
	 * @return the _matchString
	 */
	public String get_matchString() {
		return _matchString;
	}

	/**
	 * @param _matchString
	 *            the _matchString to set
	 */
	public void set_matchString(String _matchString) {
		this._matchString = _matchString;
	}

	/**
	 * @return the _matchPosition
	 */
	public int get_matchPosition() {
		return _matchPosition;
	}

	/**
	 * @param _matchPosition
	 *            the _matchPosition to set
	 */
	public void set_matchPosition(int _matchPosition) {
		this._matchPosition = _matchPosition;
	}

	/**
	 * @return the _matchId
	 */
	public int get_matchId() {
		return _matchId;
	}

	/**
	 * @param _matchId
	 *            the _matchId to set
	 */
	public void set_matchId(int _matchId) {
		this._matchId = _matchId;
	}

}
