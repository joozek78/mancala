package net.mjg.mancalagame.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class MessageContainer {
    private static final String MESSAGE_TYPE_NEW = "NEW";
    private static final String MESSAGE_TYPE_JOIN = "JOIN";
    private static final String MESSAGE_TYPE_MOVE = "MOVE";
    private static final String MESSAGE_TYPE_ERROR = "ERROR";
    private static final String MESSAGE_TYPE_STATE = "STATE";
    private static final String MESSAGE_TYPE_FINISHED = "FINISHED";

    private static final BiMap<String, Class> typeToClass = HashBiMap.create();
    private static final Map<Class, String> classToType;

    static {
        typeToClass.put(MESSAGE_TYPE_NEW, NewGameMessage.class);
        typeToClass.put(MESSAGE_TYPE_JOIN, JoinGameMessage.class);
        typeToClass.put(MESSAGE_TYPE_MOVE, MoveMessage.class);
        typeToClass.put(MESSAGE_TYPE_ERROR, ErrorMessage.class);
        typeToClass.put(MESSAGE_TYPE_STATE, GameStateMessage.class);
        typeToClass.put(MESSAGE_TYPE_FINISHED, GameFinishedMessage.class);
        classToType = typeToClass.inverse();
    }

    private String type;
    private ObjectNode message;

    public MessageContainer(String type, ObjectNode message) {
        if (!typeToClass.containsKey(type)) {
            throw new IllegalArgumentException("Unrecognized message type: " + type);
        }
        this.type = type;
        this.message = message;
    }

    public static MessageContainer wrap(Message message, ObjectMapper mapper) {
        String type = classToType.get(message.getClass());
        if (type == null) {
            throw new IllegalArgumentException("Unrecognized message type: " + message.getClass());
        }
        return new MessageContainer(type, mapper.valueToTree(message));
    }

    public Message unwrap(ObjectMapper mapper) throws JsonProcessingException {
        Class targetClass = typeToClass.get(type);
        return (Message) mapper.treeToValue(message, targetClass);
    }
}
