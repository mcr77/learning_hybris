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

package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests
import de.hybris.platform.util.Utilities

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.X509TrustManager

import groovy.json.JsonSlurper

/**
 * Class is based on property file which is loaded in static block.
 *
 * USAGE:
 * All variables that we want to override need to be set in config file. Key names in property file MUST be the same as
 * variable names that we want to override.
 *
 * CAUTION:
 * Config file is loaded after default values are set so all variables that refers to other variables must be overridden
 * separately ie. variable BASE refers to variable HOST, so if we override variable HOST in config file, variable BASE
 * will still have the old value. It should be also overridden.
 */


class TestUtil {

	public final String CURRENT_EXTENSION = 'ycommercewebservicestest';
	public final String TEST_EXTENSION_SUFFIX = 'test';

	String PROPERTIES_FILE_NAME = '/groovytests-property-file-v2.groovy'

	String HOST = 'localhost'
	String WEBROOT = 'rest'
	String PORT = 9001
	String SECURE_PORT = 9002
	String BASE = "http://${HOST}:${PORT}/${WEBROOT}/v2/wsTest"
	String SECURE_BASE = "https://${HOST}:${SECURE_PORT}/${WEBROOT}/v2/wsTest"
	//static String OAUTH2_TOKEN_ENDPOINT = 'https://localhost:${SECURE_PORT}/${WEBROOT}/oauth/token' for testing with https
	String OAUTH2_TOKEN_ENDPOINT = "http://${HOST}:${PORT}/${WEBROOT}/oauth/token"
	String CLIENT_ID = 'mobile_android'
	String CLIENT_SECRET = 'secret'
	String TRUSTED_CLIENT_ID = 'trusted_client'
	String TRUSTED_CLIENT_SECRET = 'secret'
	String REDIRECT_URI = "http://${HOST}:8080/oauth2_callback"
	String LOGOUT_URL = "https://${HOST}:${SECURE_PORT}/${WEBROOT}/v2/customers/current/logout"
	//static String BASE = 'http://api.hybrisdev.com:9001/${WEBROOT}/v1/electronics'
	//static String SECURE_BASE = 'http://api.hybrisdev.com:9001/${WEBROOT}/v1/electronics'

	String USERNAME = 'democustomer'
	String PASSWORD = '1234'

	boolean FAIL_ON_NAMING_CONVENTION_ERROR = false

	public void initializeWebRoot() {
		String extensionToTest = CURRENT_EXTENSION.replaceAll(TEST_EXTENSION_SUFFIX,'');
		WEBROOT = Utilities.getPlatformConfig().getExtensionInfo(extensionToTest).getWebModule().getWebRoot();
		println 'determined WEBROOT=' + WEBROOT;
	}

	public TestUtil(String propertyFile) {
		initializeWebRoot();
		this.loadProperties(propertyFile);
	}

	/**
	 * Method overrides default static variables such as HOST or BASE
	 * Property file should be of type .groovy and must have following structure:
	 * <VARIABLE_NAME>='<VALUE>' example:
	 * HOST='localhost'
	 * overrides variable named HOST with value localhost
	 *
	 * @param propertyFile
	 * @return
	 */
	private loadProperties(final String propertyFile) {
		if (propertyFile != null)
			PROPERTIES_FILE_NAME = propertyFile;

		def configObject = null
		try {
			ConfigSlurper configSlurper = new ConfigSlurper()
			def vars = [:];
			vars['WEBROOT'] = this.WEBROOT;
			configSlurper.setBinding(vars);
			String configScript = this.getClass().getResource(PROPERTIES_FILE_NAME).text
			configObject = configSlurper.parse(configScript)
		}
		catch (Exception e) {
			//logging error, if cannot load config files it leaves default values
			println "ERROR: cannot load groovy tests config file: ${e.message}"
		}

		if (configObject == null) return

			configObject.flatten().each
		{ key, value ->
			try {
				this."${key}" = value
			} catch (Exception e) {
				//it happens mostly when incompatible variable is set in config file
				//error is logged only
				println "ERROR: cannot override variable from config file: ${key}"
			}
		}
	}

	public getClientCredentialsToken(clientId = CLIENT_ID, clientSecret = CLIENT_SECRET) {
		return getClientCredentialsAccessTokenMap(clientId, clientSecret).access_token
	}

	public getTrustedClientCredentialsToken(clientId = TRUSTED_CLIENT_ID, clientSecret = TRUSTED_CLIENT_SECRET) {
		return getClientCredentialsAccessTokenMap(clientId, clientSecret).access_token
	}

	private getClientCredentialsAccessTokenMap(clientId, clientSecret) {
		fakeSecurity()

		//direct exchange of username and password for access token
		def con = OAUTH2_TOKEN_ENDPOINT.toURL().openConnection()
		con.doOutput = true
		con.requestMethod = 'POST'
		//con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.outputStream << "client_id=${clientId}&client_secret=${clientSecret}&grant_type=client_credentials"

		if (con.errorStream)
			con.errorStream.text

		def body = con.inputStream.text
		println body
		def response = new JsonSlurper().parseText(body)

		assert response.access_token
		assert response.token_type == 'bearer'
		assert response.expires_in


		println "Access Token: ${response.access_token}"
		return response
	}

	public getAccessToken(username = USERNAME, password = PASSWORD, clientId = CLIENT_ID, clientSecret = CLIENT_SECRET) {
		return getAccessTokenMap(username, password, clientId, clientSecret).access_token
	}

	public getAccessTokenMap(username = USERNAME, password = PASSWORD, clientId = CLIENT_ID, clientSecret = CLIENT_SECRET) {
		fakeSecurity()

		//direct exchange of username and password for access token
		def con = OAUTH2_TOKEN_ENDPOINT.toURL().openConnection()
		con.doOutput = true
		con.requestMethod = 'POST'
		//con.setRequestProperty("Accept", "application/json");
		con.outputStream << "client_id=${clientId}&client_secret=${clientSecret}&grant_type=password&username=${username}&password=${password}"

		def body = con.inputStream.text
		println body
		def response = new JsonSlurper().parseText(body)

		assert response.access_token
		assert response.token_type == 'bearer'
		assert response.refresh_token
		assert response.expires_in


		println "Access Token: ${response.access_token}"
		return response
	}

	public refreshToken(refresh_token) {
		fakeSecurity()

		//direct exchange of username and password for access token
		def con = OAUTH2_TOKEN_ENDPOINT.toURL().openConnection()
		con.doOutput = true
		con.requestMethod = 'POST'
		con.outputStream << "refresh_token=${refresh_token}&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&redirect_uri=${URLEncoder.encode(REDIRECT_URI, 'UTF-8')}&grant_type=refresh_token"

		println con.errorStream
		def body = con.inputStream.text

		def response = new JsonSlurper().parseText(body)

		assert response.access_token
		assert response.token_type == 'bearer'
		assert response.refresh_token
		assert response.expires_in

		println "Refreshed Access Token: ${response.access_token}"
		return response.access_token

	}

	/**
	 * Deprecated since v2 version which requires SSL for each call.
	 */
	@Deprecated
	public getConnection(path, method = 'GET', accept = 'XML', code = HttpURLConnection.HTTP_OK, body = null, cookie = null, auth = null) {
		def url
		if (path.startsWith('http'))
			url = path
		else
			url = BASE + path

		def con = url.toURL().openConnection()

		if (method == 'PATCH') {
			method = 'POST'
			if (body == null)
				body = "_method=PATCH";
			else
				body = body + "&_method=PATCH"
		}

		if (method == "POST" || method == 'PUT')
			con.doOutput = true

		if (accept == 'XML')
			con.setRequestProperty("Accept", "application/xml");
		else if (accept == 'JSON')
			con.setRequestProperty("Accept", "application/json");

		if (auth) {
			if (auth.contains(':'))
				con.setRequestProperty("Authorization", "Basic " + auth.toString().bytes.encodeBase64().toString())
			else
				con.setRequestProperty('Authorization', "Bearer ${auth}")
		}

		if (cookie)
			con.setRequestProperty("Cookie", cookie)

		con.requestMethod = method

		if (body) {
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
			con.outputStream << body
		}

		//if (con.responseCode >= 400)
		//	println con.errorStream.text

		assert con.responseCode == code: "Expected response code ${code}, but got ${con.responseCode}"

		return con
	}

	public getURLConnection(url, method) {
		def con = url.toURL().openConnection()
		con.requestMethod = method
		return con
	}

	public getSecureURLConnection(url, method) {
		fakeSecurity()
		def con = url.toURL().openConnection()
		con.requestMethod = method
		return con
	}

	public getSecureConnection(path, method = 'GET', accept = 'XML', code = HttpURLConnection.HTTP_OK, body = null, cookie = null, auth = null, contentType = "application/x-www-form-urlencoded") {
		fakeSecurity()

		def url
		if (path.startsWith('https'))
			url = path
		else
			url = SECURE_BASE + path

		def con = url.toURL().openConnection()

		if (method == 'PATCH') {
			method = 'POST'
			if (body == null)
				body = "_method=PATCH";
			else
				body = body + "&_method=PATCH"
		}

		if (method == "POST" || method == 'PUT') {
			con.doOutput = true
		}

		if (accept == 'XML')
			con.setRequestProperty("Accept", "application/xml");
		else if (accept == 'JSON')
			con.setRequestProperty("Accept", "application/json");


		if (auth) {
			if (auth.contains(':'))
				con.setRequestProperty("Authorization", "Basic " + auth.toString().bytes.encodeBase64().toString())
			else
				con.setRequestProperty('Authorization', "Bearer ${auth}")
		}

		if (cookie)
			con.setRequestProperty("Cookie", cookie)

		con.requestMethod = method

		if (body) {
			con.setRequestProperty("Content-Type", contentType)
			con.outputStream << body
		}

		//if (con.responseCode >= 400)
		//	println con.errorStream.text

		assert con.responseCode == code: "Expected response code ${code}, but got ${con.responseCode}"

		return con
	}

	public fakeSecurity() {
		def trustManager = new DummyTrustManager()
		def hostnameVerifier = new DummyHostnameVerifier();
		def sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, [trustManager] as X509TrustManager[], new java.security.SecureRandom());
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	}

	public messageResponseCode(returned, expected) {
		return "Response Code is: " + returned + ", expected: " + expected
	}

	public basicAuth(con) {
		String userpassword = USERNAME + ":" + PASSWORD;
		String encodedAuthorization = userpassword.bytes.encodeBase64().toString()
		con.setRequestProperty("Authorization", "Basic " + encodedAuthorization)
	}

	public basicAuth(con, username, password) {
		String userpassword = username + ":" + password;
		String encodedAuthorization = userpassword.bytes.encodeBase64().toString()

		assert con instanceof HttpsURLConnection: "Basic Auth always requires HTTPS!"

		con.setRequestProperty("Authorization", "Basic " + encodedAuthorization)
	}

	public cookieString(con, cookieString) {
		con.setRequestProperty("Cookie", cookieString)
	}

	public acceptXML(con) {
		con.setRequestProperty("Accept", "application/xml");
	}

	public acceptJSON(con) {
		con.setRequestProperty("Accept", "application/json");
	}

	public verifyJSON(jsonString, fail = FAIL_ON_NAMING_CONVENTION_ERROR) {
		assert !jsonString.contains('de.hybris'): "JSON Serialization issue - Check XStream Config! ${jsonString}"
		this.dataModelWarnings(jsonString, fail)
	}

	public verifyXML(xmlString, fail = FAIL_ON_NAMING_CONVENTION_ERROR) {
		//TODO change to strong assert
		assert !xmlString.contains('de.hybris'): "XML Serialization issue - Check XStream Config! ${xmlString}"
		this.dataModelWarnings(xmlString, fail)
	}

	private dataModelWarnings(text, fail = FAIL_ON_NAMING_CONVENTION_ERROR) {
		def m = text =~ /([^<>"]*Data[^- ])/

		m.each {
			if (fail)
				assert false: "Choose a better name for ${it[0]}?"
			else
				println "WARNING: Choose a better name for ${it[0]}?"
		}


	}

	public verifiedXMLSlurper(con, debug = false, fail = FAIL_ON_NAMING_CONVENTION_ERROR) {
		def body = con.inputStream.text

		if (debug)
			println body

		this.verifyXML(body, fail)
		return new XmlSlurper().parseText(body)
	}

	public verifiedJSONSlurper(con, debug = false, fail = FAIL_ON_NAMING_CONVENTION_ERROR) {
		def body = con.inputStream.text

		if (debug)
			println body

		this.verifyJSON(body, fail)
		return new JsonSlurper().parseText(body)
	}
}




