package com.palmer.wfhbillingapi.repository;

import com.palmer.wfhbillingapi.model.desktop.ActivationCode;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ActivationCodeRepository extends ListCrudRepository<ActivationCode, Integer> {
    Optional<ActivationCode> findByCode(String code);
}
