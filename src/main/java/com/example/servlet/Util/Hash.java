package com.example.servlet.Util;

import org.mindrot.jbcrypt.BCrypt;

public class Hash {
    // metodo para hasear a senha
    public static String bcrypt(String senha){
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // verificar se a senha digitada é igual a senha no banco
    public static boolean verficarBcrypt(String senha, String hashBanco){
        return BCrypt.checkpw(senha, hashBanco);
    }
}
