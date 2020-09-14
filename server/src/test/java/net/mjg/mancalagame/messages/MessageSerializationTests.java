package net.mjg.mancalagame.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.Player;
import net.mjg.mancalagame.game.Score;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MessageSerializationTests {

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        this.mapper = new ObjectMapper();
    }

    @TestFactory
    public Collection<DynamicTest> serializeThenDeserialize() throws JsonProcessingException {
        var cases = Arrays.asList(
                new Case<>(() -> new ErrorMessage("error"), ErrorMessage.class),
                new Case<>(() -> new JoinGameMessage("id"), JoinGameMessage.class),
                new Case<>(() -> new GameStateMessage(new GameState(), new Score(true, 1, 2)), GameStateMessage.class),
                new Case<>(() -> new MoveMessage(1, Player.Blue), MoveMessage.class),
                new Case<>(() -> new NewGameMessage(), NewGameMessage.class)
        );

        return cases.stream().map(c -> DynamicTest.dynamicTest(c.clazz.getSimpleName(), () -> {
            executeTestCase(c);
        }))
                .collect(Collectors.toList());
    }

    private void executeTestCase(Case<? extends Message> c) throws JsonProcessingException {
        Message message = c.factory.get();
        var serialized = mapper.writeValueAsString(message);
        System.out.println(serialized);
        Message deserialized = mapper.readValue(serialized, c.clazz);
        Assertions.assertThat(deserialized).isEqualTo(message);
    }

    private record Case<T>(Supplier<T> factory, Class<T> clazz) {
    }

}
