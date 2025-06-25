package com.pm.medicalrecordservice.service;

import com.pm.medicalrecordservice.domain.MedicalRecord;
import com.pm.medicalrecordservice.repository.MedicalRecordRepository;
import com.pm.medicalrecordservice.grpc.MedicalRecordServiceGrpc;
import com.pm.medicalrecordservice.grpc.CreateMedicalRecordRequest;
import com.pm.medicalrecordservice.grpc.GetMedicalRecordRequest;
import com.pm.medicalrecordservice.grpc.UpdateMedicalRecordRequest;
import com.pm.medicalrecordservice.grpc.MedicalRecordResponse;
//import com.pm.medicalrecordservice.grpc.MedicalRecord; // Corrected to match proto definition

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.lognet.springboot.grpc.GRpcService; // Updated import for GrpcService
import java.time.LocalDateTime;
import java.util.UUID;

// Assuming Spring beans for injection, e.g. @Service
@GrpcService
public class MedicalRecordServiceImpl extends MedicalRecordServiceGrpc.MedicalRecordServiceImplBase {

    private final MedicalRecordRepository medicalRecordRepository;

    // @Autowired (if using Spring)
    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public void createMedicalRecord(CreateMedicalRecordRequest request, StreamObserver<MedicalRecordResponse> responseObserver) {
        MedicalRecord medicalRecord = new MedicalRecord(request.getPatientId(), request.getRecordDetails());
        medicalRecord.setId(UUID.randomUUID().toString()); // Generate ID
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        MedicalRecordResponse response = MedicalRecordResponse.newBuilder()
                .setMedicalRecord(convertToGrpc(savedRecord))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMedicalRecord(GetMedicalRecordRequest request, StreamObserver<MedicalRecordResponse> responseObserver) {
        medicalRecordRepository.findById(request.getId())
            .map(this::convertToGrpc)
            .map(recordGrpc -> MedicalRecordResponse.newBuilder().setMedicalRecord(recordGrpc).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                // Handle not found case, e.g., by sending an error
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Medical record not found with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    @Override
    public void updateMedicalRecord(UpdateMedicalRecordRequest request, StreamObserver<MedicalRecordResponse> responseObserver) {
        medicalRecordRepository.findById(request.getId())
            .map(existingRecord -> {
                existingRecord.setRecordDetails(request.getRecordDetails());
                existingRecord.setUpdatedAt(LocalDateTime.now());
                return medicalRecordRepository.save(existingRecord);
            })
            .map(this::convertToGrpc)
            .map(recordGrpc -> MedicalRecordResponse.newBuilder().setMedicalRecord(recordGrpc).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Medical record not found for update with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    private com.pm.medicalrecordservice.grpc.MedicalRecord convertToGrpc(com.pm.medicalrecordservice.domain.MedicalRecord medicalRecord) {
        return com.pm.medicalrecordservice.grpc.MedicalRecord.newBuilder()
                .setId(medicalRecord.getId())
                .setPatientId(medicalRecord.getPatientId())
                .setRecordDetails(medicalRecord.getRecordDetails())
                .setCreatedAt(medicalRecord.getCreatedAt().toString())
                .setUpdatedAt(medicalRecord.getUpdatedAt().toString())
                .build();
    }
}
