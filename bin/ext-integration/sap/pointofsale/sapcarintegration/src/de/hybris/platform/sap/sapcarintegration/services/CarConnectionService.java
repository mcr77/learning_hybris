package de.hybris.platform.sap.sapcarintegration.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import de.hybris.platform.sap.core.configuration.http.HTTPDestinationService;

public interface CarConnectionService {

	public abstract HttpURLConnection createConnection(String absoluteUri, String contentType,
			String httpMethod) throws IOException, MalformedURLException;

}