package com.xxl.registry.store.manager;

import com.xxl.registry.store.constants.IndexConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mwup
 * @version 1.0
 * @created 2017/11/29 19:11
 **/
public final class IndexManager {

    private String indexName;

    private IndexWriter indexWriter;

    private SearcherManager searcherManager;

    private ControlledRealTimeReopenThread controlledRealTimeReopenThread;

    private IndexCommitThread indexCommitThread;

    private TrackingIndexWriter trackingIndexWriter;

    private Analyzer analyzer = new IKAnalyzer();

    private static IndexManager INSTANCE = new IndexManager("registry");

    public static IndexManager getInstance() {
        return INSTANCE;
    }

    private IndexManager(String indexName) {
        this.indexName = indexName;
        init();
    }

    private void init() {
        try {
            final String indexFile = IndexConstants.INDEX_IDR;
            final File file = new File(indexFile);
            if (!file.exists()) {
                file.mkdir();
            }
            final Directory directory = NIOFSDirectory.open(Paths.get(indexFile));
            final IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            this.indexWriter = new IndexWriter(directory, config);
            this.trackingIndexWriter = new TrackingIndexWriter(indexWriter);
            this.searcherManager = new SearcherManager(indexWriter, true, null);
            setThread();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UnsupportedOperationException(ex.getMessage(), Optional.ofNullable(ex.getCause()).orElse(ex));
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (null != indexWriter) {
                        indexWriter.close();
                    }
                    if (null != searcherManager) {
                        searcherManager.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }));
        }
    }

    public TrackingIndexWriter getIndexWriter() {
        return trackingIndexWriter;
    }

    public IndexSearcher getIndexSearcher() {
        try {
            this.searcherManager.maybeRefresh();
            return searcherManager.acquire();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void release(IndexSearcher indexSearcher) {
        try {
            if (null != indexSearcher) {
                searcherManager.release(indexSearcher);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setThread() {
        //
        this.controlledRealTimeReopenThread = new ControlledRealTimeReopenThread(trackingIndexWriter, searcherManager, 10, 0.25);
        controlledRealTimeReopenThread.setName("reopenThread");
        controlledRealTimeReopenThread.setDaemon(true);
        controlledRealTimeReopenThread.start();

        //将索引写入硬般
        this.indexCommitThread = new IndexCommitThread();
        indexCommitThread.setName("indexCommitThread");
        indexCommitThread.setDaemon(true);
        indexCommitThread.start();
    }

    /**
     * 将索引写入disk
     */
    private class IndexCommitThread extends Thread {

        private boolean flag = false;

        @Override
        public void run() {
            this.flag = true;
            while (this.flag) {
                try {
                    IndexManager.this.indexWriter.commit();
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception ex) {
                    //quite
                }
            }
            super.run();
        }
    }
}
