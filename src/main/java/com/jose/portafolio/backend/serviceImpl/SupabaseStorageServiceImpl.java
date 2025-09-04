package com.jose.portafolio.backend.serviceImpl;

import com.jose.portafolio.backend.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_KEY}")
    private String supabaseKey;

    @Value("${SUPABASE_BUCKET}")
    private String bucketName;

    private final WebClient webClient;

    public SupabaseStorageServiceImpl() {
        this.webClient = WebClient.builder().build();
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .contentType(MediaType.valueOf(file.getContentType()))
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // bloquea hasta que termine (sync)

        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        String url = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
