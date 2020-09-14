package net.mjg.mancalagame.messages;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@JsonSerialize
@EqualsAndHashCode
public class NewGameMessage implements Message {
}

