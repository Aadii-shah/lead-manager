package sales_crm.customers.leads.crm.leadmanager.models;

import java.io.Serializable;

public class NoteItem implements Serializable {

    private String description;
    private long date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
