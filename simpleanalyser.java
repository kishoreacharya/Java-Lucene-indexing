package lab_one;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
//import org.apache.lucene.analysis.core.WhitespaceAnalyzer;


public class simpleanalyser {

    private simpleanalyser() {
    }

    
    public static void main(String[] args) {
        String usage = "java org.apache.lucene.demo.IndexFiles"
                + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                + "in INDEX_PATH that can be searched with SearchFiles";
        //String indexPath = "index";
        //String docsPath = null;
        String indexPath = "C:/Users/HP/Desktop/idx_new/";
        String docsPath = "C:/Users/HP/Desktop/IR/mini_newsgroups/misc.forsale/";
        boolean create = true;
        for (int i = 0; i < args.length; i++) {
            if ("-index".equals(args[i])) {
                indexPath = args[i + 1];
                i++;
            } else if ("-docs".equals(args[i])) {
                docsPath = args[i + 1];
                i++;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        final File docDir = new File(docsPath);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '" + docDir.getAbsolutePath() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");

            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_4_10_0);            
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);

            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocsWrite(writer, docDir);
            writer.close();

            Date end_time = new Date();
            System.out.println(end_time.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }
    }

   
    static void indexDocsWrite(IndexWriter writer, File file)
            throws IOException {
        
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
          
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                    	indexDocsWrite(writer, new File(file, files[i]));
                    }
                }
            } else {

                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
   
                    return;
                }

                try {

                    Document doc = new Document();
                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);

                    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
                    String sCurrentLine;

                    while ((sCurrentLine = br.readLine()) != null) {
                        if (sCurrentLine.startsWith("From:")) {
                            doc.add(new TextField("From", sCurrentLine, Field.Store.NO));
                        } else if (sCurrentLine.startsWith("Subject:")) {
                            doc.add(new TextField("Subject", sCurrentLine, Field.Store.NO));
                        } else if (sCurrentLine.startsWith("Date:")) {
                            doc.add(new TextField("Date", sCurrentLine, Field.Store.NO));
                        } else {
                            doc.add(new TextField("contents", sCurrentLine, Field.Store.NO));
                        }
                    }

                    
                    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                        
                        System.out.println("adding " + file);
                        writer.addDocument(doc);
                    } else {
           
                        System.out.println("updating " + file);
                        writer.updateDocument(new Term("path", file.getPath()), doc);
                    }

                } finally {
                    fis.close();
                }
            }
        }
    }
}
