package hol2eih4;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("webClient")
public class WebClient {
	private static final Logger logger = LoggerFactory.getLogger(WebClient.class);
	ObjectMapper mapper = new ObjectMapper();
	void test(){
		Map<String, Object> drug = new HashMap<>();
		String url = "http://hol.curepathway.com/pushedWebNewDrug";
		pushMapToUrl(url, drug);
	}

	Map<String, Object> pushMapToUrl(String url, Map<String, Object> mapToPost) {
		logger.debug(url);
		HttpURLConnection con = sendToUrlPerPost(mapToPost, url);
		logger.debug(""+con);
		Map<String, Object>  mapResponsed = null;
		if(null != con){
			try {
				InputStream requestBody = con.getInputStream();
				mapResponsed = mapper.readValue(requestBody, Map.class);
				logger.debug(""+mapResponsed);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mapResponsed;
	}

	private HttpURLConnection sendToUrlPerPost(Map<String, Object> map, String url) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json"); 
			con.setRequestProperty("charset", "utf-8");
			mapper.writeValue(con.getOutputStream(), map);
			return con;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
