/*
 * AICare DI - Artificial Intelligence Care (Dynamic Inventory)
 * Todos os direitos reservados.
 */
package com.dminer.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Paulo Collares
 */
public class Senha {

    private final static String SALT = "m*R7OmcV73s$oSNTPBr7@0DS9sRD2c0h*N$OE%gR6SoE&5Ub*^mlNx4*xErMhFDwN8Kx!&fGo8Iyb6CTSuGdMXp#jBezs3f0lwO";

    private final String senha;

    public Senha(String senha) {
        this.senha = senha;
    }

    public String getHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getHash(senha);
    }

    private String getHash(String senha) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), SALT.getBytes(), 10000, 512);
        SecretKey key = skf.generateSecret(spec);
        byte[] res = key.getEncoded();
        return Base64.getEncoder().encodeToString(res);
    }

}
