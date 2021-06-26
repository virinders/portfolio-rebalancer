package com.finance.portfolio.rebalancer.dto;

public class TransferenceDto extends AbstractDto {
	private String recommededTransfer; // capture the full transference message
	
	public TransferenceDto() {	
	}
	
	public TransferenceDto(String recommededTransfer) {
		this.recommededTransfer = recommededTransfer;
	}

	public String getRecommededTransfer() {
		return recommededTransfer;
	}

	public void setRecommededTransfer(String recommededTransfer) {
		this.recommededTransfer = recommededTransfer;
	}
}
