package com.xxl.registry.store.constants;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author mwup
 * @version 1.0
 * @created 2017/11/27 11:23
 **/
public class IndexConstants {

    public static final int MAX_SIZE = 8;

    public static String INDEX_IDR = "/data/applogs/registry/data/";

    public static Analyzer analyzer =null;// new IKAnalyzer();
}
