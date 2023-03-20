package org.example.solr;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.example.common.solr.DataMapper;
import org.example.model.entity.Post;
import org.example.search.SearchDataGenerator;
import org.example.search.VietnameseConverter;
import org.example.utils.Utils;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostIndexMapper extends DataMapper<PostIndex> {
    @Override
    public PostIndex to(SolrDocument doc) {
        PostIndex postIndex = new PostIndex();
        postIndex.setHtml((String) doc.getFieldValue("html"));
        postIndex.setType(Post.Type.valueOf((String) doc.getFieldValue("type")));
        postIndex.setId((String) doc.getFieldValue("id"));
        postIndex.setPreviewImageUrl((String) doc.get("previewImageUrl"));
        postIndex.setTitle((String) doc.getFieldValue("title"));
        postIndex.setCreatedDate((Date) doc.getFieldValue("createdDate"));
        postIndex.setDescription((String) doc.getFieldValue("description"));
        return postIndex;
    }

    @Override
    public SolrInputDocument from(PostIndex postIndex) {
        Map<String, SolrInputField> map = new HashMap<>();
        SolrInputField field = new SolrInputField("id");
        field.setValue(postIndex.getId());
        map.put("id", field);

        field = new SolrInputField("html");
        field.setValue(postIndex.getHtml());
        map.put("html", field);

        field = new SolrInputField("type");
        field.setValue(postIndex.getType().toString());
        map.put("type", field);

        field = new SolrInputField("title");
        field.setValue(postIndex.getTitle());
        map.put("title", field);

        field = new SolrInputField("previewImageUrl");
        field.setValue(postIndex.getPreviewImageUrl());
        map.put("previewImageUrl", field);

        String dataSearch = SearchDataGenerator.normalizationText(postIndex.getTitle() + " " + Utils.extractHtmlContent(postIndex.getHtml()));
        dataSearch = SearchDataGenerator.toDataSearch(dataSearch.toLowerCase());
        field = new SolrInputField("dataSearchEn");
        field.setValue(VietnameseConverter.toTextNotMarked(dataSearch));
        map.put("dataSearchEn", field);

        field = new SolrInputField("dataSearchVi");
        field.setValue(dataSearch);
        map.put("dataSearchVi", field);

        field = new SolrInputField("createdDate");
        field.setValue(postIndex.getCreatedDate());
        map.put("createdDate", field);

        field = new SolrInputField("description");
        field.setValue(postIndex.getDescription());
        map.put("description", field);

        return new SolrInputDocument(map);
    }
}
