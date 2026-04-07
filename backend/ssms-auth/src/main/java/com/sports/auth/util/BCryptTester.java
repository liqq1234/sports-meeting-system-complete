package com.sports.auth.util;

import cn.hutool.crypto.digest.BCrypt;

public class BCryptTester {
    public static void main(String[] args) {
        String password = "123456";
        String hash = "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PQm.Ak81RhkD0bDqnGHXa";
        boolean match = BCrypt.checkpw(password, hash);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Match: " + match);
        
        String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("New Hash for 123456: " + newHash);
        System.out.println("New Match: " + BCrypt.checkpw(password, newHash));
    }
}
