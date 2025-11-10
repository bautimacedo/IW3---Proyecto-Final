package project.iw3.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import project.iw3.iw3.model.Producto;
import project.iw3.iw3.model.business.interfaces.IProductoBusiness;
import project.iw3.iw3.model.business.exceptions.*;
import project.iw3.iw3.util.IStandartResponseBusiness;

import java.util.List;

@RestController
@RequestMapping(Constants.URL_PRODUCTS)
@Tag(name = "Producto", description = "API servicios relacionados con Productos")
public class ProductoRestController {

    @Autowired
    private IProductoBusiness productoBusiness;

    @Autowired
    private IStandartResponseBusiness responseBuilder;

    @Operation(
        summary = "Listar productos",
        description = "Devuelve la lista completa de productos registrados en la base de datos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida correctamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Producto.class)),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de lista de productos",
                        value = """
                        [
                            {
                                "id": 1,
                                "nombre": "Nafta Premium",
                                "descripcion": "Combustible de alto octanaje",
                                "precio": 1300.5
                            },
                            {
                                "id": 2,
                                "nombre": "Diesel Ultra",
                                "descripcion": "Combustible diesel de bajo azufre",
                                "precio": 1250.0
                            }
                        ]
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        try {
            List<Producto> lista = productoBusiness.list();
            return new ResponseEntity<>(lista, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(
                responseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Operation(
        summary = "Registrar un nuevo producto",
        description = "Registra un nuevo producto en la base de datos. Se deben enviar los datos completos en formato JSON."
    )
    @RequestBody(
        description = "Objeto Producto a registrar",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Producto.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de producto a registrar",
                    value = """
                    {
                        "nombre": "Nafta Super",
                        "descripcion": "Combustible de 95 octanos",
                        "precio": 1100.0
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
        @ApiResponse(responseCode = "302", description = "Producto duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody Producto producto) {
        try {
            Producto creado = productoBusiness.add(producto);
            HttpHeaders headers = new HttpHeaders();
            headers.set("location", Constants.URL_PRODUCTS + "/" + creado.getId());
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
        summary = "Obtener un producto por ID",
        description = "Busca un producto en la base de datos a partir de su identificador unico."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        schema = @Schema(type = "long"),
        required = true,
        description = "Identificador unico del producto. Ejemplo: 1"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Producto.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de producto encontrado",
                        value = """
                        {
                            "id": 1,
                            "nombre": "Nafta Premium",
                            "descripcion": "Combustible de alto octanaje",
                            "precio": 1300.5
                        }
                        """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(
        summary = "Obtener un producto por nombre",
        description = "Permite buscar un producto especificando su nombre."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "nombre",
        schema = @Schema(type = "string"),
        required = true,
        description = "Nombre del producto a buscar. Ejemplo: Nafta Super"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(
        summary = "Actualizar un producto existente",
        description = "Permite modificar los datos de un producto ya registrado. Se debe enviar el objeto completo con el ID y los nuevos valores."
    )
    @RequestBody(
        description = "Objeto Producto con los datos actualizados",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Producto.class),
            examples = {
                @ExampleObject(
                    name = "Ejemplo de actualizacion de producto",
                    value = """
                    {
                        "id": 1,
                        "nombre": "Nafta Premium Actualizada",
                        "descripcion": "Combustible de alto octanaje mejorado",
                        "precio": 1350.0
                    }
                    """
                )
            }
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "302", description = "Error por nombre duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Producto producto) {
        try {
            productoBusiness.update(producto);
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
        summary = "Eliminar un producto por ID",
        description = "Elimina un producto de la base de datos utilizando su id."
    )
    @Parameter(
        in = ParameterIn.PATH,
        name = "id",
        schema = @Schema(type = "long"),
        required = true,
        description = "Identificador unico del producto a eliminar. Ejemplo: 2"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
