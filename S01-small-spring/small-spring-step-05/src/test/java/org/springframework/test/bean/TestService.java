package org.springframework.test.bean;

public class TestService {

    private String id;

    private TestDao testDao;


    public void get(){
        System.out.println();
    }

    public String getId() {
        return id;
    }

    public TestService setId(String id) {
        this.id = id;
        return this;
    }

    public TestDao getTestDao() {
        return testDao;
    }

    public TestService setTestDao(TestDao testDao) {
        this.testDao = testDao;
        return this;
    }
}
