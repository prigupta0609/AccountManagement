package com.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.bank.dao.DAO;
import com.bank.resources.AccountManagementResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AccountManagementApplication extends Application<ApplicationConfiguration> {

    private static Logger LOGGER = LoggerFactory.getLogger(AccountManagementApplication.class);

    public static void main(String[] args) throws Exception {
        new AccountManagementApplication().run(args);
    }

    @Override
    public void run(ApplicationConfiguration applicationConfiguration, Environment environment) throws Exception {
        DAO dao = cacheDummyData(applicationConfiguration);
        AccountManagementResource resource = new AccountManagementResource(dao);
        environment.jersey().register(resource);
    }

    private DAO cacheDummyData(ApplicationConfiguration applicationConfiguration) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        DAO dao = null;
        try {
            dao = mapper.readValue(new File(applicationConfiguration.getDummyDataLocation()), DAO.class);
        } catch (IOException e) {
            LOGGER.error("Database not loaded. Couldn't start application. Application shut downF!");
            e.printStackTrace();
        }
        return dao;
    }
}
