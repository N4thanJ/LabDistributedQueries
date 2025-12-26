package be.ucll.da.apigateway.web;

import be.ucll.da.apigateway.api.HospitalApiDelegate;

import be.ucll.da.apigateway.api.HospitalApiDelegate;
import be.ucll.da.apigateway.api.model.*;
import be.ucll.da.apigateway.client.appointment.api.AppointmentApi;
import be.ucll.da.apigateway.client.doctor.api.DoctorApi;
import be.ucll.da.apigateway.client.doctor.model.ApiDoctor;
import be.ucll.da.apigateway.client.patient.api.PatientApi;
import be.ucll.da.apigateway.client.patient.model.ApiPatient;
import be.ucll.da.apigateway.cqrs.Appointment;
import be.ucll.da.apigateway.cqrs.AppointmentRepository;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Component
public class HospitalController implements HospitalApiDelegate {
    private final AppointmentApi appointmentApi;
    private final PatientApi patientApi;
    private final DoctorApi doctorApi;
    private final EurekaClient discoveryClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public HospitalController(AppointmentApi appointmentApi, PatientApi patientApi, DoctorApi doctorApi, EurekaClient discoveryClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.appointmentApi = appointmentApi;
        this.patientApi = patientApi;
        this.doctorApi = doctorApi;
        this.discoveryClient = discoveryClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public ResponseEntity<Void> apiV1AppointmentConfirmationPost(ApiAppointmentConfirmation apiAppointmentConfirmation) {
        appointmentApi.getApiClient().setBasePath(discoveryClient.getNextServerFromEureka("appointment-service", false).getHomePageUrl());

        var confirmation = new be.ucll.da.apigateway.client.appointment.model.ApiAppointmentConfirmation();
        confirmation.appointmentRequestNumber(apiAppointmentConfirmation.getAppointmentRequestNumber());
        confirmation.setAcceptProposedAppointment(apiAppointmentConfirmation.getAcceptProposedAppointment());

        return circuitBreakerFactory.create("appointmentApi")
                .run(() -> appointmentApi.apiV1AppointmentConfirmationPostWithHttpInfo(confirmation));
    }

    @Override
    public ResponseEntity<ApiAppointmentRequestResponse> apiV1AppointmentRequestPost(ApiAppointmentRequest apiAppointmentRequest) {
        throw new RuntimeException("Implement me!!");
    }

    @Override
    public ResponseEntity<ApiAppointmentOverview> apiV1AppointmentDayGet(String dayString, Boolean useCqrs) {
        LocalDate day = LocalDate.parse(dayString, DateTimeFormatter.ISO_DATE);

        if (useCqrs) {
            return getUsingCqrs(day);
        } else {
            return getUsingApiComposition(day);
        }
    }

    // --- Code For Composition ---

    private ResponseEntity<ApiAppointmentOverview> getUsingApiComposition(LocalDate day) {
        throw new RuntimeException("Implement me!!");
    }

    // --- Code For CQRS

    private ResponseEntity<ApiAppointmentOverview> getUsingCqrs(LocalDate day) {
        throw new RuntimeException("Implement me!!");
    }
}
