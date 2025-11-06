package project.iw3.iw3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import project.iw3.iw3.model.Camion;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.FoundException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;
import project.iw3.iw3.model.business.interfaces.ICamionBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping(Constants.URL_CAMIONES)
@Tag(description = "API servicios relacionados con Camion", name = "Camion")
public class CamionRestController {

    @Autowired
    private ICamionBusiness camionBusiness;

    @Autowired
    private IStandartResponseBusiness standartResponseBusiness;
    
    
    @Operation(
            summary = "Listar camiones",
            description = "Devuelve la lista completa de camiones."
        )
        @ApiResponses({
            @ApiResponse(
                responseCode = "200",
                description = "Lista de camiones",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Camion.class)),
                    examples = {
                        @ExampleObject(
                            name = "Ejemplo",
                            value = """
                            [
    {
        "id": 1,
        "patente": "IAK-829",
        "descripcion": "Mercedez Benz",
        "cisterna": [
            {
                "id": 1,
                "capacidadLitros": 123456,
                "licencia": "ABC123"
            },
            {
                "id": 2,
                "capacidadLitros": 654321,
                "licencia": "CBA321"
            }
        ]
    },
    {
        "id": 2,
        "patente": "ABC-123",
        "descripcion": "CITROEN",
        "cisterna": []
    },
    {
        "id": 3,
        "patente": "GHI-7890",
        "descripcion": "Renault",
        "cisterna": []
    }
]
                            """
                        )
                    }
                )
            ),

            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            List<Camion> lista = camionBusiness.list();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                standartResponseBusiness.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @Operation(
            summary = "Obtener un camion por ID"
        )
    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), required = true, description = "Identificador del camion.")
        @ApiResponses({
            @ApiResponse(
                responseCode = "200",
                description = "Camion identificado por ID en base de datos ",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Camion.class)),
                    examples = {
                        @ExampleObject(
                            name = "Ejemplo",
                            value = """
                           
									    {
									        "id": 1,
									        "patente": "IAK-829",
									        "descripcion": "Mercedez Benz",
									        "cisterna": [
									            {
									                "id": 1,
									                "capacidadLitros": 123456,
									                "licencia": "ABC123"
									            },
									            {
									                "id": 2,
									                "capacidadLitros": 654321,
									                "licencia": "CBA321"
									            }
									        ]
									    }

                            """
                        )
                    }
                )
            ),

            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable long id) {
        try {
            return new ResponseEntity<>(camionBusiness.load(id), HttpStatus.OK);
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
    
    
    

    // camion por patente
    @Operation(
            summary = "Obtener un camion por Patente"
        )
    @Parameter(in = ParameterIn.PATH, name = "patente", schema = @Schema(type = "String"), required = true, description = "Patente del camion. Ex. \"IAK-829\"")
        @ApiResponses({
            @ApiResponse(
                responseCode = "200",
                description = "Camion identificado por patente en base de datos ",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Camion.class)),
                    examples = {
                        @ExampleObject(
                            name = "Ejemplo",
                            value = """
                           
									    {
									        "id": 1,
									        "patente": "IAK-829",
									        "descripcion": "Mercedez Benz",
									        "cisterna": [
									            {
									                "id": 1,
									                "capacidadLitros": 123456,
									                "licencia": "ABC123"
									            },
									            {
									                "id": 2,
									                "capacidadLitros": 654321,
									                "licencia": "CBA321"
									            }
									        ]
									    }

                            """
                        )
                    }
                )
            ),

            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
    @GetMapping(value = "/by-patente/{patente}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable String patente) {
        try {
            return new ResponseEntity<>(camionBusiness.load(patente), HttpStatus.OK);
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

    // crear camioM
    @Operation(
            summary = "Registrar un nuevo camion",
            description = "Registra un nuevo camión en el sistema. Requiere un objeto Camion con todos los datos necesarios. La patente debe ser única.",
            requestBody = @RequestBody(
                description = "Objeto Camión a registrar",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Camion.class)
                )
            )
        )
    
    @ApiResponses({
    	  @ApiResponse(responseCode = "201", description = "Camión creado"),
    	  @ApiResponse(responseCode = "409", description = "Patente duplicada"),
    	  @ApiResponse(responseCode = "500", description = "Error interno")
        })
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody Camion camion) {
        try {
            Camion nuevo = camionBusiness.add(camion);
            HttpHeaders headers = new HttpHeaders();
            headers.set("location", "/api/v1/camiones/" + nuevo.getId());
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

    // actualizar caminn
    @Operation(summary = "Actualizar un camión existente")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Camion camion) {
        try {
            camionBusiness.update(camion);
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

    // eliminar camion
    @Operation(summary = "Eliminar un camión por ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            camionBusiness.delete(id);
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
