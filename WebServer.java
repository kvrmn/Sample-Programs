import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer{

  public static void main(String argv[]) throws Exception{
    int port = 3000;

  	ServerSocket blah = new ServerSocket(3000);
	System.out.println("waiting for connection");
	
	while(true){
	   Socket connect = blah.accept();
	   HttpRequest request = new HttpRequest(connect);
	   Thread thread = new Thread(request);
	   thread.start();
//	   System.out.println("connection established");
	}

  }
}

final class HttpRequest implements Runnable{
    final static String CRLF = "\r\n";
    Socket socket;
    
    public HttpRequest(Socket socket) throws Exception{
	this.socket = socket;
    }

    public void run(){
	try{
	  processRequest();
	}
	catch(Exception exception){
	  System.out.print(exception);
	  exception.printStackTrace();
	}
    }

    private void processRequest() throws Exception{
	InputStream IS = socket.getInputStream();
	DataOutputStream OS = new DataOutputStream(socket.getOutputStream());

	BufferedReader BR = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	String requestLine = BR.readLine();
	System.out.println();
	System.out.println(requestLine);

	String headerLine = null;
	while((headerLine = BR.readLine()).length()!=0){
	  System.out.println(headerLine);
	}

//	OS.close();
//	BR.close();
//	socket.close();
		
	StringTokenizer tokens = new StringTokenizer(requestLine);
	tokens.nextToken();
	String fileName = tokens.nextToken();
	fileName = "." + fileName;
	
	FileInputStream fis = null;
	boolean fileExists = true;
	try{
	  fis = new FileInputStream(fileName);
	}
	catch(FileNotFoundException exception){
	  fileExists = false;
	}

	String statusLine = null;
	String contentTypeLine = null;
	String entityBody = null;
	if(fileExists){
	   statusLine = "Status: OK, Response being generated.";
	   contentTypeLine = "Content-type: " +
          contentType(fileName) + CRLF;
	}else{
	   statusLine = "Status: Error, Response not able to be generated.";
	   contentTypeLine = "Content-type: not available";
	   entityBody = "<HTML>" + 
	 	 "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
	 	 "<BODY>Not Found</BODY></HTML>";
	
	   OS.writeBytes(statusLine);
	   OS.writeBytes(contentTypeLine);
	   OS.writeBytes(CRLF);
	
	   if(fileExists){
	     sendBytes(fis, OS);
	     fis.close();
	   }else{
	     OS.writeBytes(entityBody);
	   }
	}
	OS.close();
	socket.close();
	BR.close();
    }

   private static void sendBytes(FileInputStream fis, OutputStream OS) throws Exception{
   	byte[] buffer = new byte[1024];
	int bytes = 0;
	while((bytes = fis.read(buffer)) != -1){
	   OS.write(buffer, 0, bytes);
	}
   }

   private static String contentType(String fileName){
	if(fileName.endsWith(".htm") || fileName.endsWith(".html")){
	   return "text/html";
	}
	if(fileName.endsWith(".gif")){
	   return "image/gif";
	}
	if(fileName.endsWith(".jpeg")){
	   return "image/jpeg";
	}
	return "application/octet-stream";
   }

}
