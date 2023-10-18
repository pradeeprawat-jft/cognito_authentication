package com.psr.awscognitowithoutoauth.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.utils.ImmutableMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class CognitoService {
    private final CognitoIdentityProviderClient cognitoClient;
    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;
    @Value("${aws.cognito.clientId}")
    private String clientId;
    @Value("${aws.cognito.clientSecret}")
    private String clientSecret;

    public CognitoService(@Value("${aws.cognito.region}") String region) {
        cognitoClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Region.of(region))
                .build();
    }

    public static String calculateSecretHash(String userName, String clientId, String clientSecret) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(clientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error calculating secret hash", e);
        }
    }

    public void signup(String username,String email, String password) {
        String secretHash = calculateSecretHash(username, clientId, clientSecret);
        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .clientId(clientId)
                .username(username)
                .password(password)
                .userAttributes(emailAttribute)
                .clientMetadata(ImmutableMap.of("SECRET_HASH", secretHash))
                .build();
        try {
            cognitoClient.signUp(signUpRequest);
        } catch (Exception e) {
            throw new RuntimeException("User signup failed: " + e.getMessage());
        }
    }

    public AuthenticationResultType login(String username, String password) {
        String secretHash = calculateSecretHash(username, clientId, clientSecret);

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(clientId)
                .authParameters(ImmutableMap.of(
                        "USERNAME", username,
                        "PASSWORD", password,
                        "SECRET_HASH", secretHash))
                .build();

        try {
            InitiateAuthResponse initiateAuthResponse = cognitoClient.initiateAuth(authRequest);
            return initiateAuthResponse.authenticationResult();
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}

