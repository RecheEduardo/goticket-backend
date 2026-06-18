// src/test/java/tech/goticket/backendapi/HashGen.java
package tech.goticket.backendapi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("LoadTest123!"));
    }
}