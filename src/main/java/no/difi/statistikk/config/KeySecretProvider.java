package no.difi.statistikk.config;

import lombok.Getter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;

/**
 * Loads keys from disk..
 */
@Getter
public class KeySecretProvider {

    private final Certificate certificate;
    private final RSAPrivateKey privateKey;

    private KeySecretProvider(RSAPrivateKey privateKey, Certificate certificate) {
        this.privateKey = Objects.requireNonNull(privateKey);
        this.certificate = Objects.requireNonNull(certificate);
    }

    public static KeySecretProvider from(MaskinportenProperties.KeyProperties properties) {
//        return new KeySecretProvider(loadKeyStore(Objects.requireNonNull(properties)));
        Objects.requireNonNull(properties);
        return new KeySecretProvider(readPrivateKey(properties.getPrivateKey()), readCertificate(properties.getCertificate()));
    }

    public static Certificate readCertificate(Resource file) {
        CertificateFactory certFactory;
        try {
            certFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException("Failed to create certFactory for read X.509 certificate for Maskinporten from Docker secrets: " + file.getFilename(), e);
        }

        X509Certificate cer;
        try {
            InputStream stream = file.getInputStream();
            cer = (X509Certificate) certFactory.generateCertificate(stream);
        } catch (IOException | CertificateException e) {
            throw new RuntimeException("Failed to read certificate for Maskinporten from Docker secrets: " + file.getFilename(), e);
        }

        return cer;
    }

//    public static RSAPublicKey readPublicKey(Resource file) {
//        String keyAsText;
//        try {
//            final File keyFile = file.getFile();
//            keyAsText = new String(Files.readAllBytes(keyFile.toPath()), Charset.defaultCharset());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read public key for Maskinporten from Docker secrets: " + file.getFilename(), e);
//        }
//
//        String publicKeyPEM = keyAsText
//                .replace("-----BEGIN PUBLIC KEY-----", "")
//                .replaceAll(System.lineSeparator(), "")
//                .replace("-----END PUBLIC KEY-----", "");
//
//        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
//
//
//        final PublicKey publicKey;
//        try {
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
//            publicKey = keyFactory.generatePublic(keySpec);
//        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
//            throw new RuntimeException("Failed to read public key for Maskinporten from Docker secrets: " + file.getFilename(), e);
//        }
//        return (RSAPublicKey) publicKey;
//    }


    public static RSAPrivateKey readPrivateKey(Resource file) {
        String keyAsText;
        try {
            final File keyFile = file.getFile();
            keyAsText = new String(Files.readAllBytes(keyFile.toPath()), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read private key for Maskinporten from Docker secrets: " + file.getFilename(), e);
        }

        String privateKeyPEM = keyAsText
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        final PrivateKey privateKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to read private key for Maskinporten from Docker secrets: " + file.getFilename(), e);
        }
        return (RSAPrivateKey) privateKey;
    }

}
