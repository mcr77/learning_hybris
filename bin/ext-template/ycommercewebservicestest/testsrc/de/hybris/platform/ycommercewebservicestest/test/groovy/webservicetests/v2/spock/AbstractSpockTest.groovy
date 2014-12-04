/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK
import spock.lang.Specification
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

abstract class AbstractSpockTest extends Specification {

	private static final String DEFAULT_HTTP_URI = 'http://localhost:9001'
	private static final String DEFAULT_HTTPS_URI = 'https://localhost:9002'

	private static final String BASE_PATH = '/rest/v2'
	private static final String BASE_PATH_WITH_SITE = BASE_PATH + '/wsTest'

	private static final String OAUTH2_DEFAULT_URI = DEFAULT_HTTPS_URI
	private static final String OAUTH2_TOKEN_URI = OAUTH2_DEFAULT_URI
	private static final String OAUTH2_TOKEN_PATH = '/rest/oauth/token'

	private static final String CLIENT_ID = 'mobile_android'
	private static final String CLIENT_SECRET = 'secret'
	private static final String CLIENT_REDIRECT_URI = 'http://localhost:8080/oauth2_callback'

	private static final String TRUSTED_CLIENT_ID = 'trusted_client'
	private static final String TRUSTED_CLIENT_SECRET = 'secret'

	protected RESTClient restClient

	def setup() {
		restClient = createRestClient()
	}

	def cleanup() {
		restClient.shutdown()
	}

	protected static final String getDefaultHttpUri() {
		return DEFAULT_HTTP_URI
	}

	protected static final String getDefaultHttpsUri() {
		return DEFAULT_HTTPS_URI
	}

	protected static final String getBasePath() {
		return BASE_PATH
	}

	protected static final String getBasePathWithSite() {
		return BASE_PATH_WITH_SITE
	}

	protected static final String getOAuth2TokenUri() {
		return OAUTH2_TOKEN_URI
	}

	protected static final String getOAuth2TokenPath() {
		return OAUTH2_TOKEN_PATH
	}

	protected static final String getClientId() {
		return CLIENT_ID
	}

	protected static final String getClientSecret() {
		return CLIENT_SECRET
	}

	protected static final String getClientRedirectUri() {
		return CLIENT_REDIRECT_URI
	}

	protected static final String getTrustedClientId() {
		return TRUSTED_CLIENT_ID
	}

	protected static final String getTrustedClientSecret() {
		return TRUSTED_CLIENT_SECRET
	}

	protected RESTClient createRestClient(uri = DEFAULT_HTTPS_URI) {
		def client = new RESTClient(uri)

		// makes sure we can access the services even without a valid SSL certificate
		client.ignoreSSLIssues()

		// makes sure an exception is not thrown and that the response is parsed
		client.handler.failure = client.handler.success

		// used to record the requests in jmeter
		//client.setProxy('localhost', 8080, null)

		return client
	}

	protected void addAuthorization(RESTClient client, token) {
		client.getHeaders().put('Authorization', ' Bearer ' + token.access_token)
	}

	protected void removeAuthorization(RESTClient client) {
		client.getHeaders().remove('Authorization')
	}

	protected getOAuth2TokenUsingClientCredentials(RESTClient client, clientId, clientSecret) {
		HttpResponseDecorator response = client.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: [
					'grant_type': 'client_credentials',
					'client_id': clientId,
					'client_secret': clientSecret
				],
				contentType: JSON,
				requestContentType: URLENC)

		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			assert status == SC_OK
			assert data.token_type == 'bearer'
			assert data.access_token
			assert data.expires_in
		}

		return response.data
	}

	protected getOAuth2TokenUsingPassword(RESTClient client, clientId, clientSecret, username, password, boolean doAssert=true) {
		HttpResponseDecorator response = client.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: [
					'grant_type': 'password',
					'client_id': clientId,
					'client_secret': clientSecret,
					'username': username,
					'password': password
				],
				contentType: JSON,
				requestContentType: URLENC)

		if(doAssert) {
			with(response) {
				if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
				assert status == SC_OK
				assert data.token_type == 'bearer'
				assert data.access_token
				assert data.expires_in
				assert data.refresh_token
			}
		}

		return response.data
	}

	protected refreshOAuth2Token(RESTClient client, refreshToken, clientId, clientSecret, redirectUri) {
		def bodyParams = [
			'grant_type': 'refresh_token',
			'refresh_token': refreshToken
		]

		if (clientId) {
			bodyParams['client_id'] = clientId
		}

		if (clientSecret) {
			bodyParams['client_secret'] = clientSecret
		}

		if (redirectUri) {
			bodyParams['redirect_uri'] = URLEncoder.encode(redirectUri, 'UTF-8')
		}

		HttpResponseDecorator response = client.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: bodyParams,
				contentType: JSON,
				requestContentType: URLENC)

		with(response) {
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			assert status == SC_OK
			assert data.token_type == 'bearer'
			assert data.access_token
			assert data.expires_in
			assert data.refresh_token
		}

		return response.data
	}

	protected void authorizeClient(RESTClient client) {
		def token = getOAuth2TokenUsingClientCredentials(client, getClientId(), getClientSecret())
		addAuthorization(client, token)
	}

	protected void authorizeTrustedClient(RESTClient client) {
		def token = getOAuth2TokenUsingClientCredentials(client, getTrustedClientId(), getTrustedClientSecret())
		addAuthorization(client, token)
	}

	/**
	 * Checks if a node exists and is not empty. Works for JSON and XML formats.
	 *
	 * @param the node to check
	 * @return {@code true} if the node is not empty, {@code false} otherwise
	 */
	protected isNotEmpty(node) {
		(node != null) && (node.size() > 0)
	}

	/**
	 * Same as {@link spock.lang.Specification#with(Object, groovy.lang.Closure)}, the only difference is that it returns the target object.
	 *
	 * @param target an implicit target for conditions and/or interactions
	 * @param closure a code block containing top-level conditions and/or interactions
	 * @return the target object
	 */
	def returningWith(target, closure) {
		with(target, closure)
		return target
	}
}
