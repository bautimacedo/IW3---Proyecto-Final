package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.interfaces.IProductoBusiness;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.util.IStandartResponseBusiness;

// OJO: esto lo tenés que tener igual que el profe.
// Por ejemplo:
// public class Constants {
//     public static final String URL_PRODUCTOS = "/api/productos";
// }

@RestController
@RequestMapping(Constants.URL_PRODUCTS)
public class ProductoRestController {

    @Autowired
    private IProductoBusiness productoBusiness;

    @Autowired
    private IStandartResponseBusiness responseBuilder;

 
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

 
    @PostMapping(value = "")
    public ResponseEntity<?> add(@RequestBody Producto producto) {
        try {
            Producto creado = productoBusiness.add(producto);

            // igual que el profe: devolver Location en el header y 201, sin body
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
            return new ResponseEntity<>(productoBusiness.load(id), HttpStatus.OK);
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

    @GetMapping(value = "/by-name/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByNombre(@PathVariable("nombre") String nombre) {
        try {
            return new ResponseEntity<>(productoBusiness.load(nombre), HttpStatus.OK);
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
    public ResponseEntity<?> update(@RequestBody Producto producto) {
        try {
            productoBusiness.update(producto);
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
            productoBusiness.delete(id);
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
