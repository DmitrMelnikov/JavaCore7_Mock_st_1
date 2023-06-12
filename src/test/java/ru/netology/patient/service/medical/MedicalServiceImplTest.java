package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.stubbing.OngoingStubbing;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MedicalServiceImplTest {

    @Test
    void test_MedicalServiceImpl() {

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        PatientInfo patientInfo = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26), new HealthInfo(new BigDecimal("39.65"), new BloodPressure(120, 80)));

        Mockito.when(patientInfoRepository.getById(any())).thenReturn(patientInfo);
        Mockito.when(patientInfoRepository.add(any())).thenReturn("");

        BloodPressure currentPressure = new BloodPressure(60, 120);
        medicalService.checkBloodPressure(patientInfo.toString(), currentPressure);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        String sendTest = String.format("Warning, patient with id: %s, need help", patientInfo.getId());
        Assertions.assertEquals(argumentCaptor.getValue(),sendTest);

        BigDecimal currentTemperature = new BigDecimal("37.9");
        medicalService.checkTemperature(patientInfo.toString(), currentTemperature);
        Mockito.verify(alertService,Mockito.times(2)).send(argumentCaptor.capture());
        Assertions.assertEquals(argumentCaptor.getValue(),sendTest);

        Mockito.verify(alertService, Mockito.times(2)).send(any());

    }
}
