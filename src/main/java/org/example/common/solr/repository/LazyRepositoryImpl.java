package org.example.common.solr.repository;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.example.common.solr.DataMapper;
import org.example.common.solr.exception.RepositoryException;
import org.example.common.solr.mapper.UpdateField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class LazyRepositoryImpl<T, M extends DataMapper<T>> extends RepositoryImpl<T, M> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LazyRepositoryImpl.class);
    public static int MAX_TIME_TRY_SAVE = 10;
    protected Map<String, DataContainer<T>> saveQueue;
    protected Map<String, DataContainer<UpdateField<?>>> updateQueue;
    protected ErrorSaveTempStorage<T> errorSaveTempStorage;
    protected ErrorUpdateTempStorage errorUpdateTempStorage;
    protected ScheduledExecutorService scheduledExecutor;

    public LazyRepositoryImpl(Environment environment, String schemaName, M mapper, Class<T> clazz) throws MalformedURLException {
        this(environment, schemaName, mapper, clazz, 10, 5);
    }

    public LazyRepositoryImpl(Environment environment, String schemaName, M mapper, Class<T> clazz, int initialDelaySaving, int delaySaving) throws MalformedURLException {
        super(environment, schemaName, mapper);
        this.saveQueue = new ConcurrentHashMap();
        this.updateQueue = new ConcurrentHashMap();
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.scheduledExecutor.scheduleWithFixedDelay(this::storeQueue, (long)initialDelaySaving, (long)delaySaving, TimeUnit.SECONDS);
        this.errorSaveTempStorage = new ErrorSaveTempStorage(this, clazz);
        this.errorSaveTempStorage.setEnviroment(environment, schemaName);
        this.errorUpdateTempStorage = new ErrorUpdateTempStorage();
        this.errorUpdateTempStorage.setEnviroment(environment, schemaName);
    }

    public Class<T> getBeanClass() {
        return this.errorSaveTempStorage.clazz;
    }

    public void save(T bean) throws RepositoryException {
        if (bean == null) {
            LOGGER.error("Found NUll value " + bean);
        } else {
            DataContainer<T> container = new DataContainer(bean);
            String id = this.getId(container.getValue());
            if (id == null) {
                LOGGER.error("Found NUll Id " + bean);
            } else {
                this.saveQueue.put(id, container);
            }
        }
    }

    public void update(String id, UpdateField<?>... fields) {
        List<UpdateField<?>> realtimeUpdates = new ArrayList(fields.length);
        UpdateField[] var4 = fields;
        int var5 = fields.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            UpdateField<?> field = var4[var6];
            if (field.isLazy()) {
                this.updateQueue.put(field.toUniqueIdentifier(), new DataContainer(field));
            } else {
                realtimeUpdates.add(field);
            }
        }

        try {
            if (realtimeUpdates.size() < 1) {
                return;
            }

            super.update(id, (UpdateField[])realtimeUpdates.toArray(new UpdateField[0]));
        } catch (Exception var8) {
            LOGGER.error(var8.toString(), var8);
        }

    }

    protected synchronized void storeQueue() {
        this.storeNewData();
        this.storeUpdateData();
    }

    protected synchronized void storeNewData() {
        if (this.saveQueue.isEmpty()) {
            try {
                List<DataContainer<T>> beans = this.errorSaveTempStorage.loadFromTemp();
                if (!beans.isEmpty()) {
                    LOGGER.info("load from error save storage " + beans.size());
                }

                beans.forEach((bean) -> {
                    DataContainer var10000 = (DataContainer)this.saveQueue.put(this.getId(bean.getValue()), bean);
                });
            } catch (Exception var12) {
                LOGGER.error(var12.getMessage(), var12);
            }
        }

        if (!this.saveQueue.isEmpty()) {
            try {
                Iterator<Map.Entry<String, DataContainer<T>>> iterator = this.saveQueue.entrySet().iterator();
                int counter = 0;

                while(iterator.hasNext()) {
                    Map.Entry<String, DataContainer<T>> entry = (Map.Entry)iterator.next();
                    this.store((DataContainer)entry.getValue());
                    iterator.remove();
                    ++counter;
                    if (counter >= 15) {
                        try {
                            this.commit();
                        } catch (Exception var10) {
                            LOGGER.error(var10.getMessage(), var10);
                        } finally {
                            counter = 0;
                        }
                    }
                }

                this.commit();
            } catch (Exception var13) {
                LOGGER.error(var13.getMessage(), var13);
            }

        }
    }

    protected synchronized void storeUpdateData() {
        if (this.updateQueue.isEmpty()) {
            try {
                List<DataContainer<UpdateField<?>>> beans = this.errorUpdateTempStorage.loadFromTemp();
                if (!beans.isEmpty()) {
                    LOGGER.info("load from error update storage " + beans.size());
                }

                beans.forEach((bean) -> {
                    DataContainer var10000 = (DataContainer)this.updateQueue.put(((UpdateField)bean.getValue()).toUniqueIdentifier(), bean);
                });
            } catch (Exception var15) {
                LOGGER.error(var15.getMessage(), var15);
            }
        }

        if (!this.updateQueue.isEmpty()) {
            try {
                Iterator<Map.Entry<String, DataContainer<UpdateField<?>>>> iterator = this.updateQueue.entrySet().iterator();
                int counter = 0;

                while(iterator.hasNext()) {
                    Map.Entry<String, DataContainer<UpdateField<?>>> entry = (Map.Entry)iterator.next();
                    UpdateField field = (UpdateField)((DataContainer)entry.getValue()).getValue();

                    try {
                        super.update(field.getId(), new UpdateField[]{field});
                    } catch (Exception var14) {
                        LOGGER.error(var14.getMessage(), var14);
                        this.errorUpdateTempStorage.saveTemp((DataContainer)entry.getValue());
                    }

                    iterator.remove();
                    ++counter;
                    if (counter >= 15) {
                        try {
                            this.commit();
                        } catch (Exception var12) {
                            LOGGER.error(var12.getMessage(), var12);
                        } finally {
                            counter = 0;
                        }
                    }
                }

                this.commit();
            } catch (Exception var16) {
                LOGGER.error(var16.getMessage(), var16);
            }

        }
    }

    protected boolean store(DataContainer<T> bean) {
        SolrInputDocument doc = this.mapper.from(bean.getValue());

        try {
            return this.addNoCommit(doc);
        } catch (IOException | SolrServerException var4) {
            LOGGER.error(var4.getMessage(), var4);
            this.errorSaveTempStorage.saveTemp(bean);
            return false;
        }
    }

    public T get(String id) {
        DataContainer<T> bean = (DataContainer)this.saveQueue.get(id);
        return bean == null ? this.get("id", id) : bean.getValue();
    }

    public boolean contains(String id) throws RepositoryException {
        return this.saveQueue.keySet().contains(id) ? true : this.get(id) != null;
    }

    public String deleteById(String id) throws RepositoryException {
        this.saveQueue.remove(id);
        return super.deleteById(id);
    }

    public String deleteById(List<String> ids) throws RepositoryException {
        ids.forEach((id) -> {
            DataContainer var10000 = (DataContainer)this.saveQueue.remove(id);
        });
        return super.deleteById(ids);
    }

    public void destroy() throws Exception {
        Iterator iterator = this.saveQueue.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, DataContainer<T>> entry = (Map.Entry)iterator.next();
            this.errorSaveTempStorage.saveTemp((DataContainer)entry.getValue());
            iterator.remove();
        }

        super.destroy();
    }

    public abstract String getId(T bean);
}

