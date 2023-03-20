package org.example.common.solr;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.stream.Collectors;

public abstract class DataMapper<T> {
    protected static final String PROPERTY_FIELD_EXT = "_prop_s";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected Environment env;

    public DataMapper() {
    }

    public abstract T to(SolrDocument doc);

    public abstract SolrInputDocument from(T t);

    protected String getAsString(SolrDocument doc, String field) {
        List<String> list = this.getAsList(doc, field);
        if (list != null && !list.isEmpty()) {
            StringJoiner joiner = new StringJoiner("\n");
            list.forEach((value) -> {
                joiner.add(value);
            });
            return joiner.toString();
        } else {
            return "";
        }
    }

    protected List<String> getAsList(SolrDocument doc, String field) {
        Collection<Object> collection = doc.getFieldValues(field);
        return (List)(collection == null ? new ArrayList() : (List)collection.parallelStream().map((obj) -> {
            return (String)obj;
        }).collect(Collectors.toList()));
    }

    protected Set<String> getAsSet(SolrDocument doc, String field) {
        Collection<Object> collection = doc.getFieldValues(field);
        return (Set)(collection == null ? new TreeSet() : (Set)collection.parallelStream().map((obj) -> {
            return (String)obj;
        }).collect(Collectors.toSet()));
    }

    protected String[] getAsArray(SolrDocument doc, String field) {
        List<String> list = this.getAsList(doc, field);
        return (String[])list.toArray(new String[0]);
    }

    protected Properties loadProperties(SolrDocument doc) {
        Properties properties = new Properties();
        Collection<String> fieldNames = doc.getFieldNames();
        fieldNames.forEach((field) -> {
            int idx = field.lastIndexOf("_prop_s");
            if (idx >= 0) {
                String value = (String)doc.getFieldValue(field);
                String name = field.substring(0, idx);
                properties.put(name, value);
            }
        });
        return properties;
    }

    protected void putProperties(Properties properties, SolrInputDocument input) {
        if (properties != null && !properties.isEmpty()) {
            properties.forEach((key, value) -> {
                input.addField(key + "_prop_s", value);
            });
        }
    }
}