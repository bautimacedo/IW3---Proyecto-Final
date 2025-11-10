package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                                        "licencia": "BAUTIGAY"
                                    },
                                    {
                                        "id": 4,
                                        "capacidadLitros": 35000,
                                        "licencia": "RODILLA GAY"
                                    },
                                    {
                                        "id": 6,
                                        "capacidadLitros": 35000,
                                        "licencia": "BRUNO MACHO ALFA"
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

    
   @Operation(
    summary = "Registrar una nueva orden externa (B2B)",
    description = """
Permite registrar una nueva orden proveniente de un sistema externo.
Se tiene que enviar un JSON con toda la informacion necesaria: camion, cisternas, chofer, cliente, producto y preset de carga.
La orden se crea en estado inicial.
"""
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Orden creada correctamente",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "Ejemplo de creacion de orden B2B",
                    value = """
                    {
                        "order_number": 777,
                        "truck": {
                            "id": 1000,
                            "licence_plate": "AB-OS",
                            "description": "Renault",
                            "tanks": [
                                { "id": 1, "capacity": 35000, "licence_plate": "GMI-1234" }
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
                        "preset": 18270
                    }
                    """
                )
            }
        )
    ),
    @ApiResponse(responseCode = "400", description = "Error en los datos recibidos o formato invalido"),
    @ApiResponse(responseCode = "409", description = "Error por numero de orden duplicado"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
@PostMapping(
    value = "/b2b",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
)
public ResponseEntity<?> addExternal(@RequestBody String body) {
    try {
        Orden response = ordenBusiness.addExternal(body);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, Constants.URL_ORDEN + "/" + response.getNumeroOrden());

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

    
    
    //PUNTO 4)
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

	// PUNTO 5) - Conciliacion de orden
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

	@GetMapping(value = "/by-number", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getByNumeroOrden(@RequestParam("numeroOrden") Integer numeroOrden) {
		try {
			Orden orden = ordenBusiness.loadByNumeroOrden(numeroOrden);
			return new ResponseEntity<>(orden, HttpStatus.OK);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (BusinessException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}
