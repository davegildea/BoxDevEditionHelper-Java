package com.chadburnette.box_dev_edition_helper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

//Make sure you install the unrestricted encryption libraries for your JVM (if you don't you'll get an exception about key length):
//http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html

public class BoxDevEditionHelper {

	private static final String AUTH_URL = "https://api.box.com/oauth2/token";
	private static final String USERS_URL = "https://api.box.com/2.0/users";
	private static final String FILES_URL = "https://api.box.com/2.0/files";
	private static final String JWT_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
	
	private String clientId;
	private String clientSecret;
	private String enterpriseId;
	private PrivateKey pkey;
	
	public BoxDevEditionHelper(String enterpriseId, String clientId, String clientSecret, String privateKey, String privateKeyPassword) throws IOException{
		this.enterpriseId = enterpriseId;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		
		this.pkey = readPrivateKey(privateKey, privateKeyPassword);
	}
	
	public String getEnterpriseToken() throws JoseException, IOException, UnirestException {
    	String assertion = constructJWTAssertion(this.enterpriseId, "enterprise");
    	HttpResponse<JsonNode> response = jwtAuthPost(assertion);
    	
    	String accessToken = response.getBody().getObject().getString("access_token");
		return accessToken;
    }
    
    public String getUserToken(String userId) throws JoseException, IOException, UnirestException {
    	String assertion = constructJWTAssertion(userId, "user");
    	HttpResponse<JsonNode> response = jwtAuthPost(assertion);
    	
    	String accessToken = response.getBody().getObject().getString("access_token");
		return accessToken;
    }
    
    public String createAppUser(String name, String enterpriseToken) throws UnirestException {
    	String body = "{\"name\":\"" + name + "\", \"is_platform_access_only\":true}";
    	HttpResponse<JsonNode> jsonResponse = Unirest.post(USERS_URL)
    			.header("Authorization", "Bearer " + enterpriseToken)
    			.header("accept", "application/json")
    			.body(body)
    			.asJson();
    	
    	String userId = jsonResponse.getBody().getObject().getString("id");
		return userId;
    }
    
    public void deleteAppUser(String userId, String enterpriseToken) throws UnirestException {
    	deleteAppUser(userId, enterpriseToken, true);
    }
    
    public void deleteAppUser(String userId, String enterpriseToken, boolean force) throws UnirestException {
    	String url = USERS_URL + "/" + userId;
    	Unirest.delete(url)
    			.header("Authorization", "Bearer " + enterpriseToken)
    			.header("accept", "application/json")
    			.field("force", force ? "true" : "false")
    			.asJson();
    }
    
    public static String downloadUrl(String fileId, String token) throws UnirestException{
    	HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    	CloseableHttpClient client = clientBuilder.disableRedirectHandling().build();
    	
    	Unirest.setHttpClient(client);
    	
    	String url = FILES_URL + "/" + fileId + "/content";
    	HttpResponse<String> response = Unirest.get(url)
    			.header("Authorization", "Bearer " + token)
    			.header("accept", "application/json")
    			.asString();
    	
    	String downloadUrl = response.getHeaders().get("location").get(0);  
    	
    	clientBuilder = HttpClientBuilder.create();
    	client = clientBuilder.build();
    	Unirest.setHttpClient(client);
    	
    	return downloadUrl;
    }
    
    private HttpResponse<JsonNode> jwtAuthPost(String assertion) throws UnirestException {
    	HttpResponse<JsonNode> jsonResponse = Unirest.post(AUTH_URL)
				  .header("accept", "application/json")
				  .field("grant_type", JWT_GRANT_TYPE)
				  .field("client_id", this.clientId)
				  .field("client_secret", this.clientSecret)
				  .field("assertion", assertion)
				  .asJson();
    	
    	return jsonResponse;
    }
    
    private String constructJWTAssertion(String sub, String boxSubType) throws JoseException, IOException {

    	JwtClaims claims = new JwtClaims();
    	claims.setIssuer(this.clientId);
    	claims.setAudience(AUTH_URL);
    	claims.setExpirationTimeMinutesInTheFuture(1);
    	claims.setSubject(sub);
    	claims.setClaim("box_sub_type", boxSubType);
    	claims.setGeneratedJwtId(64);
    	
    	JsonWebSignature jws = new JsonWebSignature();
    	jws.setPayload(claims.toJson());
    	jws.setKey(this.pkey);
    	jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    	
    	String assertion = jws.getCompactSerialization();
    	
    	return assertion;
    }
    
    private PrivateKey readPrivateKey(String privateKey, String privateKeyPassword) throws IOException {
    	
        Reader r = new StringReader(privateKey);
        PEMParser keyReader = new PEMParser(r);

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PEMDecryptorProvider decryptionProv = new JcePEMDecryptorProviderBuilder().build(privateKeyPassword.toCharArray());

        Object keyPair = keyReader.readObject();
        PrivateKeyInfo keyInfo;

        if (keyPair instanceof PEMEncryptedKeyPair) {
            PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) keyPair).decryptKeyPair(decryptionProv);
            keyInfo = decryptedKeyPair.getPrivateKeyInfo();
        } else {
            keyInfo = ((PEMKeyPair) keyPair).getPrivateKeyInfo();
        }

        keyReader.close();
        return converter.getPrivateKey(keyInfo);
    }
}
