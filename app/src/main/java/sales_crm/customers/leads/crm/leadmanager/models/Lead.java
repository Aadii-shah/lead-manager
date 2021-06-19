package sales_crm.customers.leads.crm.leadmanager.models;

import java.io.Serializable;

public class Lead implements Serializable {
    String status;
    String source;
    String description;
    String lfd;

    public String getLfd() {
        return lfd;
    }

    public void setLfd(String lfd) {
        this.lfd = lfd;
    }

    public String getContactUid() {
        return contactUid;
    }

    public void setContactUid(String contactUid) {
        this.contactUid = contactUid;
    }

    String contactUid;

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLatestFollowup() {
        return latestFollowup;
    }

    public void setLatestFollowup(long latestFollowup) {
        this.latestFollowup = latestFollowup;
    }

    public NoteItem[] getNotes() {
        return notes;
    }

    public void setNotes(NoteItem[] notes) {
        this.notes = notes;
    }

    public DealItem[] getDeals() {
        return deals;
    }

    public void setDeals(DealItem[] deals) {
        this.deals = deals;
    }

    public HistoryItem[] getHistory() {
        return history;
    }

    public void setHistory(HistoryItem[] history) {
        this.history = history;
    }

    private long creationDate;
    private long latestFollowup;
    private NoteItem[] notes;
    private DealItem[] deals;
    private HistoryItem[] history;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
