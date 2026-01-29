package project.iw3.iw3.controllers;

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
import project.iw3.iw3.model.Chofer;
import project.iw3.iw3.model.business.exceptions.*;
import project.iw3.iw3.model.business.interfaces.IChoferBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;

import java.util.List;

@RestController
@RequestMapping(Constants.URL_CHOFERES)
@Tag(name = "Chofer", description = "API servicios relacionados con Choferes")
public class ChoferRestController {

    @Autowired
    private IChoferBusiness choferBusiness;

    @Autowired
    private IStandartResponseBusiness responseBuilder;

    @Operation(
        summary = "Listar choferes",
        description = "Devuelve la lista completa de choferes registrados en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de choferes obtenida correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Chofer.class)),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de lista",
                        value = """
                        [
                            {
                                "id": 1,
                                "dni": "40123456",
                                "nombre": "Juan Perez",
                                "telefono": "1165437890"
                            },
                            {
                                "id": 2,
                                "dni": "39123457",
                                "nombre": "Carlos Lopez",
                                "telefono": "1145672311"
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
            List<Chofer> lista = choferBusiness.list();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
        summary = "Registrar un nuevo chofer",
        description = "Registra un nuevo chofer en la base de datos. Se tiene que enviar un objeto Chofer con todos los datos requeridos."
    )
    @RequestBody(
        description = "Objeto Chofer a registrar",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Chofer.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de chofer a registrar",
                    value = """
                    {
                        "dni": "43123456",
                        "nombre": "Pedro Gomez",
                        "telefono": "1134567890"
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Chofer creado correctamente"),
        @ApiResponse(responseCode = "302", description = "Chofer duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody Chofer chofer) {
        try {
            Chofer creado = choferBusiness.add(chofer);
            HttpHeaders headers = new HttpHeaders();
            headers.set("location", Constants.URL_CHOFERES + "/" + creado.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (FoundException e) {
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

    @Operation(
        summary = "Obtener un chofer por ID",
        description = "Busca un chofer en la base de datos a partir de su identificador unico (ID)."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        schema = @Schema(type = "long"),
        required = true,
        description = "Identificador unico del chofer."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Chofer encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Chofer.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de respuesta",
                        value = """
                        {
                            "id": 1,
                            "dni": "40123456",
                            "nombre": "Juan Perez",
                            "telefono": "1165437890"
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "404", description = "Chofer no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Obtener un chofer por DNI",
        description = "Busca un chofer usando su numero de DNI. Devuelve el objeto Chofer si se encuentra registrado."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "dni",
        schema = @Schema(type = "string"),
        required = true,
        description = "Numero de DNI del chofer. Ejemplo: 40123456"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chofer encontrado"),
        @ApiResponse(responseCode = "404", description = "Chofer no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @Operation(
        summary = "Actualizar un chofer existente",
        description = "Permite actualizar los datos de un chofer ya existente. Se debe enviar el objeto completo con el ID y los nuevos valores."
    )
    @RequestBody(
        description = "Objeto Chofer con los datos actualizados",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Chofer.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de actualizacion",
                    value = """
                    {
                        "id": 1,
                        "dni": "40123456",
                        "nombre": "Juan Perez",
                        "telefono": "1156784321"
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chofer actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Chofer no encontrado"),
        @ApiResponse(responseCode = "302", description = "Error con un chofer duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Chofer chofer) {
        try {
            choferBusiness.update(chofer);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        } catch (FoundException e) {
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

    @Operation(
        summary = "Eliminar un chofer por ID",
        description = "Elimina un chofer de la base de datos usando su identificador unico."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        required = true,
        schema = @Schema(type = "long"),
        description = "Identificador unico del chofer a eliminar. Ejemplo: 2"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chofer eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Chofer no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
