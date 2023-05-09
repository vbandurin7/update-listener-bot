package ru.tinkoff.edu.java.bot.bot.command.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.bot.command.validator.CommandValidator;
import ru.tinkoff.edu.java.bot.dto.response.ListLinksResponse;
import ru.tinkoff.edu.java.bot.service.LinkService;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.tinkoff.edu.java.bot.bot.command.commands.Command.LIST;

@Component
@RequiredArgsConstructor
public final class ListProcessor extends AbstractCommandProcessor {

    private final CommandValidator commandValidator;
    private final LinkService linkService;

    @Override
    public String command() {
        return LIST.getCommand();
    }

    @Override
    public String description() {
        return LIST.getDescription();
    }

    @Override
    public SendMessage handle(Update update) {

        if (commandValidator.validateCommand(0, update.message().text()).isEmpty()) {
            return send(update, "Wrong number of arguments.");
        }

        Optional<ListLinksResponse> linkList = linkService.getLinksList(update.message().chat().id());

        return send(update, createMessage(linkList));
    }

    private String createMessage(Optional<ListLinksResponse> linkResponses) {
        if (linkResponses.isEmpty() || linkResponses.get().size() == 0) {
            return "No links for track were added. Try <b>'/track [link]'</b>";
        }
        return "<b>All tracking links are below:</b> " + System.lineSeparator() +
                linkResponses.get().links().stream()
                        .map(l -> "<i>" + l.url().toString() + "</i>")
                        .collect(Collectors.joining(System.lineSeparator()));
    }
}