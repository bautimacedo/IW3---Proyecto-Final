package project.iw3.iw3.util;

import java.security.SecureRandom;

public class GeneradorDePasswordActivacionPaso2 {
	
    private static final SecureRandom random = new SecureRandom();
    private static final String DIGITOS = "0123456789";
    
    public static String generarPassword() {
        StringBuilder password = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(DIGITOS.length());
            password.append(DIGITOS.charAt(index));
        }
        return password.toString();
    }

}
