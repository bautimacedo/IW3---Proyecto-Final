package project.iw3.iw3.util;

import project.iw3.iw3.util.*;

import project.iw3.iw3.model.*;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class BuildEntityUtiles {
	
	public static Chofer choferConstructor(JsonNode choferNode) {
		
		Chofer chofer = new Chofer();
		
		Long id = JsonUtiles.getLong(choferNode, ConstantesJson.DRIVER_ID_ATTRIBUTES, 0);
        if (id != null && id != 0) {
            chofer.setId(id);
        }

        String nombre = JsonUtiles.getString(choferNode, ConstantesJson.DRIVER_NOMBRE_ATTRIBUTES, "");
        if (nombre != null && !nombre.isEmpty()) {
            chofer.setNombre(nombre);
        }

        String apellido = JsonUtiles.getString(choferNode, ConstantesJson.DRIVER_APELLIDO_ATTRIBUTES, "");
        if (apellido  != null && !apellido.isEmpty()) {
            chofer.setApellido(apellido);
        }

        String dni = JsonUtiles.getString(choferNode, ConstantesJson.DRIVER_DNI_ATTRIBUTES, "");
        if (dni != null && !dni.isEmpty()) {
            chofer.setDni(dni);
        }

        return chofer;
		
	}
	
	public static Camion camionConstructor(JsonNode camionNode, JsonNode cisternaNode) {
		
		Camion camion = new Camion();
		
		Long id = JsonUtiles.getLong(camionNode, ConstantesJson.CAMION_ID_ATTRIBUTES, 0);
        if (id != null && id != 0) {
            camion.setId(id);
        }
        
        String patente = JsonUtiles.getString(camionNode, ConstantesJson.CAMION_PATENTE_ATTRIBUTES, "");
        if (patente != null && !patente.isEmpty()) {
            camion.setPatente(patente);
        }
        
        String descripcion = JsonUtiles.getString(camionNode, ConstantesJson.CAMION_DESCRIPCION_ATTRIBUTES , "");
        if (descripcion != null && !descripcion.isEmpty()) {
            camion.setDescripcion(descripcion);
        }
        
        //ya tenemos todo lo de camion, ahora tenemos que agregarle las cisternas.
        //recorremos nodos Json de tanks o tankers y convertimos cada uno de esos objetos en cisternas con sus atributos para meterlos
        //al set de camion. private Set<Cisterna> cisterna = new HashSet<>();
        
        Set<Cisterna> cisternas = new HashSet<>();
        
        if(cisternaNode != null && cisternaNode.isArray()) {
        	
        	for (JsonNode cisternasNode : cisternaNode) { //bucle for each, recorremos cisternaNode uno x uno
        		Cisterna cisterna = new Cisterna();
        		
        		Long cisternaId = JsonUtiles.getLong(cisternasNode, ConstantesJson.CISTERNA_ID_ATTRIBUTES, 0);
                if (cisternaId != null && cisternaId != 0) {
                    cisterna.setId(cisternaId);
                }
                
                Long capacidadLitros = JsonUtiles.getLong(cisternasNode, ConstantesJson.CISTERNA_CAPACIDAD_LITROS_ATTRIBUTES , 0);
                if (capacidadLitros != null && capacidadLitros != 0) {
                    cisterna.setCapacidadLitros(capacidadLitros);
                }
                
                String licencia = JsonUtiles.getString(cisternasNode, ConstantesJson.CISTERA_LICENCIA_ATTRIBUTES , "");
                if (licencia != null && !licencia.isEmpty()) {
                    cisterna.setLicencia(licencia);
                }
                
                cisternas.add(cisterna);
        		
        	}
        	
        	
        	
        }
        
    	camion.setCisterna(cisternas);
    	return camion;    
        
	}
	
	
	public static Cliente clienteConstructor(JsonNode clienteNode) {
		Cliente cliente = new Cliente();
		
		Long Id = JsonUtiles.getLong(clienteNode, ConstantesJson.CLIENTE_ID_ATTRIBUTES, 0);
        if (Id != null && Id != 0) {
            cliente.setId(Id);
        }

        String nombre = JsonUtiles.getString(clienteNode, ConstantesJson.CLIENTE_NOMBRE_ATTRIBUTES, "");
        if (nombre != null && !nombre.isEmpty()) {
            cliente.setNombreEmpresa(nombre);
        }

        String email = JsonUtiles.getString(clienteNode, ConstantesJson.CLIENTE_EMAIL_ATTRIBUTES, "");
        if (email != null && !email.isEmpty()) {

            cliente.setEmail(email);
        }
        
        
        return cliente;
			
	}
	
	public static Producto productoConstructor(JsonNode productoNode) {
		Producto producto = new Producto();
		
		Long Id = JsonUtiles.getLong(productoNode, ConstantesJson.PRODUCTO_ID_ATTRIBUTES, 0);
        if (Id != null && Id != 0) {
            producto.setId(Id);
        }
        
        String nombre = JsonUtiles.getString(productoNode, ConstantesJson.PRODUCTO_NOMBRE_ATTRIBUTES, "");
        if (nombre != null && !nombre.isEmpty()) {
            producto.setNombre(nombre);
        }
        
        String descripcion = JsonUtiles.getString(productoNode, ConstantesJson.PRODUCTO_DESCRIPCION_ATTRIBUTES , "");
        if (descripcion != null && !descripcion.isEmpty()) {
            producto.setDescripcion(descripcion);
        }
		
		
		return producto;
	}
	

}
