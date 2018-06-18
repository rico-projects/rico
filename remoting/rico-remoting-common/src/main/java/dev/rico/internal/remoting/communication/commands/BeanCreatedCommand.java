package dev.rico.internal.remoting.communication.commands;

import java.util.ArrayList;
import java.util.List;

public final class BeanCreatedCommand implements Command {

    private String beanId;

    private String beanType;

    private final List<CreatedPropertyInfo> properties;

    private final List<CreatedPropertyInfo> lists;

    public BeanCreatedCommand() {
        properties = new ArrayList<>();
        lists = new ArrayList<>();
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getBeanType() {
        return beanType;
    }

    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    public List<CreatedPropertyInfo> getProperties() {
        return properties;
    }

    public List<CreatedPropertyInfo> getLists() {
        return lists;
    }

    public class CreatedPropertyInfo {

        private String propertyId;

        private String propertyName;

        private String propertyType;

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }
    }

}
