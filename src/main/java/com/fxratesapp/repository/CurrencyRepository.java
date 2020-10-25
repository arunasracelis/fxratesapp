package com.fxratesapp.repository;

import com.fxratesapp.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    @Query("SELECT c FROM Currency c WHERE c.code = :code")
    List<Currency> findByCode(@Param("code") String currencyCode);
}