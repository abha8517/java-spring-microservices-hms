package com.pm.medicalrecordservice.domain;

import java.time.LocalDateTime;

// Using Spring Data JPA annotations as an example
// import javax.persistence.Entity;
// import javax.persistence.Id;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;

// @Entity
public class MedicalRecord {

    // @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String patientId;
    private String recordDetails; // Consider a more structured type if needed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicalRecord() {
    }

    public MedicalRecord(String patientId, String recordDetails) {
        this.patientId = patientId;
        this.recordDetails = recordDetails;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRecordDetails() {
        return recordDetails;
    }

    public void setRecordDetails(String recordDetails) {
        this.recordDetails = recordDetails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
