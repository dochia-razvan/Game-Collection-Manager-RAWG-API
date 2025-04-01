/** Model pentru joc
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String title = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String genre = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String releaseYear = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String image = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String lastUpdateDate = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String description = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String developers = "N/A"; // Can store as a comma-separated string

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String publishers = "N/A"; // Can store as a comma-separated string

    @Column(nullable = true, columnDefinition ="INTEGER DEFAULT 0")
    private Integer metacriticScore = 0;

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String metacriticURL = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String esrbRating = "N/A";

    @Column(nullable = true, columnDefinition ="TEXt DEFAULT 'N/A'")
    private String website = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String platforms = "N/A"; // Can store as a comma-separated string

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String stores = "N/A"; // Can store as a comma-separated string

    @Column(nullable = true, columnDefinition ="BOOLEAN DEFAULT FALSE")
    private Boolean tba = FALSE; // Mark as favorite

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String PCrequirements = "N/A";

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String tags = "N/A";

    @Column(nullable = true, columnDefinition ="INTEGER DEFAULT 0")
    private Integer achievementsCount = 0;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getReleaseYear() {
        return releaseYear;
    }
    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String background_image) {
        this.image = background_image;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDevelopers() {
        return developers;
    }
    public void setDevelopers(String developers) {
        this.developers = developers;
    }
    public String getPublishers() {
        return publishers;
    }
    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }
    public Integer getMetacriticScore() {
        return metacriticScore;
    }
    public void setMetacriticScore(Integer metacriticScore) {
        this.metacriticScore = metacriticScore;
    }
    public String getEsrbRating() {
        return esrbRating;
    }
    public void setEsrbRating(String esrbRating) {
        this.esrbRating = esrbRating;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getPlatforms() {
        return platforms;
    }
    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }
    public String getStores() {
        return stores;
    }
    public void setStores(String stores) {
        this.stores = stores;
    }
    public String getLastUpdateDate() {
        return lastUpdateDate;
    }
    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    public Boolean getTba() {
        return tba;
    }
    public void setTba(Boolean tba) {
        this.tba = tba;
    }
    public String getPCrequirements() {
        return PCrequirements;
    }
    public void setPCrequirements(String PCrequirements) {
        this.PCrequirements = PCrequirements;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public Integer getAchievementsCount() {
        return achievementsCount;
    }
    public void setAchievementsCount(Integer achievementsCount) {
        this.achievementsCount = achievementsCount;
    }
    public String getMetacriticURL() {
        return metacriticURL;
    }
    public void setMetacriticURL(String metacriticURL) {
        this.metacriticURL = metacriticURL;
    }

}
