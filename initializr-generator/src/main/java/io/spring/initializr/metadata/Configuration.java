package io.spring.initializr.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;

/**
 * Represents a application configuration
 *
 * @author Jan Schumacher
 */
public class Configuration
{

    private String key;
    private String value;
    private Type type;


    public String getKey()
    {
        return key;
    }


    public void setKey(String key)
    {
        this.key = key;
    }


    public String getValue()
    {
        return value;
    }


    public void setValue(String value)
    {
        this.value = value;
    }


    public Type getType()
    {
        return type;
    }


    public void setType(Type type)
    {
        this.type = type;
    }


    public enum Type {
        APPLICATION,
        BOOTSTRAP
    }
}
