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
import project.iw3.iw3.model.Cliente;
import project.iw3.iw3.model.business.exceptions.*;
import project.iw3.iw3.model.business.interfaces.IClienteBusiness;
import project.iw3.iw3.util.IStandartResponseBusiness;

import java.util.List;

@RestController
@RequestMapping(Constants.URL_CLIENTES)
@Tag(name = "Cliente", description = "API servicios relacionados con Clientes")
public class ClienteRestController {

    @Autowired
    private IClienteBusiness clienteBusiness;

    @Autowired
    private IStandartResponseBusiness responseBuilder;

    @Operation(
        summary = "Listar clientes",
        description = "Devuelve la lista completa de clientes registrados en el sistema."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de clientes obtenida correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Cliente.class)),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de lista de clientes",
                        value = """
                        [
                            {
                                "id": 1,
                                "nombreEmpresa": "YPF",
                                "direccion": "Av. San Martin 1234",
                                "telefono": "1145678901",
                                "email": "contacto@ypf.com"
                            },
                            {
                                "id": 2,
                                "nombreEmpresa": "Shell",
                                "direccion": "Av. Corrientes 2345",
                                "telefono": "1132456789",
                                "email": "ventas@shell.com"
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
            List<Cliente> lista = clienteBusiness.list();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
        summary = "Registrar un nuevo cliente",
        description = "Registra un nuevo cliente en la base de datos. Se deben enviar todos los datos requeridos en formato JSON."
    )
    @RequestBody(
        description = "Objeto Cliente a registrar",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Cliente.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de cliente a registrar",
                    value = """
                    {
                        "nombreEmpresa": "Axion Energy",
                        "direccion": "Ruta 8 KM 45",
                        "telefono": "1123456789",
                        "email": "info@axionenergy.com"
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente creado correctamente"),
        @ApiResponse(responseCode = "302", description = "Cliente duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody Cliente cliente) {
        try {
            Cliente creado = clienteBusiness.add(cliente);
            HttpHeaders headers = new HttpHeaders();
            headers.set("location", Constants.URL_CLIENTES + "/" + creado.getId());
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
        summary = "Obtener un cliente por ID",
        description = "Busca un cliente en la base de datos a partir de su identificador unico (ID)."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        schema = @Schema(type = "long"),
        required = true,
        description = "Identificador unico del cliente. Ejemplo: 1"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Cliente.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de cliente encontrado",
                        value = """
                        {
                            "id": 1,
                            "nombreEmpresa": "YPF",
                            "direccion": "Av. San Martin 1234",
                            "telefono": "1145678901",
                            "email": "contacto@ypf.com"
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(clienteBusiness.load(id), HttpStatus.OK);
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
        summary = "Obtener un cliente por nombre de empresa",
        description = "Busca un cliente a partir del nombre de su empresa. Si el cliente existe, devuelve su informacion completa."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "nombreEmpresa",
        schema = @Schema(type = "string"),
        required = true,
        description = "Nombre de la empresa del cliente. Ejemplo: YPF"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/by-name/{nombreEmpresa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByNombre(@PathVariable("nombreEmpresa") String nombreEmpresa) {
        try {
            return new ResponseEntity<>(clienteBusiness.load(nombreEmpresa), HttpStatus.OK);
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
        summary = "Actualizar un cliente existente",
        description = "Permite modificar los datos de un cliente existente. Se debe enviar el objeto completo con el ID del cliente y los datos actualizados."
    )
    @RequestBody(
        description = "Objeto Cliente con los datos actualizados",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Cliente.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de actualizacion de cliente",
                    value = """
                    {
                        "id": 1,
                        "nombreEmpresa": "YPF",
                        "direccion": "Av. San Martin 1500",
                        "telefono": "1133344455",
                        "email": "nuevo-contacto@ypf.com"
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "302", description = "Error por nombre duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Cliente cliente) {
        try {
            clienteBusiness.update(cliente);
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
        summary = "Eliminar un cliente por ID",
        description = "Elimina un cliente de la base de datos utilizando su id."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        schema = @Schema(type = "long"),
        required = true,
        description = "Identificador unico del cliente a eliminar. Ejemplo: 2"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        try {
            clienteBusiness.delete(id);
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
