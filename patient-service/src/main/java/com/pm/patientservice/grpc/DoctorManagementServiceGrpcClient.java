package com.pm.patientservice.grpc;

import com.pm.doctormanagementservice.grpc.DoctorManagementServiceGrpc;
import com.pm.doctormanagementservice.grpc.GetDoctorRequest; // Generated
import com.pm.doctormanagementservice.grpc.DoctorResponse; // Generated
import com.pm.doctormanagementservice.grpc.Doctor; // Corrected name

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class DoctorManagementServiceGrpcClient {

    @GrpcClient("doctor-management-service")
    private DoctorManagementServiceGrpc.DoctorManagementServiceBlockingStub doctorManagementServiceStub;

    public Doctor getDoctorById(String doctorId) {
        GetDoctorRequest request = GetDoctorRequest.newBuilder().setId(doctorId).build();
        try {
            DoctorResponse response = doctorManagementServiceStub.getDoctor(request);
            return response.getDoctor();
        } catch (Exception e) {
            // Log error, handle appropriately
            System.err.println("Error fetching doctor " + doctorId + ": " + e.getMessage());
            return null;
        }
    }
}
