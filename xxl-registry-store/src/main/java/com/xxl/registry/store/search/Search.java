package com.xxl.registry.store.search;

import com.xxl.registry.store.constants.IndexConstants;
import com.xxl.registry.store.manager.IndexManager;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.NumericUtils;

/**
 * lucene 检索
 * https://blog.csdn.net/liuxingtianshi9570/article/details/43731321
 * https://blog.csdn.net/artaganan8/article/details/77976822
 *
 * @author mwup
 * @version 1.0
 * @created 2017/11/24 23:50
 **/
public final class Search {

    private IndexManager indexManager = IndexManager.getInstance();

    public static void main(String[] args) {
        Search search = new Search();
        int i = 0;
        try {
            String key = "1";
            while (true) {
                search.search(key);
                System.out.println();
                System.out.println();
                System.out.println();
                i++;
                if (i >= 1000) {
                    break;
                }
                Thread.sleep(5000);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void search(String key) throws Throwable {
        final long time = System.currentTimeMillis();
        final Query query = getIdQuery(key);
        IndexSearcher indexSearcher = indexManager.getIndexSearcher();
        final TopDocs topDocs = indexSearcher.search(query, 8);
        //ScoreDoc是代表一个结果的相关度得分与文档编号等信息的对象。
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        System.out.println(topDocs.getMaxScore());
        indexManager.release(indexSearcher);
        System.out.println((System.currentTimeMillis() - time) + " ms");
    }

    private Query getMultiQuery(String[] queries, String[] fields) throws Throwable {
        return MultiFieldQueryParser.parse(queries, fields, IndexConstants.analyzer);
    }

    private Query getQuery(String key) throws Throwable {
        // 调用search方法进行检索
        // 为查询分析器QueryParser 指定查询字段和分词器
        QueryParser queryParser = new QueryParser("text", IndexConstants.analyzer);
        // 查询
        Query query = queryParser.parse(key);
        return query;
    }

    private Query getIdQuery(final String id) {
        final Query query = new TermQuery(new Term("id", (id)));
        return query;
    }

    private Query getTextTermQuery(String key) {
        final Query query = new TermQuery(new Term("text", key));
        return query;
    }

    private Query getNameTermQuery(String key) {
        final Query query = new TermQuery(new Term("name", key));
        return query;
    }

    private Query getNumericRangeQuery() {
        Query query = NumericRangeQuery.newIntRange("count", 1, 10, true, true);
        return query;
    }

    public Query getRangeTermQuery(String key) {
        return NumericRangeQuery.newIntRange("count", 1, 1300, true, true);
        //   return TermRangeQuery.newStringRange("id", key, key, true, true);
    }

    private Query getMultiFieldQuery(String key) throws Throwable {
        // 调用search方法进行检索
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"name", "text"}, IndexConstants.analyzer);
        queryParser.setDefaultOperator(QueryParser.OR_OPERATOR);
        return queryParser.parse(key);
    }

    private Query getBooleanQuery(String key) throws Throwable {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new TermQuery(new Term("text", "中")), BooleanClause.Occur.MUST);
        builder.add(new TermQuery(new Term("text", "华")), BooleanClause.Occur.MUST);
        return builder.build();
    }

    private Query getIntMultiQuery(String key) throws Throwable {
        final BooleanQuery.Builder allQuery = new BooleanQuery.Builder();
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (int i = 0; i < 1000; i++) {
            builder.add(new TermQuery(new Term("id", String.valueOf(i))), BooleanClause.Occur.SHOULD);
        }
        allQuery.add(builder.build(), BooleanClause.Occur.MUST);
        allQuery.add(new TermQuery(new Term("text", "中华")), BooleanClause.Occur.MUST);
        allQuery.add(new TermQuery(new Term("text", "共和国")), BooleanClause.Occur.MUST);
        return allQuery.build();
    }

    private Query getTermRangeQuery(String key) {
        final NumericRangeQuery query = NumericRangeQuery.newIntRange("count", NumericUtils.PRECISION_STEP_DEFAULT_32, 3, 5, true, true);
        return query;
    }

    private Query getPreixTermRangeQuery(String key) {
        final PrefixQuery query = new PrefixQuery(new Term("name", key));
        return query;
    }

    private Query getTermRangeQuerys(String key) {
        final Query query = TermRangeQuery.newStringRange("name", "key", "", true, true);
        return query;
    }

    private final Sort getSort() {
        SortField idSortField = new SortField("id", SortField.Type.STRING, false);
        SortField countSortField = new SortField("count", SortField.Type.INT, false);
        return new Sort(idSortField, countSortField);
    }
}