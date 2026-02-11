package project.iw3.iw3.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import project.iw3.iw3.auth.BadPasswordException;
import project.iw3.iw3.auth.IUserBusiness;
import project.iw3.iw3.auth.IRoleBusiness;
import project.iw3.iw3.auth.Role;
import project.iw3.iw3.auth.User;
import project.iw3.iw3.controllers.AlarmRestController;
import project.iw3.iw3.controllers.BaseRestController;
import project.iw3.iw3.controllers.Constants;
import project.iw3.iw3.model.business.exceptions.BusinessException;
import project.iw3.iw3.model.business.exceptions.NotFoundException;

import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping(Constants.URL_USER)
public class UserRestController extends BaseRestController{

	@Autowired
	private IUserBusiness userBusiness;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IRoleBusiness roleBusiness;
	
	
	//listar usuarios
	@GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> list() {
        try {
            return ResponseEntity.ok(userBusiness.list());
        } catch (BusinessException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
	
	
	//traer un usuario en particular  va por ?
	@GetMapping("/search") // Cambiamos la ruta para que no choque con otros GET
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> load(@RequestParam String usernameOrEmail) {
	    try {
	        // La lógica del negocio sigue siendo la misma
	        return ResponseEntity.ok(userBusiness.load(usernameOrEmail));
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (BusinessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}
	
	//cambiar contraseña va por body
	@PostMapping("/change-password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> changePassword(@RequestBody JsonNode request) {
        try {
        	String usernameOrEmail = request.get("usernameoremail").asText();
        	String oldPassword = request.get("oldPassword").asText();
        	String newPassword = request.get("newPassword").asText();
        	if (usernameOrEmail == null || oldPassword == null || newPassword == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Faltan campos obligatorios");
            }
        	
        	userBusiness.changePassword(usernameOrEmail, oldPassword, newPassword, passwordEncoder);
            return ResponseEntity.ok().build();
        } catch (BadPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contraseña anterior incorrecta");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
	
	
	// Desactivar un usuario. URL: .../disable?usernameOrEmail=as
	@PutMapping("/disable")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> disable(@RequestParam String usernameOrEmail) {
	    try {
	        userBusiness.disable(usernameOrEmail);
	        return ResponseEntity.ok("Usuario desactivado correctamente");
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (BusinessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}

	// Activar un usuario. URL: .../enable?usernameOrEmail=as
	@PutMapping("/enable")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> enable(@RequestParam String usernameOrEmail) {
	    try {
	        userBusiness.enable(usernameOrEmail);
	        return ResponseEntity.ok("Usuario activado correctamente");
	    } catch (NotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (BusinessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}
    
 // Endpoint para asignar un rol a un usuario ==> va por body
    @PutMapping("/assign-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> assignRole(@RequestBody JsonNode payload) {
        try {
            // Extraemos los datos del JSON
            String usernameOrEmail = payload.get("usernameOrEmail").asText();
            String roleName = payload.get("roleName").asText();

            // 1. Cargamos el usuario
            User user = userBusiness.load(usernameOrEmail);

            // 2. Cargamos el rol
            Role role = roleBusiness.load(roleName);

            // 3. Ejecutamos la lógica de negocio 
            User updatedUser = userBusiness.addRole(role, user);

            return ResponseEntity.ok(updatedUser);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al asignar el rol");
        }
    }

    // Endpoint para quitar un rol a un usuario ==> va por body
    @PutMapping("/remove-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removeRole(@RequestBody JsonNode payload) {
        try {
            String usernameOrEmail = payload.get("usernameOrEmail").asText();
            String roleName = payload.get("roleName").asText();

       
            User user = userBusiness.load(usernameOrEmail);
            Role role = roleBusiness.load(roleName);

          
            User updatedUser = userBusiness.deleteRole(role, user);

            return ResponseEntity.ok(updatedUser);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al quitar el rol");
        }
    }
	
	
	
	
	
	
}
