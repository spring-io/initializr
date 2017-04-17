package io.spring.initializr.util;

import io.spring.initializr.generator.ProjectGenerator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by ali on 14/03/2017.
 */
public class ZipUtils {
    private static final Logger log = LoggerFactory.getLogger(ZipUtils.class);

    public static void unzipArchive(File archive, File outputDir,Map<String, Object> model) {
        try {
            TemplateRenderer templateRenderer = new TemplateRenderer("");
            templateRenderer.setCache(false);
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, outputDir,templateRenderer,model);
            }
        } catch (Exception e) {
            log.error("Error extracting file " + archive, e);
        }
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir,TemplateRenderer templateRenderer,Map<String, Object> model) throws IOException {
        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()){
            createDir(outputFile.getParentFile());
        }

        log.debug("Extracting entry :"+entry);
        File template = new File(outputFile+".tpl");
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(template));

        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.close();
            inputStream.close();
        }
        processFile(outputFile,template,templateRenderer, model);
        template.delete();
    }

    private static void processFile(File target,File template,TemplateRenderer templateRenderer,Map<String, Object> model){
        String body = templateRenderer.process(template.getAbsolutePath(), model);

        try (OutputStream stream = new FileOutputStream(target)) {
            StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
        }
        catch (Exception e) {
            throw new IllegalStateException("Cannot write file " + target, e);
        }
    }

    private static void createDir(File dir) {
        log.debug("Creating directory: "+dir.getName());
        if(!dir.mkdirs()) throw new RuntimeException("Can not create dir "+dir);
    }
}
