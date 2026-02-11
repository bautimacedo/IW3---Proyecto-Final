package project.iw3.iw3.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.auth.IRoleBusiness;
import project.iw3.iw3.auth.IUserBusiness;
import project.iw3.iw3.auth.Role;
import project.iw3.iw3.auth.User;
import project.iw3.iw3.auth.UserRepository;
import project.iw3.iw3.controllers.BaseRestController;
import project.iw3.iw3.controllers.Constants;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;


@Slf4j
@RestController
@RequestMapping(Constants.URL_ROLE)
public class RoleRestController extends BaseRestController{
	
	@Autowired
	private IRoleBusiness roleBusiness;
	
	@GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(roleBusiness.list());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
	

	// Agregamos una sub-ruta para evitar el conflicto
    @GetMapping("/search") 
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> load(@RequestParam String name) { 
        try {
            return ResponseEntity.ok(roleBusiness.load(name));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> add(@RequestBody JsonNode request) {
        try {
            // 1. Validación: Verificamos que el campo 'name' esté presente
            if (!request.has("name") || request.get("name").asText().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre del rol es obligatorio.");
            }

            // 2. Mapeo: Creamos la instancia de Role y seteamos el nombre
            Role role = new Role();
            role.setName(request.get("name").asText());

            // 3. Ejecución: Llamamos a tu función de negocio
            // Esta función ya se encarga del DAO y del log.error si algo falla.
            Role nuevoRole = roleBusiness.add(role);
            
            // 4. Respuesta: Retornamos el objeto creado con código 201
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRole);

        } catch (BusinessException e) {
            // Respetamos tu manejo de excepciones personalizadas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en el controlador al agregar rol: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@RequestBody Role role) {
        try {
            return ResponseEntity.ok(roleBusiness.update(role));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
