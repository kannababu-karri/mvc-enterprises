package com.mvc.enterprises.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mvc.enterprises.entities.Manufacturer;
import com.mvc.enterprises.entities.OrderQty;
import com.mvc.enterprises.entities.Product;
import com.mvc.enterprises.entities.User;

@Repository
public interface OrderQtyRepository extends JpaRepository<OrderQty, Long>{
	Optional<OrderQty> findByOrderId(Long id);
	
	void deleteByOrderId(Long id);
	
	OrderQty findByManufacturerAndProductAndUser(Manufacturer manufacturer, Product product, User user);
	
	// Find by userid
    List<OrderQty> findByUser_UserId(Long userId);

    // Find by mfg id
    List<OrderQty> findByManufacturer_ManufacturerIdAndUser_UserId(Long manufacturerId, Long userId);
    
    // Find by product id
    List<OrderQty> findByProduct_ProductIdAndUser_UserId(Long productId, Long userId);

    // Find by user, mfg and product id
    List<OrderQty> findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(Long manufacturerId, Long productId, Long userId);
}
