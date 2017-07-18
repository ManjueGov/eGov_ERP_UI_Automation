package builders.wcms.propertyCategoryType;

import entities.requests.wcms.propertyCategoryType.create.PropertyTypeCategoryType;

import static data.SearchParameterData.TENANT_DEFAULT;

public class PropertyCategoryBuilder {
    PropertyTypeCategoryType propertyTypeCategoryType = new PropertyTypeCategoryType();

    public PropertyCategoryBuilder() {
        propertyTypeCategoryType.setActive("true");
        propertyTypeCategoryType.setTenantId(TENANT_DEFAULT);
    }

    public PropertyCategoryBuilder withPropertyTypeName(String propertyTypeName) {
        propertyTypeCategoryType.setPropertyTypeName(propertyTypeName);
        return this;
    }

    public PropertyCategoryBuilder withCategoryTypeName(String categoryTypeName) {
        propertyTypeCategoryType.setCategoryTypeName(categoryTypeName);
        return this;
    }

    public PropertyCategoryBuilder withTenantId(String tenantId) {
        propertyTypeCategoryType.setTenantId(tenantId);
        return this;
    }

    public PropertyCategoryBuilder withActive(String active) {
        propertyTypeCategoryType.setActive(active);
        return this;
    }

    public PropertyTypeCategoryType build() {
        return propertyTypeCategoryType;
    }
}
