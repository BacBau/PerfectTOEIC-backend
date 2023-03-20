package org.example.common.solr.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ErrorSaveTempStorage<T> {
    private static final Logger logger = LoggerFactory.getLogger(ErrorSaveTempStorage.class);
    protected Class<T> clazz;
    protected File insertDir;
    protected File errorDir;
    private LazyRepositoryImpl<T, ?> repo;

    public ErrorSaveTempStorage(LazyRepositoryImpl<T, ?> lazyRepositoryImpl, Class<T> clazz) {
        this.clazz = clazz;
        this.repo = lazyRepositoryImpl;
    }

    public void setEnviroment(Environment environment, String schemaName) {
        this.insertDir = this.getDirectory(environment, schemaName, "insert");
        if (!this.insertDir.exists()) {
            this.insertDir.mkdirs();
        }

        this.errorDir = this.getDirectory(environment, schemaName, "insert_error");
        if (!this.errorDir.exists()) {
            this.errorDir.mkdirs();
        }

    }

    public void saveTemp(DataContainer<T> bean) {
        try {
            File file = null;
            if (bean.getErrorTime() > LazyRepositoryImpl.MAX_TIME_TRY_SAVE) {
                file = new File(this.errorDir, this.repo.getId(bean.getValue()) + ".json");
            } else {
                file = new File(this.insertDir, this.repo.getId(bean.getValue()) + ".json");
            }

            String json = (new ObjectMapper()).writeValueAsString(bean);
            Files.write(file.toPath(), json.getBytes("utf8"), new OpenOption[]{StandardOpenOption.WRITE, StandardOpenOption.CREATE});
        } catch (Exception var4) {
            logger.error(var4.getMessage(), var4);
        }

    }

    public List<DataContainer<T>> loadFromTemp() {
        File[] files = this.insertDir.listFiles((f) -> {
            return f.getName().endsWith(".json");
        });
        if (files != null && files.length >= 1) {
            Arrays.sort(files, (f1, f2) -> {
                return (int)(f1.lastModified() - f2.lastModified());
            });
            ObjectMapper mapper = new ObjectMapper();
            List<DataContainer<T>> list = new ArrayList(110);
            File[] var4 = files;
            int var5 = files.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                File file = var4[var6];
                if (!file.isDirectory()) {
                    try {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String json = new String(bytes, "utf8");
                        TypeReference<DataContainer<T>> typeReference = new TypeReference<DataContainer<T>>() {
                        };
                        list.add(mapper.readValue(json, typeReference));
                    } catch (Exception var14) {
                        logger.error(file.getAbsolutePath() + " - " + var14.getMessage(), var14);
                    } finally {
                        file.delete();
                    }

                    if (list.size() > 500) {
                        break;
                    }
                }
            }

            return list;
        } else {
            return Collections.emptyList();
        }
    }

    private File getDirectory(Environment environment, String schemaName, String name) {
        String dataDir = environment.getProperty("data.dir");
        File folder = new File(dataDir, File.separator + "temp" + File.separator + schemaName + File.separator + name + File.separator);

        try {
            folder = folder.getCanonicalFile();
        } catch (Exception var7) {
            logger.error(var7.getMessage(), var7);
        }

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }
}

