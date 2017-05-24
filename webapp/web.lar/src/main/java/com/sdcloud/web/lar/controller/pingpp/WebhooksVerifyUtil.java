package com.sdcloud.web.lar.controller.pingpp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* @author jzc
* @version 2016年6月27日 上午11:06:25
* WebhooksVerifyUtil描述:
* 该实例演示如何对 Ping++ webhooks 通知进行验证。
* 验证是为了让开发者确认该通知来自 Ping++ ，防止恶意伪造通知。用户如果有别的验证机制，可以不进行验证签名。
* 验证签名需要 签名、公钥、验证信息，该实例采用文件存储方式进行演示。
* 实际项目中，需要用户从异步通知的 HTTP header 中读取签名，从 HTTP body 中读取验证信息。公钥的存储方式也需要用户自行设定。
* 
*/
public class WebhooksVerifyUtil {
	
	private static Logger logger = LoggerFactory.getLogger(WebhooksVerifyUtil.class);
	private static String pubKeyPath = "/pingpp_public_key.pem";

	/**
	 * 验证 webhooks签名
	 * @author jzc 2016年6月27日
	 * @param request
	 * @param event
	 * @return
	 */
    public static boolean webhooksVerify(String webhooksRawPostData, String signature){
        boolean result=false;
		try {
	        if(StringUtils.isNotEmpty(webhooksRawPostData)&&StringUtils.isNotEmpty(signature)){
	        	result = verifyData(webhooksRawPostData, signature, getPubKey());
	        }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
        return result;
    }
    
    /**
     * 获取http body 内容
     * @author jzc 2016年6月27日
     * @param br
     * @return
     */
    public static String getBodyString(BufferedReader br) {
	     String inputLine;
	     StringBuffer str = new StringBuffer();
	     try {
	       while ((inputLine = br.readLine()) != null) {
	           str.append(inputLine);
	       }
	       br.close();
	     } catch (IOException e) {
	       System.out.println("IOException: " + e);
	     }
	     return str.toString();
	 }
    
    /**
     * 把请求头key转换为小写
     * @author jzc 2016年6月27日
     * @param request
     * @return
     */
    public static Map<String, String> getLowerHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement().toLowerCase();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
     }

    /**
     * 读取文件, 部署 web 程序的时候, 签名和验签内容需要从 request 中获得
     * @param filePath
     * @return
     * @throws Exception
     */
    private static String getStringFromFile(String filePath) throws Exception {
        InputStream in = WebhooksVerifyUtil.class.getResourceAsStream(filePath);
        InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
        BufferedReader bf = new BufferedReader(inReader);
        StringBuilder sb = new StringBuilder();
        String line;
        do {
            line = bf.readLine();
            if (line != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        } while (line != null);

        return sb.toString();
    }

	/**
	 * 获得公钥
	 * @return
	 * @throws Exception
	 */
    private static PublicKey getPubKey() throws Exception {
		String pubKeyString = getStringFromFile(pubKeyPath);
        pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        byte[] keyBytes = Base64.decodeBase64(pubKeyString);

		// generate public key
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(spec);
		return publicKey;
	}

	/**
	 * 验证签名
	 * @param dataString
	 * @param signatureString
	 * @param publicKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
    private static boolean verifyData(String dataString, String signatureString, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        byte[] signatureBytes = Base64.decodeBase64(signatureString);
        Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		signature.update(dataString.getBytes("UTF-8"));
		return signature.verify(signatureBytes);
	}
}
