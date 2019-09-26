package com.meizu.lichee.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpScratcher {

	private static final Logger logger = LoggerFactory.getLogger( HttpScratcher.class );

	private static final String CONTENT_CHARSET = "UTF-8";

	private HttpClient client = null;

	public HttpScratcher() {
		this( 3, 3, 20, 100 );
	}

	public HttpScratcher( int socketTimeout, int connectionTimeout, final int maxConnectionsPerRoute,
			int maxTotalConnections ) {
		try {
			// Instance of this interface manage which X509 certificates may be
			// used to
			// authenticate the remote side of a secure socket.
			// Decisions may be based on trusted certificate authorities,
			// certificate revocation lists, online status checking or other
			// means.
			TrustManager dummyTrustManager = new X509TrustManager() {

				@Override
				public void checkClientTrusted( X509Certificate[] chain, String authType )
						throws CertificateException {
					// Oh, I am easy!
				}

				@Override
				public void checkServerTrusted( X509Certificate[] chain, String authType )
						throws CertificateException {
					// Oh, I am easy!
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			// Instances of this class represent a secure socket protocol
			// implementation
			// which acts as a factory for secure socket factories or
			// SSLEngines.
			// This class is initialized with an optional set of key
			// and trust managers and source of secure random bytes.
			SSLContext sslcontext = SSLContext.getInstance( SSLSocketFactory.TLS );
			sslcontext.init( null, new TrustManager[] { dummyTrustManager }, null );

			// SSLSocketFactory can be used to validate the identity of the
			// HTTPS server against a list of
			// trusted certificates and to authenticate to the HTTPS server
			// using a private key.
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory( sslcontext,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );

			// A set of supported protocol Schemes. Schemes are identified by
			// lowercase names.
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register( new Scheme( "http", 80, PlainSocketFactory.getSocketFactory() ) );
			schemeRegistry.register( new Scheme( "https", 443, sslSocketFactory ) );

			PoolingClientConnectionManager connManager = new PoolingClientConnectionManager(
					schemeRegistry );
			connManager.setDefaultMaxPerRoute( maxConnectionsPerRoute );
			connManager.setMaxTotal( maxTotalConnections );

			HttpParams clientParams = new BasicHttpParams();

			HttpProtocolParams.setVersion( clientParams, HttpVersion.HTTP_1_1 );
			HttpProtocolParams.setUserAgent( clientParams, "Meizu Http Client" );
			HttpProtocolParams.setUseExpectContinue( clientParams, true );

			HttpConnectionParams.setConnectionTimeout( clientParams, connectionTimeout * 1000 );
			HttpConnectionParams.setSoTimeout( clientParams, socketTimeout * 1000 );
			HttpConnectionParams.setSocketBufferSize( clientParams, 8192 );
			HttpConnectionParams.setTcpNoDelay( clientParams, true );
			HttpProtocolParams.setContentCharset( clientParams, CONTENT_CHARSET );

			client = new DefaultHttpClient( connManager, clientParams );
			if( logger.isDebugEnabled() ) {
				logger.debug( "++++ initializing pool with following settings:" );
				logger.debug( "++++ maxConnectionsPerRoute : " + maxConnectionsPerRoute );
				logger.debug( "++++ maxTotalConnections    : " + maxTotalConnections );
				logger.debug( "++++ httpVersion            : " + HttpVersion.HTTP_1_1 );
				logger.debug( "++++ contentCharset         : " + CONTENT_CHARSET );
				logger.debug( "++++ connectionTimeout      : " + connectionTimeout );
				logger.debug( "++++ socketTimeout          : " + socketTimeout );
				logger.debug( "++++ socketBufferSize       : " + 8192 );
			}
		} catch( Exception ex ) {
			throw new RuntimeException( "Init meizu http client error.", ex );
		}
	}
	
	

	public HttpScratcher( int socketTimeout, int connectionTimeout, final int maxConnectionsPerRoute,
			int maxTotalConnections, int connectionManagerTimeout ) {
		this(socketTimeout, connectionTimeout, maxConnectionsPerRoute, maxTotalConnections, connectionManagerTimeout, true);
	}

	public HttpScratcher( int socketTimeout, int connectionTimeout, final int maxConnectionsPerRoute,
						  int maxTotalConnections, int connectionManagerTimeout, boolean useContinue ) {
		try {
			// Instance of this interface manage which X509 certificates may be
			// used to
			// authenticate the remote side of a secure socket.
			// Decisions may be based on trusted certificate authorities,
			// certificate revocation lists, online status checking or other
			// means.
			TrustManager dummyTrustManager = new X509TrustManager() {

				@Override
				public void checkClientTrusted( X509Certificate[] chain, String authType )
						throws CertificateException {
					// Oh, I am easy!
				}

				@Override
				public void checkServerTrusted( X509Certificate[] chain, String authType )
						throws CertificateException {
					// Oh, I am easy!
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			// Instances of this class represent a secure socket protocol
			// implementation
			// which acts as a factory for secure socket factories or
			// SSLEngines.
			// This class is initialized with an optional set of key
			// and trust managers and source of secure random bytes.
			SSLContext sslcontext = SSLContext.getInstance( SSLSocketFactory.TLS );
			sslcontext.init( null, new TrustManager[] { dummyTrustManager }, null );

			// SSLSocketFactory can be used to validate the identity of the
			// HTTPS server against a list of
			// trusted certificates and to authenticate to the HTTPS server
			// using a private key.
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory( sslcontext,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );

			// A set of supported protocol Schemes. Schemes are identified by
			// lowercase names.
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register( new Scheme( "http", 80, PlainSocketFactory.getSocketFactory() ) );
			schemeRegistry.register( new Scheme( "https", 443, sslSocketFactory ) );

			PoolingClientConnectionManager connManager = new PoolingClientConnectionManager(
					schemeRegistry );
			connManager.setDefaultMaxPerRoute( maxConnectionsPerRoute );
			connManager.setMaxTotal( maxTotalConnections );

			HttpParams clientParams = new BasicHttpParams();

			HttpProtocolParams.setVersion( clientParams, HttpVersion.HTTP_1_1 );
			HttpProtocolParams.setUserAgent( clientParams, "Meizu Http Client" );
			if(useContinue){
				HttpProtocolParams.setUseExpectContinue( clientParams, true );
			}

			HttpConnectionParams.setConnectionTimeout( clientParams, connectionTimeout );
			HttpConnectionParams.setSoTimeout( clientParams, socketTimeout );
			HttpConnectionParams.setSocketBufferSize( clientParams, 8192 );
			HttpConnectionParams.setTcpNoDelay( clientParams, true );
			HttpProtocolParams.setContentCharset( clientParams, CONTENT_CHARSET );
			HttpConnectionParams.setStaleCheckingEnabled( clientParams, true );

			// 此处解释下MaxtTotal和DefaultMaxPerRoute的区别：
			// 1、MaxtTotal是整个池子的大小；
			// 2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
			// MaxtTotal=400 DefaultMaxPerRoute=200
			// 而我只连接到http://sishuok.com时，到这个主机的并发最多只有200；而不是400；
			// 而我连接到http://sishuok.com 和 http://qq.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
			clientParams.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, connectionManagerTimeout);

			client = new DecompressingHttpClient(new DefaultHttpClient( connManager, clientParams ));
			if( logger.isDebugEnabled() ) {
				logger.debug( "++++ initializing pool with following settings:" );
				logger.debug( "++++ maxConnectionsPerRoute : " + maxConnectionsPerRoute );
				logger.debug( "++++ maxTotalConnections    : " + maxTotalConnections );
				logger.debug( "++++ httpVersion            : " + HttpVersion.HTTP_1_1 );
				logger.debug( "++++ contentCharset         : " + CONTENT_CHARSET );
				logger.debug( "++++ connectionTimeout      : " + connectionTimeout );
				logger.debug( "++++ socketTimeout          : " + socketTimeout );
				logger.debug( "++++ socketBufferSize       : " + 8192 );
			}
		} catch( Exception ex ) {
			throw new RuntimeException( "Init meizu http client error.", ex );
		}
	}

	public <T> T doHttpRequest( String methodName, String url, Map<String, String> header,
			byte[] data, ResponseHandler<? extends T> responseHandler )
					throws Exception {
		if( logger.isDebugEnabled() ) {
			logger.debug( String.format( "Receive [%s] reqeust: %s", methodName, url ) );
		}
		if( StringUtils.isBlank( url ) ) {
			throw new HttpResponseException( HttpURLConnection.HTTP_BAD_REQUEST,
					"Parameter url is empty." );
		}
		HttpUriRequest request = null;
		if( HttpPost.METHOD_NAME.equalsIgnoreCase( methodName ) ) {
			request = new HttpPost( url );
		} else
			if( HttpGet.METHOD_NAME.equalsIgnoreCase( methodName ) ) {
			request = new HttpGet( url );
		} else {
			throw new HttpResponseException( HttpURLConnection.HTTP_BAD_REQUEST,
					String.format( "Http method[%s] be not supported.", methodName ) );
		}
		if( null != header && !header.isEmpty() ) {
			if( logger.isDebugEnabled() ) {
				logger.debug( String.format( "Set request header[%s] for %s.", header, url ) );
			}
			for( Entry<String, String> item : header.entrySet() ) {
				request.setHeader( item.getKey(), item.getValue() );
			}
		}
		if( null != data && request instanceof HttpPost ) {
			( ( HttpPost )request ).setEntity( new ByteArrayEntity( data ) );
		}
		T response = null;
		try {
			response = client.execute( request, responseHandler );
		} catch( Throwable ex ) {
			request.abort();
			throw( Exception )ex;
		}
		if( logger.isDebugEnabled() ) {
			logger.debug( String.format( "Transfer [%s] reqeust[%s] successfull.", methodName, url ) );
		}
		return response;
	}

	public String doHttpPost( String url, Map<String, String> header, String data ) throws Exception {
		return doHttpRequest( HttpPost.METHOD_NAME, url, header, data.getBytes( CONTENT_CHARSET ),
				new BasicResponseHandler() );
	}
	
	public byte[] doRawHttpPost(String url, Map<String, String> header, String data) throws Exception {
		return doHttpRequest(HttpPost.METHOD_NAME, url, header, data.getBytes(CONTENT_CHARSET),
				new RawResponseHandler());
	}

	public String doHttpPost( String url, Map<String, String> header, byte[] data ) throws Exception {
		return doHttpRequest( HttpPost.METHOD_NAME, url, header, data, new BasicResponseHandler() );
	}

	public String doHttpGet( String url, Map<String, String> header ) throws Exception {
		return doHttpRequest( HttpGet.METHOD_NAME, url, header, null, new BasicResponseHandler() );
	}

	public String doHttpGet( String url, Map<String, String> header, String charSet )
			throws Exception {
		return doHttpRequest( HttpGet.METHOD_NAME, url, header, null,
				new CharSetResponseHandler( charSet ) );
	}

	/**
	 * When HttpClient instance is no longer needed, shut down the connection
	 * manager to ensure immediate deallocation of all system resources
	 */
	public void destroy() {
		if( null != client ) {
			client.getConnectionManager().shutdown();
		}
	}

	// 定义一个可以设置Charset的responseHandler内部类，用于处理返回不同编码的内容
	private class CharSetResponseHandler implements ResponseHandler<String> {

		private String charSet;

		public CharSetResponseHandler( String charSet ) {
			this.charSet = charSet;
		}

		public String handleResponse( final HttpResponse response )
				throws HttpResponseException, IOException {
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if( statusLine.getStatusCode() >= 300 ) {
				EntityUtils.consume( entity );
				throw new HttpResponseException( statusLine.getStatusCode(),
						statusLine.getReasonPhrase() );
			}
			return entity == null ? null : EntityUtils.toString( entity, charSet );
		}
	}
	
	private class RawResponseHandler implements ResponseHandler<byte[]> {

		public byte[] handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= 300) {
				EntityUtils.consume(entity);
				throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
			}
			return entity == null ? null : toByteArray(entity);
		}

		public byte[] toByteArray(HttpEntity entity) throws IOException {
			if (entity == null) {
				throw new IllegalArgumentException("HTTP entity may not be null");
			}
			InputStream entityContent = entity.getContent();
			if (entityContent == null) {
				return null;
			}
			try {
				if (entity.getContentLength() > Integer.MAX_VALUE) {
					throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
				}
				int i = (int) entity.getContentLength();
				if (i < 0) {
					i = 4096;
				}
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(i);
				byte[] buffer = new byte[1024];
				int read = -1;
				while ((read = entityContent.read(buffer)) != -1) {
					outputStream.write(buffer, 0, read);
				}
				return outputStream.toByteArray();
			} finally {
				entityContent.close();
			}
		}
	}

}
