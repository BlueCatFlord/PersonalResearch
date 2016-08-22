package com.schaffer.zookeeper.bridger.config;

import com.schaffer.zookeeper.bridger.except.DBConnException;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by schaffer on 2016/8/17.
 */
public class ConfigInject {

    private Logger logger = Logger.getLogger(ConfigInject.class);

    private static ConfigInject configInject;

    private ConfigInject() {
    }

    public synchronized static ConfigInject getInstance() {
        if (configInject == null) {
            configInject = new ConfigInject();
        }
        return configInject;
    }

    public Configuration properties2Config(String configFile) {
        InputStream inputStream = null;
        Configuration configuration = new Configuration();
        try {
            inputStream = new BufferedInputStream(new FileInputStream(configFile));
            Properties properties = new Properties();
            properties.load(inputStream);
            Method[] methods = configuration.getClass().getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("set")) {
                    String tmp = methodName.substring(4);
                    String first = methodName.substring(3, 4);
                    String key = first.toLowerCase() + tmp;
                    String propertyV = properties.getProperty(key);
                    if (propertyV != null) {
                        Class<?>[] parameterTyps = method.getParameterTypes();
                        if (parameterTyps != null && parameterTyps.length != 0) {
                            String ptFirst = parameterTyps[0].getSimpleName();
                            Object arg;
                            if ("int".equals(ptFirst)) {
                                arg = Integer.parseInt(propertyV);
                            } else if ("long".equals(ptFirst)) {
                                arg = Long.parseLong(propertyV);
                            } else if ("String".equals(ptFirst)) {
                                arg = propertyV;
                            } else if ("boolean".equals(ptFirst)) {
                                arg = Boolean.parseBoolean(propertyV);
                            } else if ("double".equals(ptFirst)) {
                                arg = Double.parseDouble(propertyV);
                            } else
                                continue;
                            method.invoke(configuration, new Object[]{arg});
                        }
                    }
                }
            }
        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
            configuration = null;
            throw new DBConnException(e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            return configuration;
        }
    }

    public static void main(String args[]) {
        String configFile = ConfigInject.class.getClassLoader().getResource("config.properties").getFile();
        ConfigInject configInject = ConfigInject.getInstance();
        Configuration configuration = configInject.properties2Config(configFile);
        System.out.println(configuration.toString());
    }
}
