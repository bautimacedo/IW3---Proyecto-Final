package project.iw3.iw3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.ICisternaBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;
import project.iw3.iw3.util.StandartResponse;

@RestController
@RequestMapping("/api/v1/cisternas")
public class CisternaRestController {

    @Autowired
    private ICisternaBusiness cisternaBusiness;

    @Autowired
    private IStandartResponseBusiness standartResponseBusiness;

    // listar cisternas
    @Operation(summary = "Listar todas las cisternas")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            List<Cisterna> lista = cisternaBusiness.list();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // cisterna x id
    @Operation(summary = "Obtener una cisterna por ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable long id) {
        try {
            return new ResponseEntity<>(cisternaBusiness.load(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // cisternia x licencia
    @Operation(summary = "Obtener una cisterna por licencia")
    @GetMapping(value = "/by-licencia/{licencia}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable String licencia) {
        try {
            return new ResponseEntity<>(cisternaBusiness.load(licencia), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // crear cisterna
    @Operation(summary = "Registrar una nueva cisterna")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody Cisterna cisterna) {
        try {
            Cisterna nueva = cisternaBusiness.add(cisterna);
            HttpHeaders headers = new HttpHeaders();
            headers.set("location", "/api/v1/cisternas/" + nueva.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (FoundException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.CONFLICT, e, e.getMessage()),
                HttpStatus.CONFLICT
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // actualizar cisterna
    @Operation(summary = "Actualizar una cisterna existente")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Cisterna cisterna) {
        try {
            cisternaBusiness.update(cisterna);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (FoundException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.CONFLICT, e, e.getMessage()),
                HttpStatus.CONFLICT
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // eliminar cisterna
    @Operation(summary = "Eliminar una cisterna por ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            cisternaBusiness.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
