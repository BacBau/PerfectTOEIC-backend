package org.example.common.solr.repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.example.common.solr.DataMapper;
import org.example.common.solr.exception.RepositoryException;
import org.example.common.solr.mapper.UpdateField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public abstract class RepositoryImpl<T, M extends DataMapper<T>> implements DisposableBean {
    public static final int QUERY_ERROR = 57;
    public static final String QUERY_ERROR_DESCRIPTION = "Solr Query Error";
    public static final int MAX_RECORD = 10000;
    public static final int SINGLE_RECORD_SIZE = 1;
    public static final int SINGLE_RECORD_PAGE = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryImpl.class);
    private SolrClient solrClient;
    protected M mapper;
    private String singleUrl;

    public RepositoryImpl(Environment environment, String schemaName, M mapper) throws MalformedURLException {
        this.mapper = mapper;
        this.solrClient = SolrClientCreator.create(environment, schemaName);
        String remote = environment.getProperty(schemaName + ".solr.url");
        if (StringUtils.isEmpty(remote)) {
            remote = environment.getProperty("solr.host");
            if (StringUtils.isEmpty(remote)) {
                remote = environment.getProperty("solr.url");
            }

            if (!StringUtils.isEmpty(remote)) {
                remote = remote.trim();
                if (remote.endsWith("/")) {
                    this.singleUrl = remote + schemaName;
                } else {
                    this.singleUrl = remote + "/" + schemaName;
                }
            }
        } else {
            this.singleUrl = remote;
        }

    }

    public void save(T bean) throws RepositoryException {
        SolrInputDocument doc = this.mapper.from(bean);
        if (doc != null) {
            this.add(doc);
        }
    }

    public void update(String id, UpdateField<?>... fields) throws RepositoryException {
        SolrInputDocument document = new SolrInputDocument(new String[0]);
        document.addField("id", id);
        UpdateField[] var4 = fields;
        int var5 = fields.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            UpdateField<?> field = var4[var6];
            if (field != null) {
                Map<String, ?> mapValues = Collections.singletonMap(field.getAction(), field.getValue());
                document.addField(field.getName(), mapValues);
            }
        }

        this.add(document);
    }

    public Page<T> search(String query, int currentPage, int pageSize) throws RepositoryException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        return this.search(solrQuery, currentPage, pageSize);
    }

    public Page<T> search(String query, int currentPage, int pageSize, String sortBy, ORDER direction) throws RepositoryException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setSort(sortBy, direction);
        return this.search(solrQuery, currentPage, pageSize);
    }

    public Page<T> search(SolrQuery solrQuery, int currentPage, int pageSize) {
        Page<T> page = new Page();
        page.setPageNumber(currentPage);

        try {
            QueryResponse rsp = this.query(solrQuery, currentPage, pageSize);
            long numberOfResult = rsp.getResults().getNumFound();
            page.computePagesAvailable(numberOfResult, pageSize);
            page.setTotalItems(numberOfResult);
            page.setTime(rsp.getElapsedTime());
            Iterator iterator = rsp.getResults().iterator();

            while(iterator.hasNext()) {
                SolrDocument doc = (SolrDocument)iterator.next();

                try {
                    T bean = this.mapper.to(doc);
                    if (bean != null) {
                        page.getPageItems().add(bean);
                    }
                } catch (Exception var11) {
                    LOGGER.error(var11.getMessage(), var11);
                }
            }
        } catch (Exception var12) {
            this.loggingSolrError(solrQuery, var12);
        }

        return page;
    }

    public QueryResponse query(SolrQuery solrQuery, int currentPage, int pageSize) throws RepositoryException {
        try {
            pageSize = Math.min(pageSize, 10000);
            solrQuery.setStart((currentPage - 1) * pageSize);
            solrQuery.setRows(pageSize);
            return this.solrClient.query(solrQuery);
        } catch (Exception var5) {
            this.loggingSolrError(solrQuery, var5);
            return null;
        }
    }

    public void importData() {
        try {
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", new String[]{"/dataimport"});
            params.set("command", new String[]{"full-import"});
            QueryResponse response = null;
            if (this.solrClient instanceof LBHttpSolrClient) {
                response = ((LBHttpSolrClient)this.solrClient).query(params);
            } else {
                response = ((HttpSolrClient)this.solrClient).query(params);
            }

            LOGGER.info("Result import data info " + response.getStatus() + " - " + response.getException());
        } catch (Exception var3) {
            LOGGER.error(var3.toString(), var3);
        }

    }

    public T get(String id) {
        try {
            return this.get("id", id);
        } catch (Exception var3) {
            LOGGER.error(var3.toString(), var3);
            return null;
        }
    }

    public T get(String field, String value) {
        try {
            return this.getByQuery(field + ":" + value);
        } catch (Exception var4) {
            LOGGER.error(var4.toString(), var4);
            return null;
        }
    }

    public T getByQuery(String query) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);

        try {
            QueryResponse rsp = this.query(solrQuery, 1, 1);
            Iterator<SolrDocument> iterator = rsp.getResults().iterator();
            return iterator.hasNext() ? this.mapper.to((SolrDocument)iterator.next()) : null;
        } catch (Exception var5) {
            this.loggingSolrError(solrQuery, var5);
            return null;
        }
    }

    public boolean contains(String id) throws RepositoryException {
        return this.get(id) != null;
    }

    public List<T> getListByQuery(String query) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        return this.getList(solrQuery);
    }

    public List<T> getListByQuery(String query, Integer numFound) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        numFound = Math.min(numFound, 10000);
        solrQuery = numFound != null ? solrQuery.setRows(numFound) : solrQuery.setRows(10);
        return this.getList(solrQuery);
    }

    private List<T> getList(SolrQuery solrQuery) {
        try {
            QueryResponse rsp = this.query(solrQuery, 1, 10000);
            Iterator<SolrDocument> iterator = rsp.getResults().iterator();
            ArrayList list = new ArrayList();

            while(iterator.hasNext()) {
                T t = this.mapper.to((SolrDocument)iterator.next());
                list.add(t);
            }

            return list;
        } catch (Exception var6) {
            this.loggingSolrError(solrQuery, var6);
            return new ArrayList();
        }
    }

    public <X> X getFieldValue(String id, String field) throws RepositoryException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("id:" + id);
        solrQuery.setFields(new String[]{"id", field});

        try {
            QueryResponse rsp = this.query(solrQuery, 1, 1);
            Iterator<SolrDocument> iterator = rsp.getResults().iterator();
            return iterator.hasNext() ? (X)((SolrDocument)iterator.next()).getFieldValue(field) : null;
        } catch (Exception var6) {
            this.loggingSolrError(solrQuery, var6);
            return null;
        }
    }

    public List<Object> getMutilFieldValue(String id, String... fields) throws RepositoryException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("id:" + id);
        solrQuery.setFields(fields);

        try {
            QueryResponse rsp = this.query(solrQuery, 1, 1);
            Iterator<SolrDocument> iterator = rsp.getResults().iterator();
            if (!iterator.hasNext()) {
                return Collections.emptyList();
            } else {
                SolrDocument doc = (SolrDocument)iterator.next();
                List<Object> result = new ArrayList(fields.length);
                String[] var8 = fields;
                int var9 = fields.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    String field = var8[var10];
                    result.add(doc.getFieldValue(field));
                }

                return result;
            }
        } catch (Exception var12) {
            this.loggingSolrError(solrQuery, var12);
            return Collections.emptyList();
        }
    }

    public Collection<Object> getFieldValues(String id, String field) throws RepositoryException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("id:" + id);
        solrQuery.setFields(new String[]{"id", field});

        try {
            QueryResponse rsp = this.query(solrQuery, 1, 1);
            Iterator<SolrDocument> iterator = rsp.getResults().iterator();
            return iterator.hasNext() ? ((SolrDocument)iterator.next()).getFieldValues(field) : null;
        } catch (Exception var6) {
            this.loggingSolrError(solrQuery, var6);
            return null;
        }
    }

    public boolean exist(String id) throws RepositoryException {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("id:" + id);
        solrQuery.setFields(new String[]{"id"});

        try {
            QueryResponse rsp = this.query(solrQuery, 1, 1);
            Iterator<SolrDocument> iterator = rsp.getResults().iterator();
            return iterator.hasNext();
        } catch (Exception var5) {
            throw new RepositoryException(501, var5);
        }
    }

    public Integer countAll() throws SolrServerException, IOException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        solrQuery.setRows(0);

        try {
            Long total = this.query(solrQuery, 1, 1).getResults().getNumFound();
            return total.intValue();
        } catch (RepositoryException var3) {
            LOGGER.error(var3.toString(), var3);
            return -1;
        }
    }

    protected void add(SolrInputDocument document) throws RepositoryException {
        try {
            UpdateResponse add = this.solrClient.add(document);
            LOGGER.info(add.toString());
            this.solrClient.commit(false, false);
        } catch (IOException | SolrServerException var3) {
            throw new RepositoryException(501, var3);
        }
    }

    protected UpdateResponse add(SolrInputDocument document, int time) throws RepositoryException {
        try {
            UpdateResponse add = this.solrClient.add(document, time);
            LOGGER.info(add.toString());
            return add;
        } catch (IOException | SolrServerException var4) {
            throw new RepositoryException(501, var4);
        }
    }

    public void add(List<SolrInputDocument> docs) throws RepositoryException {
        docs.removeIf(Objects::isNull);
        if (docs.isEmpty()) {
            LOGGER.error("No more solr doc to commit");
        } else {
            try {
                UpdateResponse add = this.solrClient.add(docs);
                LOGGER.info(add.toString());
                UpdateResponse commit = this.solrClient.commit(false, false);
                LOGGER.info(commit.toString());
            } catch (IOException | SolrServerException var8) {
                Iterator var3 = docs.iterator();

                while(var3.hasNext()) {
                    SolrInputDocument doc = (SolrInputDocument)var3.next();

                    try {
                        UpdateResponse add = this.solrClient.add(doc);
                        LOGGER.info(add.toString());
                        UpdateResponse commit = this.solrClient.commit(false, false);
                        LOGGER.info(doc.getFieldValue("id") + "-" + commit.toString());
                    } catch (IOException | SolrServerException var7) {
                        LOGGER.error(var7.toString(), var7);
                    }
                }

                throw new RepositoryException(501, var8);
            }
        }
    }

    protected boolean addNoCommit(SolrInputDocument doc) throws SolrServerException, IOException {
        if (doc == null) {
            return false;
        } else {
            this.solrClient.add(doc);
            return true;
        }
    }

    protected UpdateResponse commit() throws SolrServerException, IOException {
        return this.solrClient.commit(false, false);
    }

    public String deleteByQuery(String query) throws RepositoryException {
        return this.deleteByXmlQuery(query);
    }

    public String deleteByNormalQuery(String query) throws RepositoryException {
        try {
            UpdateResponse response = this.solrClient.deleteByQuery(query);
            this.solrClient.commit(false, false);
            return String.valueOf(response.getStatus());
        } catch (IOException | SolrServerException var3) {
            throw new RepositoryException(501, var3);
        }
    }

    public String deleteById(String id) throws RepositoryException {
        return this.deleteByXmlQuery("id:" + id);
    }

    public String deleteById(List<String> ids) throws RepositoryException {
        StringBuilder builder = new StringBuilder();

        String id;
        for(Iterator var3 = ids.iterator(); var3.hasNext(); builder.append(this.deleteByXmlQuery("id:" + id))) {
            id = (String)var3.next();
            if (builder.length() > 0) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    private String deleteByXmlQuery(String query) throws RepositoryException {
        return this.internalDeleteByXml("<add><delete><query>" + query + "</query></delete></add>");
    }

    private String internalDeleteByXml(String xml) throws RepositoryException {
        HttpClient client = null;

        try {
            HttpPost httpPost = new HttpPost(this.singleUrl + "/update?commitWithin=1000&overwrite=true&wt=json");
            httpPost.setEntity(new StringEntity(xml));
            httpPost.addHeader("Content-type", "text/xml");
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.addHeader("Connection", "keep-alive");
            if (this.solrClient instanceof HttpSolrClient) {
                client = ((HttpSolrClient)this.solrClient).getHttpClient();
            } else {
                client = ((LBHttpSolrClient)this.solrClient).getHttpClient();
            }

            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception var5) {
            LOGGER.error(client + " - " + var5.toString(), var5);
            throw new RepositoryException(-1, var5);
        }
    }

    public void destroy() throws Exception {
        this.solrClient.close();
        LOGGER.info("Solr Client " + this.solrClient + ": Shutdown done!");
    }

    protected void loggingSolrError(SolrQuery solrQuery, Exception exp) {
        if (exp instanceof NullPointerException) {
            LOGGER.error(exp.toString(), exp);
        }

        String error = exp.toString();
        if (!error.contains("SyntaxError") && !error.contains("undefined field")) {
            LOGGER.error(solrQuery.getQuery() + " - " + exp.getMessage(), exp);
        } else {
            LOGGER.error(solrQuery.getQuery() + " - " + exp.getMessage());
        }

    }
}
