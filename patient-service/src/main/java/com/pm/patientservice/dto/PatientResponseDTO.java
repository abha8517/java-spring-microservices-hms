package com.pm.patientservice.dto;

import com.pm.appointmentschedulingservice.grpc.Appointment;
import com.pm.medicalrecordservice.grpc.MedicalRecord;
//import com.pm.medicalrecordservice.grpc.MedicalRecordGrpc;
//import com.pm.appointmentschedulingservice.grpc.AppointmentGrpc;
import java.util.List;

public class PatientResponseDTO {
  private String id;
  private String name;
  private String email;
  private String address;
  private String dateOfBirth;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

    private List<MedicalRecord> medicalRecords;
    private List<Appointment> appointments;

    // Getters and Setters for new fields
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
}
