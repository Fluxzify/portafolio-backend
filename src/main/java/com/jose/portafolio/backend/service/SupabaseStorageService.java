package com.jose.portafolio.backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface SupabaseStorageService {
    String uploadFile(MultipartFile file) throws IOException;
    byte[] downloadFile(String fileName) throws IOException;
    void deleteFile(String fileName) throws IOException;
}
