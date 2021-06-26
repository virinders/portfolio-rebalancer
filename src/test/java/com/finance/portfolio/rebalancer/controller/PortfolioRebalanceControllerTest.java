package com.finance.portfolio.rebalancer.controller;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.finance.portfolio.rebalancer.dto.AmountDto;
import com.finance.portfolio.rebalancer.dto.PortfolioDto;
import com.finance.portfolio.rebalancer.dto.TransferenceDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PortfolioRebalanceControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	HttpHeaders headers;
	
	@Before
	public void setupMock() {
		headers = new HttpHeaders();
	}
	
	/**
	 * This method tests testGetRecommendedPortfolioPercentsByRiskPreference.
	 *
	 */
	@Test
	public void testGetRecommendedPortfolioPercentsByRiskPreference() {
		
		Integer riskPreference = 4;
		ResponseEntity<PortfolioDto> portfolioDto = this.restTemplate.exchange("/portfolio/" + riskPreference, HttpMethod.GET,
				new HttpEntity<>(headers), PortfolioDto.class);
		
		Assert.assertEquals(HttpStatus.OK, portfolioDto.getStatusCode());
		PortfolioDto expectedPortfolioDto = PortfolioRebalanceController.RISK_PREF_TO_PORTFOLIO.get(riskPreference);
		
		validate(expectedPortfolioDto, portfolioDto.getBody());

	}
	
	/**
	 * This method tests testGetRecommendedPortfolioPercentsByRiskPreferenceInvalid.
	 *
	 */
	@Test
	public void testGetRecommendedPortfolioPercentsByRiskPreferenceInvalid() {
		
		Integer riskPreference = 40;
		ResponseEntity<PortfolioDto> portfolioDto = this.restTemplate.exchange("/portfolio/" + riskPreference, HttpMethod.GET,
				new HttpEntity<>(headers), PortfolioDto.class);
		
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, portfolioDto.getStatusCode());

	}
	
	/**
	 * This method tests rebalance portfolio
	 *
	 *
	 */
	@Test
	public void testRebalancePortfolio() {

		Integer riskPreference = 4;
		AmountDto[] currentAmounts = new AmountDto[5];
		currentAmounts[0] = new AmountDto(PortfolioRebalanceController.CATEGORY_BONDS, 100.0);
		currentAmounts[1] = new AmountDto(PortfolioRebalanceController.CATEGORY_LARGE_CAP, 100.0);
		currentAmounts[2] = new AmountDto(PortfolioRebalanceController.CATEGORY_MID_CAP, 100.0);
		currentAmounts[3] = new AmountDto(PortfolioRebalanceController.CATEGORY_FOREIGN_CAP, 100.0);
		currentAmounts[4] = new AmountDto(PortfolioRebalanceController.CATEGORY_SMALL_CAP, 100.0);

		ParameterizedTypeReference<List<TransferenceDto>> typeRef = new ParameterizedTypeReference<List<TransferenceDto>>() {
		};

		ResponseEntity<List<TransferenceDto>> retVal = this.restTemplate.exchange("/portfolio/rebalance/" + riskPreference,
				HttpMethod.POST, new HttpEntity<>(currentAmounts, headers), typeRef);

		Assert.assertEquals(HttpStatus.OK, retVal.getStatusCode());
		List<TransferenceDto> dtos = retVal.getBody();
		Assert.assertEquals(2, dtos.size());
		
		TransferenceDto expectedDto1 = new TransferenceDto("Move 50.0 from FOREIGN_CAP to BONDS");
		TransferenceDto expectedDto2 = new TransferenceDto("Move 100.0 from SMALL_CAP to BONDS");
		
		Assert.assertEquals(expectedDto1.getRecommededTransfer(), dtos.get(0).getRecommededTransfer());
		Assert.assertEquals(expectedDto2.getRecommededTransfer(), dtos.get(1).getRecommededTransfer());
	}
	
	/**
	 * Helper method to compare 2 PortfolioDtos
	 *
	 * @param dto1
	 * @param dto2
	 */
	private void validate(PortfolioDto dto1, PortfolioDto dto2) {
		Assert.assertEquals(dto1.getRiskPreference(), dto2.getRiskPreference());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[0].getName(), dto2.getInvestCategoryDtos()[0].getName());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[0].getPercent(), dto2.getInvestCategoryDtos()[0].getPercent());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[1].getName(), dto2.getInvestCategoryDtos()[1].getName());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[1].getPercent(), dto2.getInvestCategoryDtos()[1].getPercent());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[2].getName(), dto2.getInvestCategoryDtos()[2].getName());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[2].getPercent(), dto2.getInvestCategoryDtos()[2].getPercent());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[3].getName(), dto2.getInvestCategoryDtos()[3].getName());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[3].getPercent(), dto2.getInvestCategoryDtos()[3].getPercent());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[4].getName(), dto2.getInvestCategoryDtos()[4].getName());
		Assert.assertEquals(dto1.getInvestCategoryDtos()[4].getPercent(), dto2.getInvestCategoryDtos()[4].getPercent());
	}
}
