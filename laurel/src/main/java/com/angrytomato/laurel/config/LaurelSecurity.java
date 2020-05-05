package com.angrytomato.laurel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class LaurelSecurity {
    @Value("${laurel.publickey}")
    private String base64PublicKey;

    @Value("${laurel.privatekey}")
    private String base64PrivateKey;
}
