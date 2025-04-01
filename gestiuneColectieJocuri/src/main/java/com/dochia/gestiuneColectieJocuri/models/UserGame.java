/** Model pentru a lega jocul de user
 * @author Dochia Dimitrie Razvan
 * @version 12 Ianuarie 2024
 */

package com.dochia.gestiuneColectieJocuri.models;

import jakarta.persistence.*;

import java.util.List;

import static java.lang.Boolean.FALSE;

@Entity
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String stores = "N/A";

    @Column(nullable = true, columnDefinition ="DOUBLE DEFAULT 0.0")
    private double hoursPlayed = 0.0;

    @Column(nullable = true, columnDefinition ="DOUBLE DEFAULT 0.0")
    private double personalRating = 0.0; // User's rating (1-10)

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'N/A'")
    private String review = "N/A"; // User's review or comments

    @Column(nullable = true, columnDefinition ="BOOLEAN DEFAULT FALSE")
    private Boolean favorite = FALSE; // Mark as favorite

    @Column(nullable = true, columnDefinition ="TEXT DEFAULT 'Not Started'")
    private String completionStatus = "Not Started"; // Not Started, In Progress, Completed

    @Column(nullable = true, columnDefinition ="INTEGER DEFAULT 0")
    private Integer userAchievementsCount = 0;

    @Column(nullable = true, columnDefinition ="DOUBLE DEFAULT 0")
    private double completionPercentage = 0.0;

    @Transient
    private List<String> availableStores;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Users getUser() {
        return user;
    }
    public void setUser(Users user) {
        this.user = user;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public String getStores() {
        return stores;
    }
    public void setStores(String stores) {
        this.stores = stores;
    }
    public double getHoursPlayed() {
        return hoursPlayed;
    }
    public void setHoursPlayed(Double hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }
    public double getPersonalRating() { return personalRating; }
    public void setPersonalRating(Double personalRating) { this.personalRating = personalRating; }
    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public Boolean getFavorite() {
        return favorite;
    }
    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
    public String getCompletionStatus() {
        return completionStatus;
    }
    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }
    public Integer getUserAchievementsCount() {
        return userAchievementsCount;
    }
    public void setUserAchievementsCount(Integer userAchievementsCount) {
        this.userAchievementsCount = userAchievementsCount;
    }
    public double getCompletionPercentage() {
        return completionPercentage;
    }
    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    public List<String> getAvailableStores() {
        return availableStores;
    }
    public void setAvailableStores(List<String> availableStores) {
        this.availableStores = availableStores;
    }
}
