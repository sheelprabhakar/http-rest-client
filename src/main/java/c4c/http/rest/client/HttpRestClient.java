
package c4c.http.rest.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRestClient implements AutoCloseable {

	private final CloseableHttpClient httpClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private HttpRestClient(final RequestConfig config) {
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	public static HttpRestClient createDefault() {
		return new HttpRestClient(RequestConfig.DEFAULT);
	}

	public static HttpRestClient createDefault(final RequestConfig config) {
		return new HttpRestClient(config);
	}

	private String getSonString(final Object obj) throws JsonProcessingException {
		return objectMapper.writeValueAsString(obj);
	}

	private <T> T getObjectFromString(final String json, final Class<T> classType)
			throws JsonMappingException, JsonProcessingException {
		return objectMapper.readValue(json, classType);
	}

	@Override
	public void close() throws Exception {
		if (httpClient != null) {
			httpClient.close();
		}
	}

	public <T> T executeGet(final String url, final Class<T> classType) throws Throwable {
		final Map<String, String> headers = new HashMap<String, String>();
		final Map<String, String> params = new HashMap<String, String>();
		return this.executeGet(url, headers, params, classType);

	}

	public <T> T executeGet(final String url, final Map<String, String> params, final Class<T> classType)
			throws Throwable {
		final Map<String, String> headers = new HashMap<String, String>();
		return this.executeGet(url, headers, params, classType);
	}

	public <T> T executeGet(final String url, final Map<String, String> headers, final Map<String, String> params,
			final Class<T> classType) throws Throwable {
		return this.execute(HttpMethod.GET, url, headers, params, classType);

	}

	public <T> T executeDelete(final String url, final Class<T> classType) throws Throwable {
		final Map<String, String> headers = new HashMap<String, String>();
		final Map<String, String> params = new HashMap<String, String>();
		return this.executeDelete(url, headers, params, classType);

	}

	public <T> T executeDelete(final String url, final Map<String, String> params, final Class<T> classType)
			throws Throwable {
		final Map<String, String> headers = new HashMap<String, String>();
		return this.executeDelete(url, headers, params, classType);
	}

	public <T> T executeDelete(final String url, final Map<String, String> headers, final Map<String, String> params,
			final Class<T> classType) throws Throwable {
		return this.execute(HttpMethod.DELETE, url, headers, params, classType);

	}

	private <T> T execute(final HttpMethod method, final String url, final Map<String, String> headers,
			final Map<String, String> params, final Class<T> classType) throws Throwable {
		URIBuilder builder = new URIBuilder(url);
		if (params != null) {
			for (final Entry<String, String> elm : params.entrySet()) {
				builder = builder.setParameter(elm.getKey(), elm.getValue());
			}
		}

		final URI uri = builder.build();
		HttpRequestBase request;
		if (method.equals(HttpMethod.GET)) {
			request = new HttpGet(uri);
		} else if (method.equals(HttpMethod.DELETE)) {
			request = new HttpDelete(uri);
		} else {
			throw new Exception("HTTP method not supported");
		}
		if (headers != null) {
			for (final Entry<String, String> elm : headers.entrySet()) {
				request.addHeader(elm.getKey(), elm.getValue());
			}
		}

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				return this.getObjectFromString(EntityUtils.toString(response.getEntity()), classType);

			} else {
				throw new HttpRestClientException(
						String.format("Response status code was %i and response was %s",
								response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity())),
						response.getStatusLine().getStatusCode());
			}
		} catch (final ClientProtocolException e) {
			throw new HttpRestClientException("Client protocol Exception occured while executing retuest",
					e.getCause());
		} catch (final IOException e) {
			throw new HttpRestClientException("IO Exception occured while executing retuest", e.getCause());
		}

	}

	public <T> T executePost(final String url, final Object payLoad, final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		final Map<String, String> headers = new HashMap<String, String>();
		final Map<String, String> params = new HashMap<String, String>();
		return this.executePost(url, payLoad, headers, params, classType);

	}

	public <T> T executePost(final String url, final Object payLoad, final Map<String, String> params,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		final Map<String, String> headers = new HashMap<String, String>();
		return this.executePost(url, payLoad, headers, params, classType);
	}

	public <T> T executePost(final String url, @Nullable final Object payLoad,
			@Nullable final Map<String, String> headers, @Nullable final Map<String, String> urlparams,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {

		return this.execute(HttpMethod.POST, url, payLoad, headers, urlparams, classType);

	}

	public <T> T executePut(final String url, final Object payLoad, final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		final Map<String, String> headers = new HashMap<String, String>();
		final Map<String, String> params = new HashMap<String, String>();
		return this.executePut(url, payLoad, headers, params, classType);

	}

	public <T> T executePut(final String url, final Object payLoad, final Map<String, String> params,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		final Map<String, String> headers = new HashMap<String, String>();
		return this.executePut(url, payLoad, headers, params, classType);
	}

	public <T> T executePut(final String url, @Nullable final Object payLoad,
			@Nullable final Map<String, String> headers, @Nullable final Map<String, String> urlparams,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {

		return this.execute(HttpMethod.PUT, url, payLoad, headers, urlparams, classType);

	}

	public <T> T executePatch(final String url, final Object payLoad, final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		final Map<String, String> headers = new HashMap<String, String>();
		final Map<String, String> params = new HashMap<String, String>();
		return this.executePatch(url, payLoad, headers, params, classType);

	}

	public <T> T executePatch(final String url, final Object payLoad, final Map<String, String> params,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		final Map<String, String> headers = new HashMap<String, String>();
		return this.executePatch(url, payLoad, headers, params, classType);
	}

	public <T> T executePatch(final String url, @Nullable final Object payLoad,
			@Nullable final Map<String, String> headers, @Nullable final Map<String, String> urlparams,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {

		return this.execute(HttpMethod.PATCH, url, payLoad, headers, urlparams, classType);

	}

	private <T> T execute(final HttpMethod method, final String url, @Nullable final Object payLoad,
			@Nullable final Map<String, String> headers, @Nullable final Map<String, String> urlparams,
			final Class<T> classType)
			throws HttpRestClientException, URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		URIBuilder builder = new URIBuilder(url);
		if (urlparams != null) {
			for (final Entry<String, String> elm : urlparams.entrySet()) {
				builder = builder.setParameter(elm.getKey(), elm.getValue());
			}
		}
		final URI uri = builder.build();
		final HttpEntityEnclosingRequestBase request = getRequest(method, uri);
		if (headers != null) {
			for (final Entry<String, String> elm : headers.entrySet()) {
				request.addHeader(elm.getKey(), elm.getValue());
			}
		}

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		// Send Json String as body, can also send UrlEncodedFormEntity
		if (payLoad != null) {
			final StringEntity entity = new StringEntity(getSonString(payLoad));
			request.setEntity(entity);
		}

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			final int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK || status == HttpStatus.SC_CREATED || status == HttpStatus.SC_ACCEPTED) {
				if (classType.getTypeName().equals("java.lang.String")) {
					return (T) EntityUtils.toString(response.getEntity());
				}
				return this.getObjectFromString(EntityUtils.toString(response.getEntity()), classType);

			} else {
				throw new HttpRestClientException(
						String.format("Response status code was %i and response was %s",
								response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity())),
						response.getStatusLine().getStatusCode());
			}
		} catch (final ClientProtocolException e) {
			throw new HttpRestClientException("Client protocol Exception occured while executing retuest",
					e.getCause());
		} catch (final IOException e) {
			throw new HttpRestClientException("IO Exception occured while executing retuest", e.getCause());
		}

	}

	private HttpEntityEnclosingRequestBase getRequest(final HttpMethod method, final URI uri) {
		HttpEntityEnclosingRequestBase request = null;
		if (method.equals(HttpMethod.POST)) {
			request = new HttpPost(uri);
		} else if (method.equals(HttpMethod.PUT)) {
			request = new HttpPut(uri);
		} else if (method.equals(HttpMethod.PATCH)) {
			request = new HttpPatch(uri);
		}
		return request;
	}
}
