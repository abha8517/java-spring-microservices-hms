package com.pm.doctormanagementservice.service;

import com.pm.doctormanagementservice.domain.Doctor;
import com.pm.doctormanagementservice.repository.DoctorRepository;
import com.pm.doctormanagementservice.grpc.DoctorManagementServiceGrpc;
import com.pm.doctormanagementservice.grpc.RegisterDoctorRequest;
import com.pm.doctormanagementservice.grpc.GetDoctorRequest;
import com.pm.doctormanagementservice.grpc.UpdateDoctorRequest;
import com.pm.doctormanagementservice.grpc.ListDoctorsRequest;
import com.pm.doctormanagementservice.grpc.DoctorResponse;
import com.pm.doctormanagementservice.grpc.ListDoctorsResponse;
import com.pm.doctormanagementservice.grpc.Doctor; // Corrected to match proto definition

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService; // Updated import for GrpcService
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class DoctorManagementServiceImpl extends DoctorManagementServiceGrpc.DoctorManagementServiceImplBase {

    private final DoctorRepository doctorRepository;

    public DoctorManagementServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void registerDoctor(RegisterDoctorRequest request, StreamObserver<DoctorResponse> responseObserver) {
        Doctor doctor = new Doctor(request.getName(), request.getSpecialization(), request.getContactInfo());
        doctor.setId(UUID.randomUUID().toString());
        Doctor savedDoctor = doctorRepository.save(doctor);
        DoctorResponse response = DoctorResponse.newBuilder()
                .setDoctor(convertToGrpc(savedDoctor))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDoctor(GetDoctorRequest request, StreamObserver<DoctorResponse> responseObserver) {
        doctorRepository.findById(request.getId())
            .map(this::convertToGrpc)
            .map(doctorGrpc -> DoctorResponse.newBuilder().setDoctor(doctorGrpc).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Doctor not found with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    @Override
    public void updateDoctor(UpdateDoctorRequest request, StreamObserver<DoctorResponse> responseObserver) {
        doctorRepository.findById(request.getId())
            .map(existingDoctor -> {
                existingDoctor.setName(request.getName());
                existingDoctor.setSpecialization(request.getSpecialization());
                existingDoctor.setContactInfo(request.getContactInfo());
                existingDoctor.setUpdatedAt(LocalDateTime.now());
                return doctorRepository.save(existingDoctor);
            })
            .map(this::convertToGrpc)
            .map(doctorGrpc -> DoctorResponse.newBuilder().setDoctor(doctorGrpc).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Doctor not found for update with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    @Override
    public void listDoctors(ListDoctorsRequest request, StreamObserver<ListDoctorsResponse> responseObserver) {
        ListDoctorsResponse response = ListDoctorsResponse.newBuilder()
                .addAllDoctors(doctorRepository.findAll().stream()
                        .map(this::convertToGrpc)
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private com.pm.doctormanagementservice.grpc.Doctor convertToGrpc(com.pm.doctormanagementservice.domain.Doctor doctor) {
        return com.pm.doctormanagementservice.grpc.Doctor.newBuilder()
                .setId(doctor.getId())
                .setName(doctor.getName())
                .setSpecialization(doctor.getSpecialization())
                .setContactInfo(doctor.getContactInfo())
                .setCreatedAt(doctor.getCreatedAt().toString())
                .setUpdatedAt(doctor.getUpdatedAt().toString())
                .build();
    }
}
