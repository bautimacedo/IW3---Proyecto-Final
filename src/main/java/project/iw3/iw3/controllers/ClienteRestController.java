package project.iw3.iw3.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import project.iw3.iw3.model.Cliente;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IClienteBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;
import project.iw3.iw3.util.StandartResponse;


@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
	

    @Autowired
    private IClienteBusiness clienteBusiness;

    @Autowired
    private IStandartResponseBusiness standartResponseBusiness;

    // GET Clientes
    @Operation(summary = "Listar todos los clientes")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getClientes() {
        try {
            List<Cliente> clientes = clienteBusiness.list();
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (BusinessException e) {
            // return error message on failure
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST Cliente
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Operation(summary = "Crear un nuevo cliente")
public ResponseEntity<StandartResponse> addCliente(@RequestBody Cliente cliente) {
    try {
        Cliente nuevo = clienteBusiness.add(cliente);
        HttpHeaders headers = new HttpHeaders();
        headers.set("location", Constants.URL_CLIENTES + "/" + nuevo.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    } catch (FoundException e) {
        // Ya existe cliente con ese nombre
        return new ResponseEntity<>(
            standartResponseBusiness.build(HttpStatus.CONFLICT, e, e.getMessage()),
            HttpStatus.CONFLICT
        );

    } catch (BusinessException e) {
        // Cualquier otro error de lógica o persistencia
        return new ResponseEntity<>(
            standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}


     // GET /clientes/{id}
    @Operation(summary = "Obtiene un cliente por ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable long id) {
        try {
            return new ResponseEntity<>(clienteBusiness.load(id), HttpStatus.OK);
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


    // GET /clientes/by-name/{nombreEmpresa}
    @Operation(summary = "Obtiene un cliente por razón social")
    @GetMapping(value = "/by-name/{nombreEmpresa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable String nombreEmpresa) {
        try {
            return new ResponseEntity<>(clienteBusiness.load(nombreEmpresa), HttpStatus.OK);
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

    // PUT /clientes   (sin id y el id viene en el body)
    @Operation(summary = "Actualiza un cliente existente")
    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Cliente cliente) {
        try {
            clienteBusiness.update(cliente);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    standartResponseBusiness.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        } catch (FoundException e) {
            return new ResponseEntity<>(
                    standartResponseBusiness.build(HttpStatus.FOUND, e, e.getMessage()),
                    HttpStatus.FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                    standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}