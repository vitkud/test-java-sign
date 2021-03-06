/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package test.java.sign;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.TreeSet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Library {

	public static final int KEY_LENGTH = 2048;

	public Library() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public KeyPair generateKeyPairRSA(int keyLength) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstanceStrong();
		keyPairGenerator.initialize(keyLength, random);
		return keyPairGenerator.generateKeyPair();
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		return generateKeyPairRSA(KEY_LENGTH);
	}

//	private Signature getSignature() throws Exception {
//		return Signature.getInstance("SHA256withRSA");
//	}

//	// BouncyCastle signature
//	// BC: Signature.RSASSA-PSS -> org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA
//	//  aliases: [RSAPSS]
//	private Signature getSignatureBc() throws Exception {
//		Signature signature = Signature.getInstance("RSASSA-PSS", "BC");
//		// Default: signature.setParameter(new PSSParameterSpec("SHA1", "MGF1", MGF1ParameterSpec.SHA1, 20, 1));
//		signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
//		return signature;
//	}

	// BouncyCastle signature
	// BC: Signature.SHA256WITHRSAANDMGF1 -> org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSA
	//  aliases: [SHA256withRSA/PSS, SHA256WithRSA/PSS, SHA256withRSAandMGF1, SHA256WithRSAAndMGF1]
	private Signature getSignatureBc2() throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA/PSS", "BC");
		// Default: signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
		return signature;
	}

	// openjdk-8u282
	// SunRsaSign: Signature.RSASSA-PSS -> sun.security.rsa.RSAPSSSignature
	//  aliases: [1.2.840.113549.1.1.10, OID.1.2.840.113549.1.1.10]
	private Signature getSignatureOjdk() throws Exception {
		Signature signature = Signature.getInstance("RSASSA-PSS", "SunRsaSign");
		// sun.security.rsa.RSAPSSSignature (aliases: [1.2.840.113549.1.1.10, OID.1.2.840.113549.1.1.10])
		// Parameters required for RSASSA-PSS signatures
		//signature.setParameter(PSSParameterSpec.DEFAULT);
		//signature.setParameter(new PSSParameterSpec("SHA1", "MGF1", MGF1ParameterSpec.SHA1, 20, 1));
		signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
		return signature;
	}

//	// openjdk-8u282
//	// SunMSCAPI: Signature.RSASSA-PSS -> sun.security.mscapi.CSignature$PSS
//	//  aliases: [1.2.840.113549.1.1.10, OID.1.2.840.113549.1.1.10]
//	// !!! java.security.InvalidKeyException: Key type not supported: class sun.security.rsa.RSAPrivateCrtKeyImpl RSA
//	private Signature getSignatureOjdk2() throws Exception {
//		Signature signature = Signature.getInstance("RSASSA-PSS", "SunMSCAPI");
//		signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
//		return signature;
//	}

	public byte[] sign(String message, PrivateKey privateKey) throws Exception {
		Signature signature = getSignatureOjdk();
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
		signature.initSign(privateKey, secureRandom);
		signature.update(message.getBytes(StandardCharsets.UTF_8));
		return signature.sign();
	}

	public boolean verify(String message, PublicKey publicKey, byte[] sign) throws Exception {
		Signature signature = getSignatureBc2();
		signature.initVerify(publicKey);
		signature.update(message.getBytes(StandardCharsets.UTF_8));
		return signature.verify(sign);
	}

	public static void printAlgorithms() {
		Security.addProvider(new BouncyCastleProvider());
		TreeSet<String> algorithms = new TreeSet<String>();
		for (Provider provider : Security.getProviders()) {
			System.out.println("----------------------------------------");
			System.out.println("Provider: " + provider);
			System.out.println("----------------------------------------");
			for (Service service : provider.getServices()) {
				if (service.getType().equals("Signature")) {
					System.out.println("Service: " + service);
					algorithms.add(service.getAlgorithm());
				}
			}
		}
		System.out.println("----------------------------------------");
		for (String algorithm : algorithms)
			System.out.println(algorithm);
	}
	
    public static void main(String[] args) {
    	printAlgorithms();
	}
}
