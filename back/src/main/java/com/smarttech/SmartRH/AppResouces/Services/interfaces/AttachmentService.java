package com.smarttech.SmartRH.AppResouces.Services.interfaces;

import com.smarttech.SmartRH.AppResouces.Exceptions.DemandeException;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttachmentService {
    public Attachment addAttachment(MultipartFile file, Long demId) throws IOException, DemandeException;
    public Attachment deleteAttachment(Long attId) throws IOException;
}
