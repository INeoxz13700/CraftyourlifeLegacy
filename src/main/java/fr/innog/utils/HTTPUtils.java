package fr.innog.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPUtils {

	public static String doPostHttps(String url, String postData) {
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    try {
	    	
	    	SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
	        SSLContext.setDefault(ctx);
	    	
	        URL realUrl = new URL(url);

	        // build connection
	        HttpsURLConnection conn = (HttpsURLConnection) realUrl.openConnection();
	        conn.setConnectTimeout(500);
	        conn.setReadTimeout(500);
	        
	        conn.setHostnameVerifier(new HostnameVerifier() {
	            @Override
	            public boolean verify(String arg0, SSLSession arg1) {
	                return true;
	            }
	        });
	        
	        // set request properties
	        conn.setRequestProperty("accept", "*/*");
	        conn.setRequestProperty("connection", "Keep-Alive");
	        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
	        // enable output and input
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        

	        
	        out = new PrintWriter(conn.getOutputStream());
	        // send POST DATA
	        out.print(postData);
	        out.flush();
	        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        while ((line = in.readLine()) != null) {
	            result += line;
	        }
	        conn.disconnect();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        try {
	            if (out != null) {
	                out.close();
	            }
	            if (in != null) {
	                in.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return result;
	}	
	
	public static String doPostHttp(String url, String postData) {
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    try {
	    	
	    	    	
	        URL realUrl = new URL(url);
	        // build connection
	        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
	        
	       
	        
	        // set request properties
	        conn.setRequestProperty("accept", "*/*");
	        conn.setRequestProperty("connection", "Keep-Alive");
	        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
	        // enable output and input
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        
	        
	        out = new PrintWriter(conn.getOutputStream());
	        // send POST DATA
	        out.print(postData);
	        out.flush();
	        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        while ((line = in.readLine()) != null) {
	            result += line;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        try {
	            if (out != null) {
	                out.close();
	            }
	            if (in != null) {
	                in.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return result;
	}	
	
	public static String doUserPostHttp(String basicToken, String url, String postData) {
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    try {
	    	
	    	    	
	        URL realUrl = new URL(url);
	        // build connection
	        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
	        
	       
	        
	        // set request properties
	        conn.setRequestProperty("Authorization", "Basic " + basicToken);
	        conn.setRequestProperty("accept", "*/*");
	        conn.setRequestProperty("connection", "Keep-Alive");
	        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
	        // enable output and input
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        
	        
	        out = new PrintWriter(conn.getOutputStream());
	        // send POST DATA
	        out.print(postData);
	        out.flush();
	        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        while ((line = in.readLine()) != null) {
	            result += line;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        try {
	            if (out != null) {
	                out.close();
	            }
	            if (in != null) {
	                in.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return result;
	}
	
	public static String sendGETHttps(String url) throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
        SSLContext.setDefault(ctx);
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			con.disconnect();
			in.close();

			// print result
			return response.toString();
		} else {
			System.out.println("Error");
		}
		
		return "";
	}
	
	public static String readFileFromUrl(String fileUrl)
	{
		try {

            // Ouvrir une connexion vers l'URL
            URL url = new URL(fileUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

            // Lire le fichier ligne par ligne
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();

            return content.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
		return "";
	}
	
    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    
    public static String sendFilePost(String url,File toSend, String params) throws IOException
    {
    	MultipartUtility multipart = new MultipartUtility(url, "UTF-8", null);
    	
        for(String param : params.split("&")) {
        	String key = param.split("=")[0];
        	String value = param.split("=")[1];
        	multipart.addFormField(key, value);
        }
        
        multipart.addFilePart("skin", toSend);
        List<String> responses = multipart.finish();
        StringBuilder builder = new StringBuilder();
        for(String r : responses) {
        	builder.append(r);
        }

        return builder.toString();
    }
    
    public static String sendUserFilePost(String basicToken, String url, File toSend, String params) throws IOException
    {
    	Map<String, String> properties = new HashMap<>();
    	properties.put("Authorization", "Basic " + basicToken);
    	properties.put("accept", "*/*");
    	properties.put("connection", "Keep-Alive");
    	properties.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		MultipartUtility multipart = new MultipartUtility(url, "UTF-8", properties);
	
		
	    for(String param : params.split("&"))
	    {
	    	String key = param.split("=")[0];
	    	String value = param.split("=")[1];
	    	multipart.addFormField(key, value);
	    }
	    
	    multipart.addFilePart("skin", toSend);
	    List<String> responses = multipart.finish();
	    StringBuilder builder = new StringBuilder();
	    for(String r : responses) {
	    	builder.append(r);
	    }
	
	    return builder.toString();
    }
    
    public static String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }
    
    private static ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }
	
}

class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
 
    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset, Map<String, String> properties) throws IOException
    {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
         
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        
        if(properties != null)
        {
            for(Entry<String, String> property : properties.entrySet()) {
            	httpConn.setRequestProperty(property.getKey(), property.getValue());
            }
        }

        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
        
    }
    
    
    public HttpURLConnection getCon()
    {
    	return this.httpConn;
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
     
    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
 
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
 
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
 
        return response;
    }
}