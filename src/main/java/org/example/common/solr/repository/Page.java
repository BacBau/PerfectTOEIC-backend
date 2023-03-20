package org.example.common.solr.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

public class Page<E> {
    private int pageNumber;
    private int pageSize = 10;
    private int pagesAvailable;
    private long totalItems;
    private long time;
    private int startNumber = -1;
    private int endNumber = -1;
    private List<E> pageItems;

    public Page() {
        this.pageItems = new ArrayList(15);
    }

    public Page(int pageNumber, int pagesAvailabe) {
        this.pageNumber = pageNumber;
        this.pagesAvailable = pagesAvailabe;
        this.pageItems = new ArrayList(15);
    }

    public Page(List<E> all, int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageItems = new ArrayList(pageSize);
        this.computePagesAvailable((long)all.size(), pageSize);
        int start = (pageNumber - 1) * pageSize;
        int end = (int)Math.min(this.totalItems, (long)(pageNumber * pageSize));
        if (all.size() <= pageSize) {
            this.pageItems = all;
        } else {
            this.pageItems = new ArrayList();
            if (start > 0 && end > start) {
                this.pageItems.addAll(all.subList(start, end));
            }
        }

    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @JsonIgnore
    public int getPageStart() {
        return Math.max(1, this.pageNumber - 3);
    }

    @JsonIgnore
    public int getPageEnd() {
        return Math.min(this.pagesAvailable, this.pageNumber + 3);
    }

    public int getPagesAvailable() {
        return this.pagesAvailable;
    }

    public void setPagesAvailable(int pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }

    public void computePagesAvailable(long numberOfResult, int size) {
        this.pageSize = size;
        this.totalItems = numberOfResult;
        this.pagesAvailable = 0;
        if (numberOfResult % (long)this.pageSize == 0L) {
            this.pagesAvailable = (int)(numberOfResult / (long)this.pageSize);
        } else {
            this.pagesAvailable = (int)(numberOfResult / (long)this.pageSize) + 1;
        }

    }

    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    public int getStartNumber() {
        if (this.startNumber > -1) {
            return this.startNumber;
        } else {
            this.startNumber = (this.pageNumber - 1) * this.pageSize;
            return this.startNumber;
        }
    }

    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    public int getEndNumber() {
        if (this.endNumber > -1) {
            return this.endNumber;
        } else {
            this.endNumber = this.pageNumber * this.pageSize;
            return this.endNumber;
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<E> getPageItems() {
        return this.pageItems;
    }

    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }

    public long getTotalItems() {
        return this.totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Page<E> clone() {
        Page<E> page = new Page();
        page.pageNumber = this.pageNumber;
        page.pagesAvailable = this.pagesAvailable;
        page.totalItems = this.totalItems;
        page.time = this.time;
        page.pageItems = new ArrayList();
        return page;
    }
}
