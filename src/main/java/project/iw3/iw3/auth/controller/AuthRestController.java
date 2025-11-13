package project.iw3.iw3.auth.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import project.iw3.iw3.auth.User;
import project.iw3.iw3.auth.custom.CustomAuthenticationManager;
import project.iw3.iw3.auth.filters.AuthConstants;
import project.iw3.iw3.controllers.BaseRestController;
import project.iw3.iw3.util.IStandartResponseBusiness;
import project.iw3.iw3.controllers.Constants;
//import ar.edu.iw3.auth.event.UserEvent;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthRestController extends BaseRestController {
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private IStandartResponseBusiness response;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;


	@PostMapping(value = Constants.URL_LOGIN, produces = MediaType.TEXT_PLAIN_VALUE)
	//pasamos x body
	public ResponseEntity<?> loginExternalOnlyToken(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
		Authentication auth = null;
		try {
			//el metodo authenticate tiene un monton de metodos que implementar que son tremenda paja. lo unico que le quiero pasar yo
			// es el usuario y la contraseña (obvio depende de lo que querramos hacer).
			// x eso se creo authwrap que lo que hace es basicamente autosetear todas esas variables para no tener que hacerlo yo.
			// lo unico que implementa es getName y getCredentials --> osea la password.
			auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
		} catch (AuthenticationServiceException e0) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		}
		
		//obtengo la instancia de usuario. principal es user.
		User user = (User) auth.getPrincipal();
		
		//contruyo el jwt. le paso el usuario, los claims, cuanto duran los tokens (en authconstants) --> 1h en ms y funalmente con .sign cifro.
		String token = JWT.create().withSubject(user.getUsername()) 
				.withClaim("internalId", user.getIdUser())
				.withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr()))
				.withClaim("email", user.getEmail())
				.withClaim("version", "1.0.0")
				.withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
				.sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

		//applicationEventPublisher.publishEvent(new UserEvent(user, request, UserEvent.TypeEvent.LOGIN));

		return new ResponseEntity<String>(token, HttpStatus.OK); // devuelvo el token
	}
	@Autowired
	private PasswordEncoder pEncoder;

	@GetMapping(value = "/demo/encodepass", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> encodepass(@RequestParam String password) {
		try {
			return new ResponseEntity<String>(pEncoder.encode(password), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}