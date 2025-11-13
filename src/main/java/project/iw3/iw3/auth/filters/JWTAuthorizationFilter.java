
package project.iw3.iw3.auth.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import project.iw3.iw3.auth.Role;
import project.iw3.iw3.auth.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j //extendemos de BasicAuthenticationFilter y eso me ayuda a ubicarlo en la posición justa de la cola de filtros.
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override // esto es la imolementacion del filtro. basicamente recibe el request, response y la cadena de filtros.
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		// tengo que definir como enviar mis tokens. en el body, en el header o como parametro.
		// en nuestro caso vamos a usar el estandar por header y se llama authorization
		// sin embargo tambien vamos a tratar de obtenerlo desde el body o por parametro es decir querystring (x la url).
		String header = req.getHeader(AuthConstants.AUTH_HEADER_NAME); 
		String param = req.getParameter(AuthConstants.AUTH_PARAM_NAME); //authtoken
		
		// tomo x header cuando != null y cuando comienza con la palabra Bearer.
		boolean byHeader = !(header == null || !header.startsWith(AuthConstants.TOKEN_PREFIX)); // true si viene x header.
		
		//minimamente tiene un tamaño de 10
		boolean byParam = !(param == null || param.trim().length() < 10);
		
		// Si no se envía o es correcto el inicio de la cabecera o bien no se envía un
		// parámetro correcto, se continúa con el resto de los filtros
		if (!byHeader && !byParam) {
			chain.doFilter(req, res); // en caso de que no me llegue nada, paso la query al siguiente filtro.
			return;					  // por que no reboto de una? xq capaz hay otro filtro que tiene otro metodo de autenticación
		}							  // qcyo biometrico x ejemplo.		
		
		// Le damos prioridad al header.
		UsernamePasswordAuthenticationToken authentication = getAuthentication(req, byHeader); // getAutentication extrae el JWT de la cabecera. 
		SecurityContextHolder.getContext().setAuthentication(authentication); //ACA LE PASO CHE ESTE ES EL USUARIO LOGUEADO.
		chain.doFilter(req, res);

	}

	// Extraer el token JWT de la cabecera y lo intenta validar
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, boolean byHeader) {
		// le quito el prefijo Bearer y me queda el token solo (en el caso de que venga byHeader =true, sino lo saca del parametro al token)
		String token = byHeader
				? request.getHeader(AuthConstants.AUTH_HEADER_NAME).replace(AuthConstants.TOKEN_PREFIX, "")
				: request.getParameter(AuthConstants.AUTH_PARAM_NAME);

		if (token != null) {
            token = token.trim();
            if (token.isEmpty()) {
                    return null;
            }
            // Parseamos el token usando la librería
            DecodedJWT jwt=null; 
			try {
				//jwt va a ser un objeto
				jwt = JWT.require(Algorithm.HMAC512(AuthConstants.SECRET.getBytes())).build().verify(token); //verifico el token y la clave de cifrado que es MyVerySecretKey 
				//acordate de desactivar esto en produccion el trace
				log.trace("Token recibido por '{}'", byHeader ? "header" : "query param");
				log.trace("Usuario logueado: " + jwt.getSubject());
				log.trace("Roles: " + jwt.getClaim("roles"));
				log.trace("Custom JWT Version: " + jwt.getClaim("version").asString());
				
				//hasta ahora lo unico que hice fue obtener un token x cabecera y decifrarlo nada mas. Ahora le tengo que avisar a spring.
				
				Set<Role> roles=new HashSet<Role>();
				
				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				@SuppressWarnings("unchecked")
				List<String> rolesStr = (List<String>) jwt.getClaim("roles").as(List.class); //obtengo del token los roles.
				authorities = rolesStr.stream().map(role -> new SimpleGrantedAuthority(role))
						.collect(Collectors.toList()); // paso los roles a granted authorities
				roles=rolesStr.stream().map(role-> new Role(role,0,role)).collect(Collectors.toSet());
				String username = jwt.getSubject();
				
				//creo el objeto user con todos los datos que me llegan del token. 
				// hace falta verificar que el usuario no este bloqueado y todo eso? NO
				// esto lo hacemos la primera vez que nos logueamos nomas. ahora como para cada query
				// tengo que mandarle el token, no hace falta verificar esto (ya estoy logueado)
				if (username != null) {
					User user = new User();
					user.setIdUser(jwt.getClaim("internalId").asLong());
					user.setUsername(username);
					user.setRoles(roles);
					user.setEmail(jwt.getClaim("email").asString());
					return new UsernamePasswordAuthenticationToken(user, null, authorities);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			
			
			return null;
		}
		return null;
	}

}
