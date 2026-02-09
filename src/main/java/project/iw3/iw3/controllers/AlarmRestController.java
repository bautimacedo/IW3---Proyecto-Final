package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.business.interfaces.IAlarmBusiness;

@Slf4j
@RestController
@RequestMapping(Constants.URL_ALARMS)
public class AlarmRestController {

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(alarmBusiness.list());
        } catch (Exception e) {
            log.error("Error listando alarmas", e);
            return ResponseEntity.internalServerError().body("Error interno");
        }
    }
}
