package ru.tinkoff.edu.java.scrapper.persistence.repository.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.tinkoff.edu.java.scrapper.persistence.entity.Chat;
import ru.tinkoff.edu.java.scrapper.persistence.entity.Link;
import ru.tinkoff.edu.java.scrapper.persistence.repository.SubscriptionRepository;

import java.net.URI;
import java.sql.ResultSet;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcSubscriptionRepository implements SubscriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String ADD_RELATION_SQL = "INSERT into chat_link VALUES (?, ?)";
    private static final String DELETE_RELATION_SQL = "DELETE FROM chat_link WHERE chat_id = ? AND link_url = ?";
    private static final String FIND_ALL_BY_CHAT_SQL = "SELECT * FROM link WHERE url IN (SELECT link_url FROM chat_link WHERE chat_id = ?)";
    private static final String FIND_CHATS_BY_URL_SQL = "SELECT * FROM chat WHERE id IN (SELECT chat_id FROM chat_link WHERE link_url = ?)";
    private static final String COUNT_LINK_TRACKS_SQL = "SELECT count(*) FROM chat_link WHERE link_url = ?";
    private static final String COUNT_CHAT_TRACKS_SQL = "SELECT count(*) FROM chat_link WHERE chat_id = ?";

    private static final RowMapper<Link> LINK_ROW_MAPPER = (ResultSet rs, int rownum) ->
    {
        try {
            return new Link(rs.getLong("id"), URI.create(rs.getString("url")),
                    new ObjectMapper().readValue(rs.getString("link_info"), HashMap.class),
                    rs.getTimestamp("last_checked_at").toLocalDateTime().atOffset(ZoneOffset.UTC),
                    rs.getTimestamp("updated_at").toLocalDateTime().atOffset(ZoneOffset.UTC));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };
    private static final RowMapper<Chat> CHAT_ROW_MAPPER = (ResultSet rs, int rownum) -> new Chat(rs.getLong("id"));


    @Override
    public void addRelation(long tgChatId, String linkUrl) {
        jdbcTemplate.update(ADD_RELATION_SQL, tgChatId, linkUrl);
    }

    @Override
    public void deleteRelation(long tgChatId, String linkUrl) {
        jdbcTemplate.update(DELETE_RELATION_SQL, tgChatId, linkUrl);
    }

    @Override
    public List<Link> findAllByChat(long tgChatId) {
        return jdbcTemplate.query(
                FIND_ALL_BY_CHAT_SQL, LINK_ROW_MAPPER, tgChatId
        );
    }

    @Override
    public List<Chat> findChatsByLink(String url) {
        return jdbcTemplate.query(
                FIND_CHATS_BY_URL_SQL, CHAT_ROW_MAPPER, url
        );
    }

    @Override
    public long countLinkTracks(String linkUrl) {
        Long count = jdbcTemplate.queryForObject(COUNT_LINK_TRACKS_SQL, Long.class, linkUrl);
        return count == null ? 0 : count;
    }

    @Override
    public long countChatTracks(Long id) {
        Long count = jdbcTemplate.queryForObject(COUNT_CHAT_TRACKS_SQL, Long.class, id);
        return count == null ? 0 : count;
    }
}
