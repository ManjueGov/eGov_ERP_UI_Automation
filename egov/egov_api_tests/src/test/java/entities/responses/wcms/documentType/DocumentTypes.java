package entities.responses.wcms.documentType;

public class DocumentTypes {
    private String code;
    private Object auditDetails;
    private String name;
    private String tenantId;
    private String description;
    private boolean active;
    private int id;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getAuditDetails() {
        return this.auditDetails;
    }

    public void setAuditDetails(Object auditDetails) {
        this.auditDetails = auditDetails;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
