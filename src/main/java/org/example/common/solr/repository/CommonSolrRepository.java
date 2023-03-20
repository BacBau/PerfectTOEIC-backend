package org.example.common.solr.repository;

import org.example.common.solr.DataMapper;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;

public abstract class CommonSolrRepository<T, M extends DataMapper<T>>  extends LazyRepositoryImpl<T, M> {


    public CommonSolrRepository(Environment environment, String name, M mapper, Class<T> clazz) throws MalformedURLException {
        super(environment,name, mapper, clazz);
    }


}