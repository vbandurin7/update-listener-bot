package ru.tinkoff.edu.java.scrapper.persistence.service;

import ru.tinkoff.edu.java.scrapper.persistence.entity.Link;

import java.net.URI;
import java.util.List;

public interface LinkService {
    void save(Link link);
    void delete(String url);
    Link findByUrl(String url);
    void updateTime(Link link);
    List<Link> findAll();
    long count(String url);

    List<Link> findUnchecked();
}
