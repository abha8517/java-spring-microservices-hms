package com.pm.patientservice.grpc;

import com.pm.appointmentschedulingservice.grpc.AppointmentSchedulingServiceGrpc;
import com.pm.appointmentschedulingservice.grpc.ListAppointmentsForPatientRequest; // Generated
import com.pm.appointmentschedulingservice.grpc.ListAppointmentsResponse; // Generated
import com.pm.appointmentschedulingservice.grpc.Appointment; // Corrected name

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentSchedulingServiceGrpcClient {

    @GrpcClient("appointment-scheduling-service")
    private AppointmentSchedulingServiceGrpc.AppointmentSchedulingServiceBlockingStub appointmentSchedulingServiceStub;

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        ListAppointmentsForPatientRequest request = ListAppointmentsForPatientRequest.newBuilder().setPatientId(patientId).build();
        try {
            ListAppointmentsResponse response = appointmentSchedulingServiceStub.listAppointmentsForPatient(request);
            return response.getAppointmentsList();
        } catch (Exception e) {
            // Log error, handle appropriately
            System.err.println("Error fetching appointments for patient " + patientId + ": " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
}
