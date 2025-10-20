package dev.ivan.reviewverso_back.implementations;

import java.util.List;

public interface IReviewService <T, S> {
    public T createEntity(S dto);
    public List<T> getEntities();
    public T getByID(Long id);
    public T updateEntity(Long id, S dto);
    public void deleteEntity(Long id);
    
} 