/**
 * 
 */
package com.github.cthulhu666.drools.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.drools.command.BatchExecutionCommand;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.help.BatchExecutionHelper;

import com.thoughtworks.xstream.XStream;

/**
 * @author Jakub Głuszecki
 *
 */
public class DroolsTemplate {
	
	private String lookup;
	
	private String resourceURI;
	
	private String host;
	
	private int port;
	
	public final ExecutionResults executeBatch(DroolsCallback callback) {
		
		List<Command> commands = new ArrayList<Command>();
				
		callback.execute(commands);
		
		BatchExecutionCommand batchExecutionCommand = CommandFactory.newBatchExecution(commands, getLookup());
		
		HttpClient httpClient = getHttpClient();		
		String resourceURI = getResourceURI();
		PostMethod postMethod = new PostMethod(resourceURI);
		
		XStream xStreamMarshaller = BatchExecutionHelper.newJSonMarshaller();
		String xmlCommand = xStreamMarshaller.toXML(batchExecutionCommand);
		System.out.println(xmlCommand);
		
		StringRequestEntity request;
		try {
			request = new StringRequestEntity(xmlCommand, "text/plain", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		postMethod.setRequestEntity(request);
		int status;
		try {
			status = httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String response;
		try {
			response = postMethod.getResponseBodyAsString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		System.out.printf("Response STATUS=%s, BODY=%s\n", status, response);
		
		ExecutionResults results = (ExecutionResults) BatchExecutionHelper.newJSonMarshaller().fromXML(response);
		
		return results;
	}
	
	protected HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().setHost(getHost(), getPort());
		return httpClient;
	}

	public String getLookup() {
		return lookup;
	}

	public String getResourceURI() {		
		return resourceURI;
	}

	public DroolsTemplate setLookup(String lookup) {
		this.lookup = lookup;
		return this;
	}

	public DroolsTemplate setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
		return this;
	}

	public String getHost() {
		return host;
	}

	public DroolsTemplate setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public DroolsTemplate setPort(int port) {
		this.port = port;
		return this;
	}

}
