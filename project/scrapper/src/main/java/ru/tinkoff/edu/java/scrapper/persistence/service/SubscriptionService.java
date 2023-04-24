package ru.tinkoff.edu.java.scrapper.persistence.service;

import ru.tinkoff.edu.java.scrapper.persistence.entity.Chat;
import ru.tinkoff.edu.java.scrapper.persistence.entity.Link;

import java.net.URI;
import java.util.List;

public interface SubscriptionService {
    Link addLink(long tgChatId, URI url);
    Link removeLink(long tgChatId, URI url);
    List<Chat> chatList(String url);
    List<Link> listAll(long tgChatId);

}