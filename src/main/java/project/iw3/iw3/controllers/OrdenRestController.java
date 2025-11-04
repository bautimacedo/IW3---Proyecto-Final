package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.*;
import project.iw3.iw3.model.business.interfaces.IOrdenBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;



@RestController
@RequestMapping(Constants.URL_ORDEN)
public class OrdenRestController extends BaseRestController {
    
    @Autowired
    private IOrdenBusiness ordenBusiness;
    
    @Autowired
	private IStandartResponseBusiness standartResponseBusiness;
    
    //listar todas las ordenes
    @Operation(summary = "Listar todas las ordenes")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(){
    	try {
    		return new ResponseEntity<>(ordenBusiness.list(), HttpStatus.OK);
    	}catch(BusinessException e) {
    		return new ResponseEntity<>(standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    // PUNTO 1)
    @PostMapping(value = "/b2b",
    		consumes = MediaType.APPLICATION_JSON_VALUE, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> addExternal(@RequestBody String body){
    	try {
    		Orden response = ordenBusiness.addExternal(body);
    		
    		HttpHeaders headers = new HttpHeaders();
    		
        	headers.set(HttpHeaders.LOCATION, Constants.URL_ORDEN + "/" + response.getNumeroOrden());
        	
        	return new ResponseEntity<>(headers, HttpStatus.CREATED);
        	
    	} catch(BusinessException e) {
    		return new ResponseEntity<>(
    	            standartResponseBusiness.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
    	            HttpStatus.BAD_REQUEST
    	        );
    	} catch (FoundException e) { // duplicado por UNIQUE (p.ej. numeroOrden)
            return new ResponseEntity<>(
                    standartResponseBusiness.build(HttpStatus.CONFLICT, e, e.getMessage()),
                    HttpStatus.CONFLICT
                );	
    } catch (Exception e) { // fallback
        return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, "Error interno"),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
  
    }  
}
