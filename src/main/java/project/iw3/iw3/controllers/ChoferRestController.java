package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IChoferBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_CHOFERES)
public class ChoferRestController {
    @Autowired
    private IChoferBusiness choferBusiness;

    @Autowired
    private IStandartResponseBusiness responseBuilder;

 
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            return new ResponseEntity<>(choferBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

 
    @PostMapping(value = "")
    public ResponseEntity<?> add(@RequestBody Chofer chofer) {
        try {
            Chofer creado = choferBusiness.add(chofer);

            HttpHeaders headers = new HttpHeaders();
            headers.set("location", Constants.URL_PRODUCTS + "/" + creado.getId());

            return new ResponseEntity<>(headers, HttpStatus.CREATED);

        } catch (FoundException e) {
            // algo ya existe (id o nombre duplicado)
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.FOUND, e, e.getMessage()),
                HttpStatus.FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

   
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(choferBusiness.load(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping(value = "/by-dni/{dni}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByNombre(@PathVariable("dni") String dni) {
        try {
            return new ResponseEntity<>(choferBusiness.load(dni), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

 
    @PutMapping(value = "")
    public ResponseEntity<?> update(@RequestBody Chofer chofer) {
        try {
            choferBusiness.update(chofer);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NotFoundException e) {
            // no existe el id que me mandaste
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );

        } catch (FoundException e) {
            // estás tratando de cambiarle el nombre a uno que ya usa otro producto
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.FOUND, e, e.getMessage()),
                HttpStatus.FOUND
            );

        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        try {
            choferBusiness.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
