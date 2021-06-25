package gov.ca.portfolio.rebalancer.exception;

/**
 * The Exception to indicate that the provided input is not valid.
 *
 */
public class PortfolioInvalidInputException extends PortfolioException {

	private String fieldName;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor.
	 * 
	 * @param message
	 *            The exception message.
	 * @param fieldName
	 *            THe field that caused the invalid input.
	 */
	public PortfolioInvalidInputException(String message, String fieldName) {
		super(message);
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
