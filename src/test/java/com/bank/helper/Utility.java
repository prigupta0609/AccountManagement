package com.bank.helper;

import com.bank.dao.DAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class Utility {

    private static final String dataLocation = "src/test/resources/dummyData.yml";

    public static DAO getDummyData () {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        DAO dao = null;
        try {
            dao = mapper.readValue(new File(dataLocation), DAO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dao;
    }
}
