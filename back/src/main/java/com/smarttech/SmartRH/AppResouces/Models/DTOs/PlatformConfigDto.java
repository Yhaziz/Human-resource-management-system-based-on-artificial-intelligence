package com.smarttech.SmartRH.AppResouces.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlatformConfigDto {
    private boolean aiFeature;
    private boolean smsFeature;
    private String smsUsername;
    private String smsPassword;

    private String apiKey;
    private String model;
    private double temperature;

    // Getters and setters
    public boolean isAiFeature() {
        return aiFeature;
    }

    public void setAiFeature(boolean aiFeature) {
        this.aiFeature = aiFeature;
    }

    public boolean isSmsFeature() {
        return smsFeature;
    }

    public void setSmsFeature(boolean smsFeature) {
        this.smsFeature = smsFeature;
    }

    public String getSmsUsername() {
        return smsUsername;
    }

    public void setSmsUsername(String smsUsername) {
        this.smsUsername = smsUsername;
    }

    public String getSmsPassword() {
        return smsPassword;
    }

    public void setSmsPassword(String smsPassword) {
        this.smsPassword = smsPassword;
    }
}