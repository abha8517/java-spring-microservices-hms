package com.pm.patientservice.grpc;

import com.pm.medicalrecordservice.grpc.MedicalRecord;
import com.pm.medicalrecordservice.grpc.MedicalRecordServiceGrpc;
import com.pm.medicalrecordservice.grpc.GetMedicalRecordRequest; // This will be generated after build
import com.pm.medicalrecordservice.grpc.MedicalRecordResponse; // This will be generated after build
//import com.pm.medicalrecordservice.grpc.MedicalRecordGrpc; // Expected name

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class MedicalRecordServiceGrpcClient {

    @GrpcClient("medical-records-service") // Matches the service name in api-gateway URI (without http://) or a configured gRPC client name
    private MedicalRecordServiceGrpc.MedicalRecordServiceBlockingStub medicalRecordServiceStub;

    // Example: Get medical records for a patient.
    // The proto currently has GetMedicalRecord(id), not GetByPatientId.
    // This client method will need to be adjusted based on actual service capabilities.
    // For now, let's assume we'd iterate or the service would provide a better method.
    // This is a placeholder, as the current medical_records_service.proto only has GetMedicalRecord by record ID.
    // We would ideally add a rpc GetMedicalRecordsByPatientId(GetMedicalRecordsByPatientIdRequest) returns (ListMedicalRecordsResponse) to medical_records_service.proto
    public List<MedicalRecord> getMedicalRecordsForPatient(String patientId) {
        // Placeholder: This won't work as is with current proto.
        // GetMedicalRecordRequest request = GetMedicalRecordRequest.newBuilder().setId(patientId /* This should be recordId not patientId */).build();
        // MedicalRecordResponse response = medicalRecordServiceStub.getMedicalRecord(request);
        // return Collections.singletonList(response.getMedicalRecord());
        System.out.println("WARN: getMedicalRecordsForPatient is a placeholder and needs proper proto support.");
        return Collections.emptyList(); // Return empty list until proto is updated
    }
}
