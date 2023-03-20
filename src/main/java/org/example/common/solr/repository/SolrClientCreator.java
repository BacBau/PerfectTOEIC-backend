package org.example.common.solr.repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class SolrClientCreator {
    private static final Logger logger = LoggerFactory.getLogger(SolrClientCreator.class);
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 5000;
    private static final int KEEP_ALIVE_TIME_MILLIS = 30000;
    private static final String HTTP_TARGET_HOST = "http.target_host";

    public SolrClientCreator() {
    }

    public static SolrClient create(Environment environment, String schemaName) throws MalformedURLException {
        String remote = environment.getProperty(schemaName + ".solr.url");
        String username;
        String password;
        if (!StringUtils.isEmpty(remote)) {
            username = environment.getProperty(schemaName + ".solr.username");
            password = environment.getProperty(schemaName + ".solr.password");
            return create(remote, (String)null, username, password, 30000);
        } else {
            remote = environment.getProperty("solr.host");
            if (!StringUtils.isEmpty(remote)) {
                username = environment.getProperty("solr.username");
                password = environment.getProperty("solr.password");
                return create(remote, schemaName, username, password, 30000);
            } else {
                remote = environment.getProperty("solr.url");
                if (!StringUtils.isEmpty(remote)) {
                    username = environment.getProperty("solr.username");
                    password = environment.getProperty("solr.password");
                    return create(remote, schemaName, username, password, 30000);
                } else {
                    throw new RuntimeException("No Remote " + schemaName);
                }
            }
        }
    }

    public static SolrClient create(String remote, String username, String password, int timeout) throws MalformedURLException {
        return create(remote, (String)null, username, password, timeout);
    }

    public static SolrClient create(String remote, String collectionName, String username, String password, int timeout) throws MalformedURLException {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setMaxConnPerRoute(228);
        clientBuilder.setMaxConnTotal(100);
        Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(timeout);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(timeout);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(timeout);
        clientBuilder.setDefaultRequestConfig(requestBuilder.build());
        clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(10, true));
        if (StringUtils.isEmpty(username)) {
            clientBuilder.setDefaultRequestConfig(requestBuilder.build());
        } else {
            URL url = new URL(remote);
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(username, password));
            clientBuilder.setDefaultRequestConfig(requestBuilder.build()).setDefaultCredentialsProvider(credentialsProvider).addInterceptorFirst(new SolrClientCreator.PreemptiveAuthInterceptor());
        }

        CloseableHttpClient httpClient = HttpClients.custom().setKeepAliveStrategy(getConnectionKeepAliveStrategy()).setDefaultConnectionConfig(getConnectionConfig()).build();
        if (remote.indexOf(44) > -1) {
            return createLBHttpSolrClient(remote, collectionName, httpClient);
        } else {
            if (!remote.endsWith("/")) {
                remote = remote + "/";
            }

            if (!StringUtils.isEmpty(collectionName)) {
                remote = remote + collectionName;
            }

            org.apache.solr.client.solrj.impl.HttpSolrClient.Builder solrBuilder = new org.apache.solr.client.solrj.impl.HttpSolrClient.Builder(remote);
            solrBuilder.withHttpClient(httpClient);
            solrBuilder.allowCompression(true);
            HttpSolrClient client = solrBuilder.build();
            logger.info(client.getBaseURL() + ": Create Successful! ");
            return client;
        }
    }

    private static ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy() {
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));

                String param;
                String value;
                do {
                    if (!it.hasNext()) {
                        HttpHost target = (HttpHost)context.getAttribute("http.target_host");
                        if (target.getHostName().equalsIgnoreCase("localhost")) {
                            return 5000L;
                        }

                        return 30000L;
                    }

                    HeaderElement he = it.nextElement();
                    param = he.getName();
                    value = he.getValue();
                } while(value == null || !param.equalsIgnoreCase("timeout"));

                return Long.parseLong(value) * 1000L;
            }
        };
    }

    private static ConnectionConfig getConnectionConfig() {
        return ConnectionConfig.custom().setBufferSize(4128).build();
    }

    private static LBHttpSolrClient createLBHttpSolrClient(String remote, String collectionName, CloseableHttpClient httpClient) {
        List<String> list = org.example.common.solr.repository.StringUtils.toList(remote, ',');
        Iterator iterator = list.iterator();

        while(iterator.hasNext()) {
            String value = ((String)iterator.next()).trim();
            if (StringUtils.isEmpty(value)) {
                iterator.remove();
            }
        }

        String[] urls = new String[list.size()];

        for(int i = 0; i < list.size(); ++i) {
            urls[i] = ((String)list.get(i)).trim();
            if (!urls[i].endsWith("/")) {
                urls[i] = urls[i] + "/";
            }

            if (!StringUtils.isEmpty(collectionName)) {
                urls[i] = urls[i] + collectionName;
            }
        }

        org.apache.solr.client.solrj.impl.LBHttpSolrClient.Builder builder = new org.apache.solr.client.solrj.impl.LBHttpSolrClient.Builder();
        builder.withBaseSolrUrls(urls);
        builder.withHttpClient(httpClient);
        LBHttpSolrClient client = builder.build();
        logger.info("LBSolr client with " + Arrays.asList(urls) + ": Create Successful! ");
        return client;
    }

    static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
        PreemptiveAuthInterceptor() {
        }

        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState)context.getAttribute("http.auth.target-scope");
            if (authState.getAuthScheme() == null) {
                CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
                HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
                AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                Credentials creds = credsProvider.getCredentials(authScope);
                if (creds == null) {
                }

                authState.update(new BasicScheme(), creds);
            }

        }
    }
}
