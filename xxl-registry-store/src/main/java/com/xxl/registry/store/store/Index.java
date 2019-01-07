package com.xxl.registry.store.store;

import com.xxl.registry.store.manager.IndexManager;
import org.apache.lucene.document.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.util.BytesRef;

public final class Index {

    private IndexManager indexManager = IndexManager.getInstance();

    public void createIndex() {
        boolean result = false;
        TrackingIndexWriter indexWriter = null;
        try {
            indexWriter = indexManager.getIndexWriter();
            indexWriter.deleteAll();
            long time = System.currentTimeMillis();
            int index = 1;

            saveOrUpdate(null, index);
            index++;

            System.out.println(((System.currentTimeMillis() - time)) + " ms");
            result = true;
            indexWriter.getIndexWriter().forceMerge(1);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        if (result) {
            System.out.println("创建index成功");
        }
    }

    private boolean saveOrUpdate(Object topicModel, int index) {
        return false;
    }

    private boolean add(Integer id) {
        final TrackingIndexWriter indexWriter = indexManager.getIndexWriter();
        Document doc = new Document();
        doc.add(new StringField("name", "专题_0", Field.Store.YES));
        doc.add(new TextField("text", "中华人民共和国", Field.Store.YES));
        doc.add(new StringField("id", String.valueOf(id), Field.Store.YES));
        doc.add(new SortedDocValuesField("id", new BytesRef(String.valueOf(id))));
        doc.add(new IntField("count", 1, Field.Store.YES));
        doc.add(new NumericDocValuesField("count", 1));
        try {
            indexWriter.addDocument(doc);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                indexWriter.getIndexWriter().commit();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private boolean delete() {
        TrackingIndexWriter indexWriter = indexManager.getIndexWriter();
        try {
            indexWriter.deleteDocuments(new Term("id", "1"));
            Thread.sleep(100000);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        return false;
    }

}
