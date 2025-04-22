package com.saiyans.aicodereviewer.github;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class GitHubJwtUtil {

    private static final String GITHUB_APP_ID = "1225599"; // Replace with your App ID

    public static String generateJWT() {
        try {
            PrivateKey privateKey = loadPrivateKey("saiyans-ai-code-reviewer-bot.2025-04-22.private-key.pem");

            Instant now = Instant.now();
            return Jwts.builder()
                    .setIssuer(GITHUB_APP_ID)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusSeconds(540))) // max 10 min
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate GitHub App JWT", e);
        }
    }

    public static PrivateKey loadPrivateKey(String pemPath) throws Exception {
    	InputStream is = GitHubJwtUtil.class.getClassLoader().getResourceAsStream(pemPath);
    	if (is == null) {
    	    throw new FileNotFoundException("Key file not found in resources");
    	}
    	try (PemReader pemReader = new PemReader(new InputStreamReader(is))) {
            byte[] pemContent = pemReader.readPemObject().getContent();
            RSAPrivateKey asn1PrivKey = RSAPrivateKey.getInstance(pemContent);

            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                asn1PrivKey.getModulus(),
                asn1PrivKey.getPublicExponent(),
                asn1PrivKey.getPrivateExponent(),
                asn1PrivKey.getPrime1(),
                asn1PrivKey.getPrime2(),
                asn1PrivKey.getExponent1(),
                asn1PrivKey.getExponent2(),
                asn1PrivKey.getCoefficient()
            );

            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }
}
