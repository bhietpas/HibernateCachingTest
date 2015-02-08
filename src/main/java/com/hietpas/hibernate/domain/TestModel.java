package com.hietpas.hibernate.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Brad on 2/7/2015.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TestModel {

    @Id
    //@GeneratedValue  // We'll manually set the id for now.
    private long id;
    private String testData;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }
}
