package com.pm.doctormanagementservice.repository;

import com.pm.doctormanagementservice.domain.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(String id);
    List<Doctor> findAll(); // For ListDoctors RPC
}
