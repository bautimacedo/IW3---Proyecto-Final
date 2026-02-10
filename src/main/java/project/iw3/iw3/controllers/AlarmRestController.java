package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.interfaces.IAlarmBusiness;

@Slf4j
@RestController
@RequestMapping(Constants.URL_ALARMS)
public class AlarmRestController {

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(alarmBusiness.list());
        } catch (BusinessException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }   
}
