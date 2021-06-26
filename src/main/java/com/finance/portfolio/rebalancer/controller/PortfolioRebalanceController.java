package com.finance.portfolio.rebalancer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.finance.portfolio.rebalancer.dto.AmountDto;
import com.finance.portfolio.rebalancer.dto.InvestmentCategoryDto;
import com.finance.portfolio.rebalancer.dto.PortfolioDto;
import com.finance.portfolio.rebalancer.dto.TransferenceDto;
import com.finance.portfolio.rebalancer.exception.PortfolioInvalidInputException;

/**
 * This is Portfolio Rebalance Controller.
 * @author Virinder Singh
 */

@RestController
public class PortfolioRebalanceController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// We are assuming there are 5 investment categories as follows
	public static final String CATEGORY_BONDS = "BONDS";
	public static final String CATEGORY_LARGE_CAP = "LARGE_CAP";
	public static final String CATEGORY_MID_CAP = "MID_CAP";
	public static final String CATEGORY_FOREIGN_CAP = "FOREIGN_CAP";
	public static final String CATEGORY_SMALL_CAP = "SMALL_CAP";
	
	/**
	 * Map from riskPreference to PortfolioDto.
	 * Assume the following recommended investment category percentages for each risk preference between 1-10
	 */
	public static final Map<Integer, PortfolioDto> RISK_PREF_TO_PORTFOLIO = new HashMap<>();
	static {
		RISK_PREF_TO_PORTFOLIO.put(1, new PortfolioDto(1, 80, 20, 0, 0, 0));
		RISK_PREF_TO_PORTFOLIO.put(2, new PortfolioDto(2, 70, 15, 15, 0, 0));
		RISK_PREF_TO_PORTFOLIO.put(3, new PortfolioDto(3, 60, 15, 15, 10, 0));
		RISK_PREF_TO_PORTFOLIO.put(4, new PortfolioDto(4, 50, 20, 20, 10, 0));
		RISK_PREF_TO_PORTFOLIO.put(5, new PortfolioDto(5, 40, 20, 20, 20, 0));
		RISK_PREF_TO_PORTFOLIO.put(6, new PortfolioDto(6, 35, 25, 5, 30, 5));
		RISK_PREF_TO_PORTFOLIO.put(7, new PortfolioDto(7, 20, 25, 25, 25, 5));
		RISK_PREF_TO_PORTFOLIO.put(8, new PortfolioDto(8, 10, 20, 40, 20, 10));
		RISK_PREF_TO_PORTFOLIO.put(9, new PortfolioDto(9, 5, 15, 40, 25, 15));
		RISK_PREF_TO_PORTFOLIO.put(10, new PortfolioDto(10, 0, 5, 25, 30, 40));
	}
	
	@RequestMapping(path = "/welcome", method = RequestMethod.GET)
	public String welcome(HttpServletRequest request, HttpServletResponse response) {
		return "Welcome message from Portfolio Rebalancer Spring Boot App";
	}
	
	@GetMapping(path = "/portfolio/{riskPreference}", produces = MediaType.APPLICATION_JSON_VALUE)
	public PortfolioDto getRecommendedPortfolioPercentsByRiskPreference(@PathVariable Integer riskPreference) {
		logger.info("Received riskPreference: " + riskPreference);
		PortfolioDto portfolioDto = RISK_PREF_TO_PORTFOLIO.get(riskPreference);
		if (portfolioDto == null) {
			throw new PortfolioInvalidInputException("Invalid input riskPreference: " + riskPreference, "riskPreference");
		}
		return portfolioDto;
	}
	
	@PostMapping(path = "/portfolio/rebalance/{riskPreference}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<TransferenceDto> rebalancePortfolio(@RequestBody AmountDto[] currentAmountDtos, @PathVariable Integer riskPreference) {
		logger.info("Received riskPreference: " + riskPreference);
		PortfolioDto portfolioDto = RISK_PREF_TO_PORTFOLIO.get(riskPreference);
		if (portfolioDto == null) {
			throw new PortfolioInvalidInputException("Invalid input riskPreference: " + riskPreference, "riskPreference");
		}
		InvestmentCategoryDto[] investCategoryDtos = portfolioDto.getInvestCategoryDtos();
		
		logger.info("Received currentAmounts array: " + Arrays.toString(currentAmountDtos));
		if (currentAmountDtos.length != 5) {
			throw new PortfolioInvalidInputException("Invalid input array length: " + currentAmountDtos.length, "currentAmountsLength");
		}
		// Need to copy currentAmountDtos to the currentAmounts in the following order: 
		// 0 -> BONDS, 1 -> LARGE_CAP, 2 -> MID_CAP, 3 -> FOREIGN_CAP, 4 -> SMALL_CAP
		// this will ensure the input amounts can be provided in any order, we always use them internally in above order
		Double[] currentAmounts = new Double[currentAmountDtos.length];
		currentAmounts[0] = this.getAmountByCategory(currentAmountDtos, PortfolioRebalanceController.CATEGORY_BONDS);
		currentAmounts[1] = this.getAmountByCategory(currentAmountDtos, PortfolioRebalanceController.CATEGORY_LARGE_CAP);
		currentAmounts[2] = this.getAmountByCategory(currentAmountDtos, PortfolioRebalanceController.CATEGORY_MID_CAP);
		currentAmounts[3] = this.getAmountByCategory(currentAmountDtos, PortfolioRebalanceController.CATEGORY_FOREIGN_CAP);
		currentAmounts[4] = this.getAmountByCategory(currentAmountDtos, PortfolioRebalanceController.CATEGORY_SMALL_CAP);
		
		Double[] desiredAmounts = new Double[currentAmounts.length];
		Double[] sellAmounts = new Double[currentAmounts.length];
		Double[] buyAmounts = new Double[currentAmounts.length];
		double totalAmount=0;
		for (Double currentAmount : currentAmounts) {
			totalAmount += currentAmount.intValue();
		}
		// Compute desiredAmounts, sellAmounts and buyAmounts for each investment category
		for (int i=0;i<currentAmounts.length; i++) {
			// Assume: the investCategories in PortfolioDto is in same order as the values in currentAmounts
			desiredAmounts[i] = totalAmount *  investCategoryDtos[i].getPercent().intValue()/100.0;
			// Sell all those categories having more than the desired amounts
			if (currentAmounts[i].intValue() > desiredAmounts[i].doubleValue()) {
				sellAmounts[i] = currentAmounts[i].intValue() - desiredAmounts[i].doubleValue();
				buyAmounts[i] = 0.0;
			} else {
				sellAmounts[i] = 0.0;
				buyAmounts[i] = desiredAmounts[i].doubleValue() - currentAmounts[i].intValue();
			}
		}
		this.printArrays(currentAmounts, desiredAmounts, sellAmounts, buyAmounts);
		
		List<TransferenceDto> transferenceDtos = new ArrayList<>();
		this.handleMatchingTransfersBetweenInvestments(portfolioDto, currentAmounts, desiredAmounts, sellAmounts, buyAmounts, transferenceDtos);
		this.handleNonMatchingTransfersBetweenInvestments(portfolioDto, currentAmounts, desiredAmounts, sellAmounts, buyAmounts, transferenceDtos);
		
		for (TransferenceDto transferenceDto : transferenceDtos) {
			logger.info("Transference: " + transferenceDto.getRecommededTransfer());
		}
		return transferenceDtos;
	}
	
	// Find the matching amount by input category name in the given array
	private Double getAmountByCategory(AmountDto[] amountDtos, String category) {
		for (AmountDto amountDto : amountDtos) {
			if (amountDto != null && amountDto.getCategory() != null && amountDto.getCategory().equalsIgnoreCase(category)) {
				return amountDto.getAmount();
			}
		}
		throw new PortfolioInvalidInputException("Invalid input for AmountDtos. Cannot find category: " + category, "categoryName");
	}
	
	// This method handles the case where the sell amounts of investments matches the buy amounts of other investments entirely.
	private void handleMatchingTransfersBetweenInvestments(PortfolioDto portfolioDto, Double[] currentAmounts, Double[] desiredAmounts, Double[] sellAmounts, Double[] buyAmounts, List<TransferenceDto> transferenceDtos) {
		// Checking if any buy amounts match the sell amounts entirely
		for (int i=0;i<currentAmounts.length;i++) {
			Double buyAmt = buyAmounts[i];
			if (buyAmt == 0.0) {
				// this category does not need any more money
				continue;
			}
			int sellIndex = this.doesSellAmountMatch(currentAmounts.length, sellAmounts, buyAmt);
			if (sellIndex != -1) {
				// this index i will receive buyAmt from sellIndex
				this.addTransferenceDto(portfolioDto, transferenceDtos, sellIndex, i, buyAmt);
				currentAmounts[i] = currentAmounts[i] + buyAmt;
				buyAmounts[i] = 0.0;
				currentAmounts[sellIndex] = currentAmounts[sellIndex] - buyAmt;
				sellAmounts[sellIndex] = sellAmounts[sellIndex] - buyAmt;
			}
		}
		logger.info("After handling matching transfer between investment categories");
		this.printArrays(currentAmounts, desiredAmounts, sellAmounts, buyAmounts);
	}
	
	// This method handles the cases where the sell amounts and buy amounts do not match.
	private void handleNonMatchingTransfersBetweenInvestments(PortfolioDto portfolioDto, Double[] currentAmounts, Double[] desiredAmounts, Double[] sellAmounts, Double[] buyAmounts, List<TransferenceDto> transferenceDtos) {
		// Now handle partial transfers between investment categories
		for (int i=0;i<currentAmounts.length;i++) {
			Double buyAmt = buyAmounts[i]; // save the original buyAmt
			if (buyAmt == 0.0) {
				// this category does not need any more money
				continue;
			}
			// find which other category has money to satisfy this category
			int sellIndex = this.canProvideAmount(currentAmounts.length, sellAmounts);
			if (sellIndex != -1) {
				Double transferAmt = sellAmounts[sellIndex];
				if (transferAmt < buyAmounts[i]) {
					// the sellAmount is less than buyAmt, so take whatever is avail
					buyAmounts[i] = buyAmounts[i] - transferAmt;
					currentAmounts[i] = currentAmounts[i] + transferAmt;
					currentAmounts[sellIndex] = currentAmounts[sellIndex] - transferAmt;
					sellAmounts[sellIndex] = sellAmounts[sellIndex] - transferAmt;
					this.addTransferenceDto(portfolioDto, transferenceDtos, sellIndex, i, transferAmt);
				} else {
					// sellAmount is greater than or equal to buyAmt, so we get the full buyAmt
					buyAmounts[i] = 0.0;
					currentAmounts[i] = currentAmounts[i] + buyAmt;
					currentAmounts[sellIndex] = currentAmounts[sellIndex] - buyAmt;
					sellAmounts[sellIndex] = sellAmounts[sellIndex] - buyAmt;
					this.addTransferenceDto(portfolioDto, transferenceDtos, sellIndex, i, buyAmt);
				}
			}
		}
		logger.info("After handling non matching transfer between investment categories");
		this.printArrays(currentAmounts, desiredAmounts, sellAmounts, buyAmounts);
		// if there is at least one buyAmt>0, we must call this method recursively
		if (this.doesBuyAmtGreaterThanZeroExist(currentAmounts.length, buyAmounts)) {
			this.handleNonMatchingTransfersBetweenInvestments(portfolioDto, currentAmounts, desiredAmounts, sellAmounts, buyAmounts, transferenceDtos);
		}
	}
	
	// This method sets the transference required for a move
	private void addTransferenceDto(PortfolioDto portfolioDto, List<TransferenceDto> transferenceDtos, int fromIndex, int toIndex, Double amount) {
		InvestmentCategoryDto[] investCategoryDtos = portfolioDto.getInvestCategoryDtos();
		String fromInvestCategoryName = investCategoryDtos[fromIndex].getName();
		String toInvestCategoryName = investCategoryDtos[toIndex].getName();
		TransferenceDto transferDto = new TransferenceDto("Move " + amount + " from " + fromInvestCategoryName + " to " + toInvestCategoryName);
		transferenceDtos.add(transferDto);
	}
	
	// This is debugging method to print the various arrays
	private void printArrays(Double[] currentAmounts, Double[] desiredAmounts, Double[] sellAmounts, Double[] buyAmounts) {
		logger.info("#### BEGIN ARRAYS #### ");
		for (int i=0;i<currentAmounts.length;i++) {
			logger.info(i + " : currentAmt: " + currentAmounts[i] + " : desired: " + desiredAmounts[i] + " : sellAmt: " + sellAmounts[i] + " : buyAmt: " + buyAmounts[i]);
		}
		logger.info("#### END ARRAYS #### ");
	}
	
	// This method checks if there is any buyAmt greater than 0
	private boolean doesBuyAmtGreaterThanZeroExist(int numInvestmentCategories, Double[] buyAmounts) {
		for (int i=0;i<numInvestmentCategories;i++) {
			if (buyAmounts[i].doubleValue() > 0.0) {
				logger.info("Found buyAmt more than 0 for index: " + i);
				return true;
			}
		}
		return false;
	}
	
	// This method checks if sellAmt of any category matches the given input amount
	private int doesSellAmountMatch(int numInvestmentCategories, Double[] sellAmounts, double amount) {
		for (int i=0;i<numInvestmentCategories;i++) {
			if (sellAmounts[i].doubleValue() == amount) {
				logger.info("Found sellAmt for index: " + i + " which matches amount: " + amount);
				return i;
			}
		}
		return -1;
	}
	
	// This method checks if the sellAmt is greater than 0 and therefore can provide that amount to buyer
	private int canProvideAmount(int numInvestmentCategories, Double[] sellAmounts) {
		for (int i=0;i<numInvestmentCategories;i++) {
			// if sellAmount is greater than 0, it can provide that amount
			if (sellAmounts[i].doubleValue() > 0.0) {
				logger.info("Found sellAmt for index: " + i + " which is greater than 0");
				return i;
			}
		}
		return -1;
	}	
}
