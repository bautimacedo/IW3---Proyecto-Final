package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.auth.User;
import project.iw3.iw3.model.DatosCargaDTO;
import project.iw3.iw3.model.Orden;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.IOrdenBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;
import project.iw3.iw3.auth.User;


@Slf4j
@RestController
@RequestMapping(Constants.URL_ORDEN)
public class OrdenRestController extends BaseRestController {
    
    @Autowired
    private IOrdenBusiness ordenBusiness;
    
    @Autowired
	private IStandartResponseBusiness standartResponseBusiness;
    
   @Operation(
    summary = "Listar todas las ordenes",
    description = "Devuelve lista completa de ordenes registradas en el sistema. Cada orden contiene la informacion del cliente, producto, camion, chofer y los estados del proceso de carga."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lista de ordenes obtenida correctamente",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Orden.class)),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de lista de ordenes",
                    value = """
                    [
                        {
                            "id": 47,
                            "estadoOrden": "PENDIENTE_PESAJE_INICIAL",
                            "password": null,
                            "numeroOrden": 400,
                            "producto": {
                                "id": 1,
                                "nombre": "Butano",
                                "descripcion": "flechaverde@gmail.com"
                            },
                            "camion": {
                                "id": 14,
                                "patente": "AH-123-AL",
                                "descripcion": "volvo",
                                "cisterna": [
                                    {
                                        "id": 5,
                                        "capacidadLitros": 35000,
                                        "licencia": "ABC-123"
                                    },
                                    {
                                        "id": 4,
                                        "capacidadLitros": 35000,
                                        "licencia": "DEF-456"
                                    },
                                    {
                                        "id": 6,
                                        "capacidadLitros": 35000,
                                        "licencia": "GHI-789"
                                    }
                                ]
                            },
                            "chofer": {
                                "id": 3,
                                "dni": "1234567",
                                "nombre": "juan",
                                "apellido": "gonzales"
                            },
                            "cliente": {
                                "id": 5,
                                "nombreEmpresa": "YPF",
                                "email": "mail@mail"
                            },
                            "preset": 18270.0,
                            "tara": null,
                            "pesoFinal": 0.0,
                            "fechaRecepcionInicial": "2025-11-08T19:45:02.905-03:00",
                            "fechaPesajeTara": null,
                            "fechaInicioCarga": null,
                            "ultimaFechaInformacion": null,
                            "fechaCierreCarga": null,
                            "fechaCierreDeOrden": null,
                            "promedioDensidad": null,
                            "promedioTemperatura": null,
                            "promedioCaudal": null,
                            "ultimaMasaAcumulada": null,
                            "ultimaDensidad": null,
                            "ultimaTemperatura": null,
                            "ultimaFlowRate": null
                        }
                    ]
                    """
                )
            }
        )
    ),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> list() {
    try {
        return new ResponseEntity<>(ordenBusiness.list(), HttpStatus.OK);
    } catch (BusinessException e) {
        return new ResponseEntity<>(
            standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
    
   @io.swagger.v3.oas.annotations.parameters.RequestBody(
		    description = "Datos completos de la orden, incluyendo información anidada del camión, cisterna, chofer, cliente y producto. "
		        + "Utiliza un deserializador flexible que acepta diferentes claves JSON para los mismos campos.",
		    required = true,
		    content = @Content(
		        mediaType = "application/json",
		        examples = {
		            @ExampleObject(
		                name = "Creación de Orden Externa",
		                value = """
		                {
		                    "order_number": 777,
		                    "truck": {
		                        "id": 1000,
		                        "licence_plate": "AB-OS",
		                        "description": "Renault",
		                        "tanks": [
		                            { "id": 1, "capacity": 35000, "licence_plate": "GMI-1234" },
		                            { "capacity": 15000, "licence_plate": "GMI-5678" }
		                        ]
		                    },
		                    "driver": {
		                        "id": 1,
		                        "name": "juan",
		                        "last_name": "gonzales",
		                        "document": "1234567"
		                    },
		                    "customer": {
		                        "id": 21,
		                        "business_name": "YPF",
		                        "email": "mail@mail"
		                    },
		                    "product": {
		                        "id": 1,
		                        "product": "Butano"
		                    },
		                    "preset": 18270.0
		                }
		                """
		            )
		        }
		    )
		)
		@Operation(
		    summary = "Crear una nueva orden desde un sistema externo ",
		    description = "Este endpoint recibe la información completa de una orden de carga y la registra en el sistema. "
		        + "Durante el proceso, se verifican las reglas de negocio, como la capacidad. "
		        + "La orden se crea en estado **PENDIENTE_PESAJE_INICIAL**."
		)
		@ApiResponses({
		    @ApiResponse(
		        responseCode = "201",
		        description = "Orden creada exitosamente. El ID de la nueva orden se devuelve en el encabezado `Location`.",
		        headers = @io.swagger.v3.oas.annotations.headers.Header(
		            name = HttpHeaders.LOCATION,
		            description = "URL completa para acceder al recurso de la orden recién creada.",
		            schema = @Schema(type = "string", example = "/api/v1/orden/777")
		        )
		    ),
		    @ApiResponse(
		        responseCode = "400",
		        description = "Error de negocio (`BusinessException`). Por ejemplo: El 'preset' excede la capacidad total de las cisternas, patente de camión/cisterna faltante, o error de validación de datos.",
		        content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Respuesta de Error", value = "{ \"status\": 400, \"error\": \"BusinessException\", \"message\": \"El preset excede la capacidad total.\" }"))
		    ),
		    @ApiResponse(
		        responseCode = "409",
		        description = "Conflicto (`FoundException`). El número de orden enviado ya se encuentra registrado en el sistema.",
		        content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Respuesta de Conflicto", value = "{ \"status\": 409, \"error\": \"FoundException\", \"message\": \"El número de orden 777 ya existe.\" }"))
		    ),
		    @ApiResponse(
		        responseCode = "500",
		        description = "Error interno del servidor (`Exception`).",
		        content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Respuesta de Error Interno", value = "{ \"status\": 500, \"error\": \"Exception\", \"message\": \"Error interno\" }"))
		    )
		})
@PostMapping(
    value = "/b2b",
    consumes = MediaType.APPLICATION_JSON_VALUE
)
   
// PUNTO 1
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SAP')")
public ResponseEntity<?> addExternal(@RequestBody String body) {
    try {
        Orden response = ordenBusiness.addExternal(body);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, Constants.URL_ORDEN + "/by-number/" + response.getNumeroOrden());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    } catch (BusinessException e) {
        return new ResponseEntity<>(
            standartResponseBusiness.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    } catch (FoundException e) {
        return new ResponseEntity<>(
            standartResponseBusiness.build(HttpStatus.CONFLICT, e, e.getMessage()),
            HttpStatus.CONFLICT
        );
    } catch (Exception e) {
        return new ResponseEntity<>(
            standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, "Error interno"),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

    
    
@Operation(
    summary = "Registrar el pesaje inicial de una orden",
    description = "Este endpoint recibe la patente del camion y el peso tara (peso del camion vacio). "
        + "Si la patente corresponde a un camion con una orden pendiente de pesaje, se registra el peso inicial "
        + "y se genera una contraseña asociada a la orden. Devuelve la contraseña en texto plano y el numero de orden en el header."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Pesaje inicial registrado correctamente. Devuelve la contraseña de la orden en el cuerpo y el numero de orden en el header.",
        content = @Content(
            mediaType = "text/plain",
            examples = {
                @ExampleObject(
                    name = "Respuesta exitosa",
                    value = "12345"
                )
            }
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Error en los datos enviados o camion no encontrado"
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor"
    )
})
@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Datos del pesaje inicial. Incluye la patente del camion y el valor de tara (peso vacio).",
    required = true,
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(
            example = """
            {
                "patente": "AH-123-AL",
                "tara": 15400.5
            }
            """
        )
    )
)

//PUNTO2
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TMS')")
@PostMapping(value = "/pesaje-inicial", produces = MediaType.TEXT_PLAIN_VALUE)
public ResponseEntity<?> registerInitialWeighing(@RequestBody JsonNode body) {

    try {
        String patente = body.get("patente").asText();
        float tara = (float) body.get("tara").asDouble();

        Orden orden = ordenBusiness.registrarPesoInicial(patente, tara);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Order-Id", String.valueOf(orden.getNumeroOrden()));

        return new ResponseEntity<>(orden.getPassword().toString(), responseHeaders, HttpStatus.OK);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
    }
}

@Operation(
	    summary = "Cerrar la orden de carga",
	    description = "Este endpoint recibe el número de una orden y procede a marcarla como CERRADA_PARA_CARGA en el sistema. "
	        + "Para ello debe pasar por body el numero de orden correspondiente y debe estar la orden como CON_PESAJE_INICIAL"
	        + "Devuelve un mensaje de éxito en el cuerpo y el número de orden en el header."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Orden cerrada correctamente. Devuelve un mensaje de confirmación en el cuerpo y el número de orden en el header 'Order-Number'.",
	        content = @Content(
	            mediaType = "text/plain",
	            examples = {
	                @ExampleObject(
	                    name = "Respuesta exitosa",
	                    value = "Orden cerrada correctamente"
	                )
	            }
	        )
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Error en los datos enviados, la orden no existe, o la orden ya estaba cerrada/finalizada."
	    ),
	    @ApiResponse(
	        responseCode = "500",
	        description = "Error interno del servidor durante el proceso de cierre."
	    )
	})
@io.swagger.v3.oas.annotations.parameters.RequestBody(
	    description = "Número de la orden que se desea cerrar.",
	    required = true,
	    content = @Content(
	        mediaType = "application/json",
	        schema = @Schema(
	            example = """
	            {
	                "numeroOrden": 1024
	            }
	            """
	        )
	    )
	)
    
    //PUNTO 4)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLI3')")
    @PostMapping("/cerrar-carga")
    public ResponseEntity<?> closeOrder(@RequestBody JsonNode body) {

        try {
            Integer numeroOrden = body.get("numeroOrden").asInt();

            Orden orden = ordenBusiness.cerrarOrden(numeroOrden);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Order-Number", String.valueOf(orden.getNumeroOrden()));

            return new ResponseEntity<>("Orden cerrada correctamente", responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }

//1. Anotación de Swagger para describir el RequestBody
@io.swagger.v3.oas.annotations.parameters.RequestBody(
 description = "Datos técnicos de la carga. Requiere el número de orden y la contraseña generada previamente para la autorización.",
 required = true,
 content = @Content(
     mediaType = "application/json",
     schema = @Schema(implementation = DatosCargaDTO.class),
     examples = {
         @ExampleObject(
             name = "Ejemplo de Datos de Carga",
             value = """
             {
               "numeroOrden": 401,
               "password": 79509,
               "masa": 5900,
               "densidad": 4,
               "temperatura": 100,
               "caudal": 100
             }
             """
         )
     }
 )
)
//2. Anotación de Swagger para describir la Operación
@Operation(
 summary = "Registrar los datos de carga/descarga de una orden",
 description = "Este endpoint recibe los datos técnicos de la carga (masa, densidad, temperatura y caudal) junto con el número de orden y la contraseña generada en el pesaje inicial. "
     + "La orden debe existir y estar en estado 'CON_PESAJE_INICIAL'. La contraseña actúa como token de seguridad para autorizar la carga de datos."
)
//3. Anotación de Swagger para describir las Respuestas
@ApiResponses({
 @ApiResponse(
     responseCode = "200",
     description = "Datos de carga recibidos y registrados correctamente. Devuelve un mensaje de éxito y el número de orden en el header.",
     content = @Content(mediaType = "text/plain", examples = {@ExampleObject(name = "Respuesta exitosa", value = "Datos recibidos correctamente")})
 ),
 @ApiResponse(
     responseCode = "400",
     description = "Error de negocio (`BusinessException`). Posibles causas: La contraseña es incorrecta, la orden no está en el estado 'CON_PESAJE_INICIAL', o hay un error de validación en los datos enviados.",
     content = @Content(mediaType = "text/plain")
 ),
 @ApiResponse(
     responseCode = "404",
     description = "Orden no encontrada (`NotFoundException`). La orden con el número especificado no existe en el sistema.",
     content = @Content(mediaType = "text/plain")
 ),
 @ApiResponse(
     responseCode = "500",
     description = "Error interno del servidor (`Exception`).",
     content = @Content(mediaType = "text/plain")
 )
})
    // PUNTO 3) - Recibir datos de carga desde JSON
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLI3')")
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


@io.swagger.v3.oas.annotations.parameters.RequestBody(
	    description = "Contiene el número de orden y el peso final registrado en la balanza.",
	    required = true,
	    content = @Content(
	        mediaType = "application/json",
	        examples = {
	            @ExampleObject(
	                name = "Solicitud de Pesaje Final",
	                value = """
	                {
	                    "numeroOrden": 401,
	                    "pesoFinal": 25500.0
	                }
	                """
	            )
	        }
	    )
	)
	@Operation(
	    summary = "Registrar el pesaje final y realizar conciliación de la orden",
	    description = "Recibe el número de orden y el peso final del camión (TARA + Carga). "
	        + "La orden debe estar en estado CERRADA_PARA_CARGA y la pasa a FINALIZADO"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Pesaje final registrado. Devuelve un objeto JSON con los resultados de la conciliación y los promedios de datos.",
	        content = @Content(
	            mediaType = MediaType.APPLICATION_JSON_VALUE,
	            examples = {
	                @ExampleObject(
	                    name = "Resultado de Conciliación Exitosa",
	                    value = """
	                    {
	                        "numeroOrden": 401,
	                        "tara": 10000.0,
	                        "pesoFinal": 25500.0,
	                        "productoCargado": 15400.0,
	                        "netoPorBalanza": 15500.0,
	                        "diferencia": 100.0,
	                        "promedioTemperatura": 95.5,
	                        "promedioDensidad": 3.8,
	                        "promedioCaudal": 98.7,
	                        "fechaPesajeFinal": "2025-11-09T23:54:49.000Z"
	                    }
	                    """
	                )
	            }
	        )
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Orden no encontrada (`NotFoundException`). La orden no existe o no se encontró el pesaje inicial asociado.",
	        content = @Content(mediaType = "text/plain")
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Error de negocio (`BusinessException`). Posibles causas: La orden no está en un estado válido para pesaje final, o faltan datos previos.",
	        content = @Content(mediaType = "text/plain")
	    ),
	    @ApiResponse(
	        responseCode = "500",
	        description = "Error interno del servidor.",
	        content = @Content(mediaType = "text/plain")
	    )
	})

	// PUNTO 5) - Conciliacion de orden
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TMS')")
	@PostMapping(value = "pesaje-final", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registrarPesajeFinal(@RequestBody JsonNode body) {
        try {
            Integer numeroOrden = body.get("numeroOrden").asInt();
            Double pesoFinal = body.get("pesoFinal").asDouble();

            var resultado = ordenBusiness.registrarPesajeFinal(numeroOrden, pesoFinal);
            return ResponseEntity.ok(resultado);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error interno al registrar pesaje final", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
        }
    }
	
	// PUNTO 5 - Obtener conciliacion GET

	@GetMapping(value = "/conciliacion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getConciliacion(@RequestParam Integer numeroOrden) {

		try{
			return new ResponseEntity<>(ordenBusiness.getConciliacion(numeroOrden), HttpStatus.OK);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (BusinessException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error interno al obtener conciliacion", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno");
		}

	}

	// Extra: buscar por orden especifica

	@GetMapping(value = "/by-number/{numeroOrden}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getByNumeroOrden(@PathVariable Integer numeroOrden) {
	    try {
	        Orden orden = ordenBusiness.loadByNumeroOrden(numeroOrden);
	        return new ResponseEntity<>(orden, HttpStatus.OK);
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (BusinessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}

	@Operation(
		summary = "Historial de datos de carga",
		description = "Devuelve el historial de datos de carga de una orden para graficar temperatura durante la carga. Cada elemento representa un envío de datos (POST /datos-carga)."
	)
	@GetMapping(value = "/by-number/{numeroOrden}/historial-carga", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getHistorialCarga(@PathVariable Integer numeroOrden) {
	    try {
	        var historial = ordenBusiness.getHistorialCargaByNumeroOrden(numeroOrden);
	        return new ResponseEntity<>(historial, HttpStatus.OK);
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (BusinessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}
	
	
	//PARA MODIFICAR LAS ALARMAS
	//TERMINAR
	@PostMapping("/accept-alarm")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> aceptarAlarma (@RequestParam("idAlarm") Long idAlarm) {
        User user = getUserLogged(); //esto es para ver que operador aceptó la alarma.
        
        
        //ACA IMPLEMENTAR ESTO. ES PARA ACEPTAR LA ALARMA (CAMBIAR EL ESTADO DE LA ALARMA CUYO ID ES EL QUE VIENE X idAlarm). A su vez tenes que cambiar tambien la orden
        // la orden acordate que viene ya dentro de la alarma (en la bd guardaste la  orden) ==> fijate de revisar esto (no se como se guarda en la bd)
        // tenes esto:
        //@ManyToOne
        // @JoinColumn(name = "id_order", nullable = false)
        //private Orden orden;
        // en el alarm event listener pusiste esto: alarm.setOrden(orden);
        
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
        	Orden orden = ordenBusiness.alarmaAceptada(idAlarm, user);
        	responseHeaders.set("Location", Constants.URL_ORDEN + orden.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }catch(NotFoundException e) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (BusinessException e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        
    }
	
	
}
