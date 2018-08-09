package luna.services.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HttpsPost {
	/**
	 * ���KeyStore.
	 * 
	 * @param keyStorePath
	 *            ��Կ��·��
	 * @param password
	 *            ����
	 * @return ��Կ��
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String password, String keyStorePath)
			throws Exception {
		// ʵ����Կ��
		KeyStore ks = KeyStore.getInstance("JKS");
		// �����Կ���ļ���
		FileInputStream is = new FileInputStream(keyStorePath);
		// ������Կ��
		ks.load(is, password.toCharArray());
		// �ر���Կ���ļ���
		is.close();
		return ks;
	}

	/**
	 * ���SSLSocketFactory.
	 * 
	 * @param password
	 *            ����
	 * @param keyStorePath
	 *            ��Կ��·��
	 * @param trustStorePath
	 *            ���ο�·��
	 * @return SSLSocketFactory
	 * @throws Exception
	 */
	public static SSLContext getSSLContext(String password,
			String keyStorePath, String trustStorePath) throws Exception {
		// ʵ����Կ��
		KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// �����Կ��
		KeyStore keyStore = getKeyStore(password, keyStorePath);
		// ��ʼ����Կ����
		keyManagerFactory.init(keyStore, password.toCharArray());

		// ʵ�����ο�
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// ������ο�
		KeyStore trustStore = getKeyStore(password, trustStorePath);
		// ��ʼ�����ο�
		trustManagerFactory.init(trustStore);
		// ʵ��SSL������
		SSLContext ctx = SSLContext.getInstance("TLS");
		// ��ʼ��SSL������
		ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory
				.getTrustManagers(), null);
		// ���SSLSocketFactory
		return ctx;
	}

	/**
	 * ��ʼ��HttpsURLConnection.
	 * 
	 * @param password
	 *            ����
	 * @param keyStorePath
	 *            ��Կ��·��
	 * @param trustStorePath
	 *            ���ο�·��
	 * @throws Exception
	 */
	public static void initHttpsURLConnection(String password,
			String keyStorePath, String trustStorePath) throws Exception {
		// ����SSL������
		SSLContext sslContext = null;
		// ʵ����������֤�ӿ�
		HostnameVerifier hnv = new MyHostnameVerifier();
		try {
			sslContext = getSSLContext(password, keyStorePath, trustStorePath);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	/**
	 * ��������.
	 * 
	 * @param httpsUrl
	 *            ����ĵ�ַ
	 * @param xmlStr
	 *            ��������
	 */
	public static void post(String httpsUrl, String xmlStr) {
		HttpsURLConnection urlCon = null;
		try {
			urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			urlCon.setRequestMethod("POST");
			urlCon.setRequestProperty("Content-Length", String.valueOf(xmlStr
					.getBytes().length));
			urlCon.setUseCaches(false);
			// ����Ϊgbk���Խ������������ʱ��ȡ�����������������
			urlCon.getOutputStream().write(xmlStr.getBytes("gbk"));
			urlCon.getOutputStream().flush();
			urlCon.getOutputStream().close();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon
					.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���Է���.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// ����
		String password = "lunabox";
		// ��Կ��
		String keyStorePath = "tomcat_key.keystore";
		// ���ο�
		String trustStorePath = "tomcat_key.keystore";
		// �������https����
		String httpsUrl = "https://localhost:8443/webserdemo/services/httpsPost";
		// �����ı�
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruitShop><fruits><fruit><kind>�ܲ�</kind></fruit><fruit><kind>����</kind></fruit></fruits></fruitShop>";
		HttpsPost
				.initHttpsURLConnection(password, keyStorePath, trustStorePath);
		// ��������
		HttpsPost.post(httpsUrl, xmlStr);
	}
}