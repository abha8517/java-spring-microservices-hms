package com.pm.appointmentschedulingservice.service;

import com.pm.appointmentschedulingservice.domain.Appointment;
import com.pm.appointmentschedulingservice.repository.AppointmentRepository;
import com.pm.appointmentschedulingservice.grpc.AppointmentSchedulingServiceGrpc;
import com.pm.appointmentschedulingservice.grpc.ScheduleAppointmentRequest;
import com.pm.appointmentschedulingservice.grpc.GetAppointmentRequest;
import com.pm.appointmentschedulingservice.grpc.UpdateAppointmentRequest;
import com.pm.appointmentschedulingservice.grpc.CancelAppointmentRequest;
import com.pm.appointmentschedulingservice.grpc.ListAppointmentsForPatientRequest;
import com.pm.appointmentschedulingservice.grpc.ListAppointmentsForDoctorRequest;
import com.pm.appointmentschedulingservice.grpc.AppointmentResponse;
import com.pm.appointmentschedulingservice.grpc.ListAppointmentsResponse;
import com.pm.appointmentschedulingservice.grpc.Appointment; // Corrected to match proto definition

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService; // Updated import for GrpcService

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class AppointmentSchedulingServiceImpl extends AppointmentSchedulingServiceGrpc.AppointmentSchedulingServiceImplBase {

    private final AppointmentRepository appointmentRepository;

    public AppointmentSchedulingServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void scheduleAppointment(ScheduleAppointmentRequest request, StreamObserver<AppointmentResponse> responseObserver) {
        LocalDateTime appointmentTime = LocalDateTime.parse(request.getAppointmentTime()); // Assuming ISO 8601 string
        Appointment appointment = new Appointment(request.getPatientId(), request.getDoctorId(), appointmentTime, request.getNotes());
        appointment.setId(UUID.randomUUID().toString());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentResponse response = AppointmentResponse.newBuilder()
                .setAppointment(convertToGrpc(savedAppointment))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAppointment(GetAppointmentRequest request, StreamObserver<AppointmentResponse> responseObserver) {
        appointmentRepository.findById(request.getId())
            .map(this::convertToGrpc)
            .map(grpcAppointment -> AppointmentResponse.newBuilder().setAppointment(grpcAppointment).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Appointment not found with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    @Override
    public void updateAppointment(UpdateAppointmentRequest request, StreamObserver<AppointmentResponse> responseObserver) {
        appointmentRepository.findById(request.getId())
            .map(existingAppointment -> {
                if (!request.getAppointmentTime().isEmpty()) {
                    existingAppointment.setAppointmentTime(LocalDateTime.parse(request.getAppointmentTime()));
                }
                if (!request.getStatus().isEmpty()) {
                    existingAppointment.setStatus(request.getStatus());
                }
                if (request.hasNotes()) { // Check if notes field is set in proto
                    existingAppointment.setNotes(request.getNotes());
                }
                existingAppointment.setUpdatedAt(LocalDateTime.now());
                return appointmentRepository.save(existingAppointment);
            })
            .map(this::convertToGrpc)
            .map(grpcAppointment -> AppointmentResponse.newBuilder().setAppointment(grpcAppointment).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Appointment not found for update with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    @Override
    public void cancelAppointment(CancelAppointmentRequest request, StreamObserver<AppointmentResponse> responseObserver) {
        appointmentRepository.findById(request.getId())
            .map(existingAppointment -> {
                existingAppointment.setStatus("CANCELLED");
                existingAppointment.setUpdatedAt(LocalDateTime.now());
                return appointmentRepository.save(existingAppointment);
            })
            .map(this::convertToGrpc)
            .map(grpcAppointment -> AppointmentResponse.newBuilder().setAppointment(grpcAppointment).build())
            .ifPresentOrElse(response -> {
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Appointment not found for cancellation with ID: " + request.getId())
                    .asRuntimeException());
            });
    }

    @Override
    public void listAppointmentsForPatient(ListAppointmentsForPatientRequest request, StreamObserver<ListAppointmentsResponse> responseObserver) {
        ListAppointmentsResponse response = ListAppointmentsResponse.newBuilder()
                .addAllAppointments(appointmentRepository.findByPatientId(request.getPatientId()).stream()
                        .map(this::convertToGrpc)
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listAppointmentsForDoctor(ListAppointmentsForDoctorRequest request, StreamObserver<ListAppointmentsResponse> responseObserver) {
        ListAppointmentsResponse response = ListAppointmentsResponse.newBuilder()
                .addAllAppointments(appointmentRepository.findByDoctorId(request.getDoctorId()).stream()
                        .map(this::convertToGrpc)
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private com.pm.appointmentschedulingservice.grpc.Appointment convertToGrpc(com.pm.appointmentschedulingservice.domain.Appointment appointment) {
        Instant instant = appointment.getAppointmentTime().toInstant(ZoneOffset.UTC);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();

        return com.pm.appointmentschedulingservice.grpc.Appointment.newBuilder()
                .setId(appointment.getId())
                .setPatientId(appointment.getPatientId())
                .setDoctorId(appointment.getDoctorId())
                .setAppointmentTime(appointment.getAppointmentTime().toString()) // Sending as ISO string
                .setStatus(appointment.getStatus())
                .setNotes(appointment.getNotes() == null ? "" : appointment.getNotes())
                .setCreatedAt(appointment.getCreatedAt().toString())
                .setUpdatedAt(appointment.getUpdatedAt().toString())
                .build();
    }
}
