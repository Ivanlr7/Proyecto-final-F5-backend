package dev.ivan.reviewverso_back.implementations;

public interface IRegisterService<T,S> {
    S register(T request);
}
