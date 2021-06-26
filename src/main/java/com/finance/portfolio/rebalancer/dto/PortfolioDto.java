package com.finance.portfolio.rebalancer.dto;

import com.finance.portfolio.rebalancer.controller.PortfolioRebalanceController;

public class PortfolioDto extends AbstractDto {
	private Integer riskPreference;
	
	// always go with 5 categories
	private InvestmentCategoryDto[] investCategoryDtos = new InvestmentCategoryDto[5];
	
	public PortfolioDto() {
		
	}
	
	public PortfolioDto(Integer riskPreference, Integer bondPercentage, Integer largePercentage, Integer midPercentage, Integer foreignPercentage, Integer smallPercentage) {
		this.riskPreference = riskPreference;
		
		this.investCategoryDtos[0] = new InvestmentCategoryDto(PortfolioRebalanceController.CATEGORY_BONDS, bondPercentage);
		this.investCategoryDtos[1] = new InvestmentCategoryDto(PortfolioRebalanceController.CATEGORY_LARGE_CAP, largePercentage);
		this.investCategoryDtos[2] = new InvestmentCategoryDto(PortfolioRebalanceController.CATEGORY_MID_CAP, midPercentage);
		this.investCategoryDtos[3] = new InvestmentCategoryDto(PortfolioRebalanceController.CATEGORY_FOREIGN_CAP, foreignPercentage);
		this.investCategoryDtos[4] = new InvestmentCategoryDto(PortfolioRebalanceController.CATEGORY_SMALL_CAP, smallPercentage);
	}
	
	public Integer getRiskPreference() {
		return riskPreference;
	}
	public void setRiskPreference(Integer riskPreference) {
		this.riskPreference = riskPreference;
	}

	public InvestmentCategoryDto[] getInvestCategoryDtos() {
		return investCategoryDtos;
	}

	public void setInvestCategoryDtos(InvestmentCategoryDto[] investCategoryDtos) {
		this.investCategoryDtos = investCategoryDtos;
	}
	
}
