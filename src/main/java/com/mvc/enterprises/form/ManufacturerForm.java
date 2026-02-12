package com.mvc.enterprises.form;

import java.util.List;

import com.mvc.enterprises.entities.Manufacturer;
import com.mvc.enterprises.entities.PageResponseDto;

public class ManufacturerForm {
	
	private List<Manufacturer> resultManufacturers;
    private boolean showDetails;
	private Manufacturer manufacturer;
	private Long manufacturerId;
	private PageResponseDto<?> pageResponseDto;
	
	public List<Manufacturer> getResultManufacturers() {
		return resultManufacturers;
	}
	public void setResultManufacturers(List<Manufacturer> resultManufacturers) {
		this.resultManufacturers = resultManufacturers;
	}
	public boolean isShowDetails() {
		return showDetails;
	}
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
	public Long getManufacturerId() {
		return manufacturerId;
	}
	public void setManufacturerId(Long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	public PageResponseDto<?> getPageResponseDto() {
		return pageResponseDto;
	}
	public void setPageResponseDto(PageResponseDto<?> pageResponseDto) {
		this.pageResponseDto = pageResponseDto;
	}
}
