package com.finance.portfolio.rebalancer.exception;

import java.security.SecureRandom;

/**
 * The Portfolio specific runtime exception.
 *
 */
public class PortfolioException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final SecureRandom rnd = new SecureRandom();

	private final String errorId;

	/**
	 * Constructing a new instance with a detail exception message and a cause exception.
	 *
	 * @param message
	 *            detail exception message
	 * @param cause
	 *            cause exception.
	 */
	public PortfolioException(String message, Throwable cause) {
		super(message, cause);
		errorId = generateId();
	}

	/**
	 * Constructing a new instance with a detail exception message.
	 *
	 * @param message
	 *            detail exception message.
	 */
	public PortfolioException(String message) {
		super(message);
		errorId = generateId();
	}

	/**
	 * Generate random 6 digits error to identify the exception.
	 * 
	 * @return The auto generated id.
	 */
	public static String generateId() {
		StringBuilder sb = new StringBuilder(6);
		for (int i = 0; i < 6; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}

	/**
	 * @return the errorId
	 */
	public String getErrorId() {
		return errorId;
	}

}
