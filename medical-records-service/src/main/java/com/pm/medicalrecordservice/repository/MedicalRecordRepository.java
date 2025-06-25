package com.pm.medicalrecordservice.repository;

import com.pm.medicalrecordservice.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
// Using Spring Data JPA as an example
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository  extends JpaRepository<MedicalRecord, String> {
    // Basic CRUD will be inherited if using JpaRepository
    // Add custom query methods if needed

    // Placeholder for saving a record
    MedicalRecord save(MedicalRecord medicalRecord);

    // Placeholder for finding a record by ID
    Optional<MedicalRecord> findById(String id);
}
