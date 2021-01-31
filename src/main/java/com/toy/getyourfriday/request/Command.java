package com.toy.getyourfriday.request;

import com.toy.getyourfriday.utils.BotNameExtractor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum Command {
    MODELS("/models$"),
    REGISTER("/register [A-z]+"),
    REMOVE("/remove$");

    private static final Map<Command, Pattern> BY_PATTERN;

    static {
        BY_PATTERN = new HashMap<>();
        for (Command command : Command.values()) {
            BY_PATTERN.put(command, Pattern.compile(command.getRegex()));
        }
    }

    private final String regex;

    Command(String command) {
        this.regex = command;
    }

    public static Command from(String text) {
        String line = BotNameExtractor.extractBotName(text);
        return Arrays.stream(Command.values())
                .filter(c -> BY_PATTERN.get(c).matcher(line).find())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 명령어입니다."));
    }

    public String extractCommand(String text) throws IllegalAccessException {
        if (this.equals(Command.REGISTER)) {
            String line = BotNameExtractor.extractBotName(text);
            return line.replace("/register ", "");
        }
        throw new IllegalAccessException("Only '/register' Command is Allowed");
    }

    public String getRegex() {
        return regex;
    }
}
