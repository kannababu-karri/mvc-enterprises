package com.mvc.enterprises.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mvc.enterprises.entities.MfgVsProduct;

@Repository
public interface MfgvsproductRepository extends JpaRepository<MfgVsProduct, Long> {
	Optional<MfgVsProduct> findByMfgvsproductId(Long mfgvsproductId);
	Optional<MfgVsProduct> findByManufacturerId(Long manufacturerId);												
}
