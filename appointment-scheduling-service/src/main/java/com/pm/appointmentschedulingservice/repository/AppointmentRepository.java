package com.pm.appointmentschedulingservice.repository;

import com.pm.appointmentschedulingservice.domain.Appointment;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(String id);
    List<Appointment> findByPatientId(String patientId);
    List<Appointment> findByDoctorId(String doctorId);
    // Consider adding methods for finding by date range, status etc.
}
