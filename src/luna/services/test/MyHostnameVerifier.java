package luna.services.test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * ʵ��������������֤�Ļ�ӿڡ� 
 * �������ڼ䣬��� URL ��������ͷ������ı�ʶ������ƥ�䣬����֤���ƿ��Իص��˽ӿڵ�ʵ�ֳ�����ȷ���Ƿ�Ӧ����������ӡ�
 */
public class MyHostnameVerifier implements HostnameVerifier {
	public boolean verify(String hostname, SSLSession session) {
		if("localhost".equals(hostname)){
			return true;
		} else {
			return false;
		}
	}
}