package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import project.iw3.iw3.model.DatosCargaDTO;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.*;
import project.iw3.iw3.model.business.interfaces.IOrdenBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;


@Slf4j
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
    
    
    // PUNTO 2)
    @PostMapping(value = "/pesaje-inicial", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> registerInitialWeighing(
            @RequestHeader("Patente") String patente,
            @RequestHeader("Tara") float tara) {
    	
    	try {
    		Orden orden = ordenBusiness.registrarPesoInicial(patente, tara);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Order-Id", String.valueOf(orden.getNumeroOrden()));
            return new ResponseEntity<>(orden.getPassword().toString(), responseHeaders, HttpStatus.OK);
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
    	}
    	
        
    }
    
    
    //PUNTO 4)
    @PostMapping("/cerrar-carga")
    public ResponseEntity<?> closeOrder(@RequestHeader("NumeroOrden") Integer numeroOrden) {
    	
    	
    	try{
    		Orden orden = ordenBusiness.cerrarOrden(numeroOrden);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Order-Number", String.valueOf(orden.getNumeroOrden()));
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    	}catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
    	}
        
    }

    // PUNTO 3) - Recibir datos de carga desde JSON
	@PostMapping(value = "/datos-carga", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveLoadData(@RequestBody DatosCargaDTO datos) {
	    try {
	        Orden orden = ordenBusiness.recibirDatosCarga(datos);
	        HttpHeaders responseHeaders = new HttpHeaders();
	        responseHeaders.set("Order-Number", String.valueOf(orden.getNumeroOrden()));
	        return new ResponseEntity<>("Datos recibidos correctamente", responseHeaders, HttpStatus.OK);
	    } catch (BusinessException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        log.error("Erro interno al recibir datos de carga", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
	    }
	}


	


}
