package com.mvc.enterprises.form;

import java.util.List;

import com.mvc.enterprises.entities.PageResponseDto;
import com.mvc.enterprises.entities.Product;

public class ProductForm {
	
	private List<Product> resultProducts;
    private boolean showDetails;
	private Product product;
	private Long ProductId;
	private PageResponseDto<?> pageResponseDto;
	
	public List<Product> getResultProducts() {
		return resultProducts;
	}
	public void setResultProducts(List<Product> resultProducts) {
		this.resultProducts = resultProducts;
	}
	public boolean isShowDetails() {
		return showDetails;
	}
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Long getProductId() {
		return ProductId;
	}
	public void setProductId(Long productId) {
		ProductId = productId;
	}
	public PageResponseDto<?> getPageResponseDto() {
		return pageResponseDto;
	}
	public void setPageResponseDto(PageResponseDto<?> pageResponseDto) {
		this.pageResponseDto = pageResponseDto;
	}
}
