package project.iw3.iw3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import project.iw3.iw3.model.Cisterna;
import project.iw3.iw3.model.business.exceptions.*;
import project.iw3.iw3.model.business.interfaces.ICisternaBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_CISTERNAS)
@Tag(name = "Cisterna", description = "API servicios relacionados con Cisternas")
public class CisternaRestController {

    @Autowired
    private ICisternaBusiness cisternaBusiness;

    @Autowired
    private IStandartResponseBusiness standartResponseBusiness;

    @Operation(
        summary = "Listar todas las cisternas",
        description = "Devuelve la lista completa de cisternas registradas en la base de datos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cisternas obtenida correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Cisterna.class)),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de lista de cisternas",
                        value = """
                        [
                            {
                                "id": 1,
                                "capacidadLitros": 50000,
                                "licencia": "LIC123",
                                "descripcion": "Cisterna principal"
                            },
                            {
                                "id": 2,
                                "capacidadLitros": 30000,
                                "licencia": "LIC456",
                                "descripcion": "Cisterna secundaria"
                            }
                        ]
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Obtener una cisterna por ID",
        description = "Busca una cisterna en la base de datos a partir de su identificador unico."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        schema = @Schema(type = "long"),
        required = true,
        description = "Identificador unico de la cisterna. Ejemplo: 1"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cisterna encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Cisterna.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de cisterna por ID",
                        value = """
                        {
                            "id": 1,
                            "capacidadLitros": 50000,
                            "licencia": "LIC123",
                            "descripcion": "Cisterna principal"
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "404", description = "Cisterna no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Obtener una cisterna por licencia",
        description = "Permite buscar una cisterna a partir de su numero de licencia."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "licencia",
        schema = @Schema(type = "string"),
        required = true,
        description = "Numero de licencia de la cisterna. Ejemplo: LIC123"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cisterna encontrada por licencia",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Cisterna.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de cisterna por licencia",
                        value = """
                        {
                            "id": 1,
                            "capacidadLitros": 50000,
                            "licencia": "LIC123",
                            "descripcion": "Cisterna principal"
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "404", description = "Cisterna no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Registrar una nueva cisterna",
        description = "Registra una nueva cisterna en la base de datos. Se tiene que enviar un objeto con los datos requeridos: capacidadLitros, licencia y descripcion."
    )
    @RequestBody(
        description = "Objeto Cisterna a registrar",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Cisterna.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de registro de cisterna",
                    value = """
                    {
                        "capacidadLitros": 60000,
                        "licencia": "LIC789",
                        "descripcion": "Cisterna de reserva"
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cisterna registrada correctamente"),
        @ApiResponse(responseCode = "409", description = "Cisterna con licencia duplicada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Actualizar una cisterna existente",
        description = "Permite modificar los datos de una cisterna existente. Se tiene que enviar el objeto completo incluyendo el ID."
    )
    @RequestBody(
        description = "Objeto Cisterna actualizado",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Cisterna.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de actualizacion de cisterna",
                    value = """
                    {
                        "id": 1,
                        "capacidadLitros": 52000,
                        "licencia": "LIC123",
                        "descripcion": "Cisterna principal actualizada"
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cisterna actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Cisterna no encontrada"),
        @ApiResponse(responseCode = "409", description = "Error por licencia duplicada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Eliminar una cisterna por ID",
        description = "Elimina una cisterna de la base de datos a partir de su id."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        required = true,
        schema = @Schema(type = "long"),
        description = "Identificador unico de la cisterna a eliminar. Ejemplo: 3"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cisterna eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Cisterna no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
