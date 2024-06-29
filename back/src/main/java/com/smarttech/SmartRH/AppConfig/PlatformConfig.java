package com.smarttech.SmartRH.AppConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class PlatformConfig {

    private boolean aiFeature = false;
    private boolean smsFeature = false;
    private String smsUsername = "benxkhelil@gmail.com";
    private String smsPassword = "smart2024";



    private String apiKey = "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";
    private String model = "gpt-3.5-turbo";
    private double temperature = 0.7;


}