package dev.ivan.reviewverso_back.implementations;

import org.springframework.web.multipart.MultipartFile;

public interface IRegisterService<T,S> {
    S register(T request, MultipartFile profileImage);
}
