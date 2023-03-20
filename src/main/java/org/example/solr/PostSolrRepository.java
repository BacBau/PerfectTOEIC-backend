package org.example.solr;

import lombok.extern.log4j.Log4j2;
import org.apache.solr.client.solrj.SolrQuery;
import org.example.common.solr.repository.CommonSolrRepository;
import org.example.search.SearchDataGenerator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.List;

@Service
@Log4j2
public class PostSolrRepository extends CommonSolrRepository<PostIndex, PostIndexMapper> {

    public PostSolrRepository(Environment environment) throws MalformedURLException {
        super(environment, "post", new PostIndexMapper(), PostIndex.class);
    }

    @Override
    public String getId(PostIndex bean) {
        return bean.getId();
    }

    public List<PostIndex> search(String keyword, int maxSize) {
        String search = SearchDataGenerator.toAliasSearch(keyword);
        StringBuilder searchBuilder = new StringBuilder();
        if (SearchDataGenerator.isVietnameseWord(keyword)) {
            searchBuilder.append("dataSearchVi:(").append(search).append(")");
        } else
            searchBuilder.append("dataSearchEn:(").append(search).append(")");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(searchBuilder.toString());
        solrQuery.setParam("q.op", "AND");
        return search(solrQuery, 1, maxSize).getPageItems();
    }
}
