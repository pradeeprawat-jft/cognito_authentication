package com.psr.awscognitowithoutoauth.service;
import com.psr.awscognitowithoutoauth.exceptionHandler.MyAuthenticationExceptionHandler;
import org.apache.tomcat.websocket.AuthenticationException;
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
    public Boolean signup(String username, String email, String password) throws AuthenticationException {
        String secretHash = calculateSecretHash(username, clientId, clientSecret);
        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .clientId(clientId)
                .username(username)
                .password(password)
                .userAttributes(emailAttribute)
                .secretHash(secretHash)
                .build();
        try {
           SignUpResponse response = cognitoClient.signUp(signUpRequest);
           return response.userConfirmed();
        } catch (Exception e) {
            throw new MyAuthenticationExceptionHandler("User signup failed: " + e.getMessage());
        }
    }

    public CognitoIdentityProviderResponseMetadata confirmUserSignUp(String username, String confirmationCode)  {
        String secretHash = calculateSecretHash(username, clientId, clientSecret);
        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .username(username)
                .confirmationCode(confirmationCode)
                .secretHash(secretHash)
                .build();

        try {
            ConfirmSignUpResponse confirmResponse = cognitoClient.confirmSignUp(confirmSignUpRequest);
            return confirmResponse.responseMetadata();
        } catch (Exception e) {
            throw new MyAuthenticationExceptionHandler("User confirmation failed: " + e.getMessage());
        }
    }

    public AuthenticationResultType login(String username, String password) throws AuthenticationException {
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
            throw new MyAuthenticationExceptionHandler("User SignIn failed: " + e.getMessage());
        }
    }

    public void logout(String accessToken) {
        GlobalSignOutRequest signOutRequest = GlobalSignOutRequest.builder()
                .accessToken(accessToken)
                .build();
        try {
            cognitoClient.globalSignOut(signOutRequest);
        } catch (Exception e) {
            throw new MyAuthenticationExceptionHandler("User logout failed: " + e.getMessage());
        }
    }
}

