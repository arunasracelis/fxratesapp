package com.fxratesapp.repository;

import com.fxratesapp.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RateRepository extends JpaRepository<Rate, Long> {
    @Query("SELECT r FROM Rate r WHERE r.currency = :currency and r.date = :date")
    List<Rate> findByCurrency(@Param("currency") String currency, @Param("date") String date);
}
