package com.smarttech.SmartRH.AppResouces.Services;

import com.smarttech.SmartRH.AppResouces.Exceptions.DemandeException;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Attachment;
import com.smarttech.SmartRH.AppResouces.Models.Entities.Demande;
import com.smarttech.SmartRH.AppResouces.Repository.AttachmentRepository;
import com.smarttech.SmartRH.AppResouces.Repository.DemandeRepository;
import com.smarttech.SmartRH.AppResouces.Services.interfaces.AttachmentService;
import com.smarttech.SmartRH.AppUtils.FileUpload.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;
    @Override
    public Attachment addAttachment(MultipartFile file, Long demId) throws IOException, DemandeException {
        Demande demande = demandeRepository.findById(demId).orElseThrow(() -> new DemandeException("Demande does not exist"));
        String path = FileUploadUtil.saveFile(file);

        Attachment att = new Attachment();
        att.setPath(path);
        att.setDemande(demande);
        att.setDateUpload(LocalDateTime.now());

        Attachment newAtt = attachmentRepository.save(att);

        return newAtt;
    }

    @Override
    public Attachment deleteAttachment(Long attId) throws IOException {
        Attachment att = attachmentRepository.findById(attId).orElseThrow(() -> new IOException("Attachment does not exist"));
        try {
            FileUploadUtil.deleteFile(att.getPath());
        } catch (IOException e) {
            throw new IOException("Error: " + e.getMessage());
        }
        return att;
    }

}
