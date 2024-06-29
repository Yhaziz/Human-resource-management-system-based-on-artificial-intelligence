package com.smarttech.SmartRH.AppUtils.Sms;

import com.smarttech.SmartRH.AppConfig.PlatformConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;

@Component
public class SmsUtil {

    private final RestTemplate restTemplate;
    @Autowired
    private PlatformConfig platformConfig;

    @Autowired
    public SmsUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public void sendSms(String text, String phoneNumber) {
        try {
            if (platformConfig.isSmsFeature()){
                String apiUrl = "https://smartgateway.tn/apiCode.php";
                String username = platformConfig.getSmsUsername();
                String password = platformConfig.getSmsPassword();
                String encodedText = URLEncoder.encode(text, "UTF-8");


                String url = apiUrl + "?user2=" + username + "&psw=" + password +
                        "&sender=SmartRH&msisdn=" + phoneNumber +
                        "&language=fr&encoding=gsm7&dlr=0&dateDiff&message=" + encodedText;


                String response = restTemplate.getForObject(url, String.class);


                System.out.println("SMS sent. Response: " + response);
            }else {
                System.out.println("Sms Feature is Disabled");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSms(String text, String phoneNumber) {
        try {
            if (platformConfig.isSmsFeature()){
                String apiUrl = "https://smartgateway.tn/apiCode.php";
                String username = platformConfig.getSmsUsername();
                String password = platformConfig.getSmsPassword();
                String encodedText = URLEncoder.encode(text, "UTF-8");


                String url = apiUrl + "?user2=" + username + "&psw=" + password +
                        "&sender=SmartRH&msisdn=" + phoneNumber +
                        "&language=fr&encoding=gsm7&dlr=0&dateDiff&message=" + encodedText;


                String response = restTemplate.getForObject(url, String.class);


                System.out.println("SMS sent. Response: " + response);
            }else {
                System.out.println("Sms Feature is Disabled");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void passwordResetSms(String phoneNumber) {
        try {
            if (platformConfig.isSmsFeature()){
                String apiUrl = "https://smartgateway.tn/apiCode.php";
                String username = platformConfig.getSmsUsername();
                String password = platformConfig.getSmsPassword();
                String encodedText = URLEncoder.encode("Votre mot de passe a été réinitialisé par défaut ( 'votre cin'$mart )", "UTF-8");


                String url = apiUrl + "?user2=" + username + "&psw=" + password +
                        "&sender=SmartRH&msisdn=" + phoneNumber +
                        "&language=fr&encoding=gsm7&dlr=0&dateDiff&message=" + encodedText;


                String response = restTemplate.getForObject(url, String.class);


                System.out.println("SMS sent. Response: " + response);
            }else {
                System.out.println("Sms Feature is Disabled");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}