package com.finance.portfolio.rebalancer.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

//import javax.validation.constraints.AssertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base object to be used by all DTO's for capturing data per needs of interfaces
 */
public class AbstractDto {

	@JsonIgnore
	/**
	 * Non-ascii character pattern
	 */
	private static Pattern p = Pattern.compile("\\A\\p{ASCII}*\\z");

	/**
	 * Map that contains common unicode to ascii character mapping.
	 */
	@JsonIgnore
	private static Map<String, String> replacementChar = new HashMap<>();

	static {
		replacementChar.put("\u00AB", "\"");
		replacementChar.put("\u00AD", "-");
		replacementChar.put("\u00B4", "\'");
		replacementChar.put("\u00BB", "\"");
		replacementChar.put("\u00F7", "/");
		replacementChar.put("\u01C0", "|");
		replacementChar.put("\u01C3", "!");
		replacementChar.put("\u02B9", "\'");
		replacementChar.put("\u02BA", "\"");
		replacementChar.put("\u02BC", "\'");
		replacementChar.put("\u02C4", "^");
		replacementChar.put("\u02C6", "^");
		replacementChar.put("\u02C8", "\'");
		replacementChar.put("\u02CB", "`");
		replacementChar.put("\u02CD", "_");
		replacementChar.put("\u02DC", "~");
		replacementChar.put("\u0300", "`");
		replacementChar.put("\u0301", "\'");
		replacementChar.put("\u0302", "^");
		replacementChar.put("\u0303", "~");
		replacementChar.put("\u030B", "\"");
		replacementChar.put("\u030E", "\"");
		replacementChar.put("\u0331", "_");
		replacementChar.put("\u0332", "_");
		replacementChar.put("\u0338", "/");
		replacementChar.put("\u0589", ":");
		replacementChar.put("\u05C0", "|");
		replacementChar.put("\u05C3", ":");
		replacementChar.put("\u066A", "%");
		replacementChar.put("\u066D", "*");
		replacementChar.put("\u200B", " ");
		replacementChar.put("\u2010", "-");
		replacementChar.put("\u2011", "-");
		replacementChar.put("\u2012", "-");
		replacementChar.put("\u2013", "-");
		replacementChar.put("\u2014", "-");
		replacementChar.put("\u2015", "-");
		replacementChar.put("\u2016", "|");
		replacementChar.put("\u2017", "_");
		replacementChar.put("\u2018", "\'");
		replacementChar.put("\u2019", "\'");
		replacementChar.put("\u201A", ";");
		replacementChar.put("\u201B", "\'");
		replacementChar.put("\u201C", "\"");
		replacementChar.put("\u201D", "\"");
		replacementChar.put("\u201E", "\"");
		replacementChar.put("\u201F", "\"");
		replacementChar.put("\u2032", "\'");
		replacementChar.put("\u2033", "\"");
		replacementChar.put("\u2034", "\'");
		replacementChar.put("\u2035", "`");
		replacementChar.put("\u2036", "\"");
		replacementChar.put("\u2037", "\'");
		replacementChar.put("\u2038", "^");
		replacementChar.put("\u2039", "<");
		replacementChar.put("\u203A", ">");
		replacementChar.put("\u203D", "?");
		replacementChar.put("\u2044", "/");
		replacementChar.put("\u204E", "*");
		replacementChar.put("\u2052", "%");
		replacementChar.put("\u2053", "~");
		replacementChar.put("\u2060", " ");
		replacementChar.put("\u20E5", "\\");
		replacementChar.put("\u2212", "-");
		replacementChar.put("\u2215", "/");
		replacementChar.put("\u2216", "\\");
		replacementChar.put("\u2217", "*");
		replacementChar.put("\u2223", "|");
		replacementChar.put("\u2236", ":");
		replacementChar.put("\u223C", "~");
		replacementChar.put("\u2264", "<");
		replacementChar.put("\u2265", ">");
		replacementChar.put("\u2266", "<");
		replacementChar.put("\u2267", ">");
		replacementChar.put("\u2303", "^");
		replacementChar.put("\u2329", "<");
		replacementChar.put("\u232A", ">");
		replacementChar.put("\u266F", "#");
		replacementChar.put("\u2731", "*");
		replacementChar.put("\u2758", "|");
		replacementChar.put("\u2762", "!");
		replacementChar.put("\u27E6", "[");
		replacementChar.put("\u27E8", "<");
		replacementChar.put("\u27E9", ">");
		replacementChar.put("\u2983", "{");
		replacementChar.put("\u2984", "}");
		replacementChar.put("\u3003", "\"");
		replacementChar.put("\u3008", "<");
		replacementChar.put("\u3009", ">");
		replacementChar.put("\u301B", "]");
		replacementChar.put("\u301C", "~");
		replacementChar.put("\u301D", "\"");
		replacementChar.put("\u301E", "\"");
		replacementChar.put("\uFEFF", " ");
	}

	@JsonIgnore
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long id;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error("Unable to convert dto into string.", e);
			return "Unable to convert object to string.";
		}
	}
}
