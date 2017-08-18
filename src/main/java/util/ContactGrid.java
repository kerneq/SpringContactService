package util;

import entity.Contact;

import java.util.List;

/**
 * Created by iters on 8/18/17.
 */
public class ContactGrid {
    private int totalPages;
    private int currentPage;
    private int totalRecords;
    private List<Contact> contactData;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<Contact> getContactData() {
        return contactData;
    }

    public void setContactData(List<Contact> contactData) {
        this.contactData = contactData;
    }
}