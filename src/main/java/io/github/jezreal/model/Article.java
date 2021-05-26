package io.github.jezreal.model;

import java.time.LocalDate;

public class Article {
    private int article_id;
    private LocalDate dateAcquired;
    private String articleName;
    private String propertyNumber;
    private int quantity;
    private double unitCost;
    private double totalCost;
    private String remarks;

    public Article(
            int article_id,
            LocalDate dateAcquired,
            String articleName,
            String propertyNumber,
            int quantity,
            double unitCost,
            double totalCost,
            String remarks
    ) {
        this.article_id = article_id;
        this.dateAcquired = dateAcquired;
        this.articleName = articleName;
        this.propertyNumber = propertyNumber;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.remarks = remarks;
    }

    public Article(
            LocalDate dateAcquired,
            String articleName,
            String propertyNumber,
            int quantity,
            double unitCost,
            double totalCost,
            String remarks
    ) {
        this.dateAcquired = dateAcquired;
        this.articleName = articleName;
        this.propertyNumber = propertyNumber;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
        this.remarks = remarks;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public LocalDate getDateAcquired() {
        return dateAcquired;
    }

    public void setDateAcquired(LocalDate dateAcquired) {
        this.dateAcquired = dateAcquired;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getPropertyNumber() {
        return propertyNumber;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Article{" +
                "article_id=" + article_id +
                ", dateAcquired=" + dateAcquired +
                ", articleName='" + articleName + '\'' +
                ", propertyNumber='" + propertyNumber + '\'' +
                ", quantity=" + quantity +
                ", unitCost=" + unitCost +
                ", totalCost=" + totalCost +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
