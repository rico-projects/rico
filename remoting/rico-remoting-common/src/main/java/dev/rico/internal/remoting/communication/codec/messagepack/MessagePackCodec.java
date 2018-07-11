package dev.rico.internal.remoting.communication.codec.messagepack;

import dev.rico.internal.core.http.ConnectionUtils;
import dev.rico.internal.remoting.communication.codec.CodecException;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.*;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.rico.internal.remoting.communication.codec.CodecConstants.*;

public class MessagePackCodec {

    private void pack(final MessagePacker messagePacker, final Object value) throws IOException {
        if (value == null) {
            messagePacker.packNil();
        } else if (value instanceof String) {
            messagePacker.packString((String) value);
        } else if (value instanceof Integer) {
            messagePacker.packInt((int) value);
        } else if (value instanceof Double) {
            messagePacker.packDouble((double) value);
        } else if (value instanceof Boolean) {
            messagePacker.packBoolean((boolean) value);
        } else if (value instanceof Collection) {
            messagePacker.packArrayHeader(((Collection) value).size());
            for (Object o : ((Collection) value)) {
                pack(messagePacker, o);
            }
        }
    }

    private void pack(final MessagePacker messagePacker, final String key, final Object value) throws IOException {
        pack(messagePacker, key);
        pack(messagePacker, value);
    }

    private void encodeCreateBeanCommand(final CreateBeanCommand command, final MessagePacker messagePacker) throws IOException {
        messagePacker.packMapHeader(4);
        pack(messagePacker, COMMAND_TYPE_ATTRIBUTE, CREATE_BEAN_COMMAND_ID);
        pack(messagePacker, ID_ATTRIBUTE, command.getUniqueIdentifier());
        pack(messagePacker, BEAN_ATTRIBUTE, command.getBeanId());
        pack(messagePacker, CLASS_ATTRIBUTE, command.getClassId());
    }

    private void encodeDeleteBeanCommand(final DeleteBeanCommand command, final MessagePacker messagePacker) throws IOException {
        messagePacker.packMapHeader(3);
        pack(messagePacker, COMMAND_TYPE_ATTRIBUTE, CREATE_BEAN_COMMAND_ID);
        pack(messagePacker, ID_ATTRIBUTE, command.getUniqueIdentifier());
        pack(messagePacker, BEAN_ATTRIBUTE, command.getBeanId());
    }

    private void encodeValueChangedCommand(final ValueChangedCommand command, final MessagePacker messagePacker) throws IOException {
        messagePacker.packMapHeader(5);
        pack(messagePacker, COMMAND_TYPE_ATTRIBUTE, VALUE_CHANGED_COMMAND_ID);
        pack(messagePacker, ID_ATTRIBUTE, command.getUniqueIdentifier());
        pack(messagePacker, BEAN_ATTRIBUTE, command.getBeanId());
        pack(messagePacker, NAME_ATTRIBUTE, command.getPropertyName());
        pack(messagePacker, VALUE_ATTRIBUTE, command.getNewValue());
    }

    private void encodeListAddCommand(final ListAddCommand command, final MessagePacker messagePacker) throws IOException {
        messagePacker.packMapHeader(5);
        pack(messagePacker, COMMAND_TYPE_ATTRIBUTE, LIST_ADD_COMMAND_ID);
        pack(messagePacker, ID_ATTRIBUTE, command.getUniqueIdentifier());
        pack(messagePacker, BEAN_ATTRIBUTE, command.getBeanId());
        pack(messagePacker, NAME_ATTRIBUTE, command.getListName());
        pack(messagePacker, FROM_ATTRIBUTE, command.getStart());
        pack(messagePacker, VALUE_ATTRIBUTE, command.getValues());
    }

    private void encodeListReplaceCommand(final ListReplaceCommand command, final MessagePacker messagePacker) throws IOException {
        messagePacker.packMapHeader(5);
        pack(messagePacker, COMMAND_TYPE_ATTRIBUTE, LIST_REPLACE_COMMAND_ID);
        pack(messagePacker, ID_ATTRIBUTE, command.getUniqueIdentifier());
        pack(messagePacker, BEAN_ATTRIBUTE, command.getBeanId());
        pack(messagePacker, NAME_ATTRIBUTE, command.getListName());
        pack(messagePacker, FROM_ATTRIBUTE, command.getStart());
        pack(messagePacker, VALUE_ATTRIBUTE, command.getValues());
    }

    private void encodeCommand(final Command command, final MessagePacker messagePacker) throws IOException {
        if (command instanceof ValueChangedCommand) {
            encodeValueChangedCommand((ValueChangedCommand) command, messagePacker);
        } else if (command instanceof CreateBeanCommand) {
            encodeCreateBeanCommand((CreateBeanCommand) command, messagePacker);
        }
    }

    public String encode(final List<? extends Command> commands) throws CodecException {
        try {
            MessageBufferPacker messagePacker = MessagePack.newDefaultBufferPacker();
            final int size = commands.size();
            messagePacker.packArrayHeader(size);
            for (final Command c : commands) {
                encodeCommand(c, messagePacker);
            }
            messagePacker.flush();
            final byte[] encoded = messagePacker.toByteArray();
            return ConnectionUtils.toBase64(encoded);
        } catch (Exception e) {
            throw new CodecException("Can not encode commands", e);
        }
    }


    private ListAddCommand decodeListAddCommand(final MessageUnpacker messageUnpacker, final String uniqueCommandId, final int keyValueCount) throws IOException {
        final ListAddCommand command = new ListAddCommand(uniqueCommandId);
        IntStream.of(keyValueCount)
                .mapToObj(i -> {
                    try {
                        return unpack(messageUnpacker);
                    } catch (IOException e) {
                        throw new RuntimeException("error in unpack!", e);
                    }
                })
                .forEach(keyValue -> {
                    if (Objects.equals(BEAN_ATTRIBUTE, keyValue.getKey())) {
                        command.setBeanId(keyValue.getValueAsString());
                    } else if (Objects.equals(NAME_ATTRIBUTE, keyValue.getKey())) {
                        command.setListName(keyValue.getValueAsString());
                    } else if (Objects.equals(FROM_ATTRIBUTE, keyValue.getKey())) {
                        command.setStart(keyValue.getValueAsInt());
                    } else if (Objects.equals(VALUE_ATTRIBUTE, keyValue.getKey())) {
                        command.getValues().addAll(keyValue.getValueAsCollection());
                    }
                });
        return command;
    }

    private Object convert(final Value value) throws IOException {
        if (value.isNilValue()) {
            return null;
        } else if (value.isStringValue()) {
            return value.asStringValue().asString();
        } else if (value.isIntegerValue()) {
            return value.asIntegerValue().asInt();
        } else if (value.isBooleanValue()) {
            return value.asBooleanValue().getBoolean();
        } else if (value.isArrayValue()) {
            return value.asArrayValue().list().stream()
                    .map(v -> {
                        try {
                            return convert(v);
                        } catch (IOException e) {
                            throw new RuntimeException("Error in conversion of array element", e);
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Unsupported type: " + value.getValueType().toString());
        }
    }

    private KeyValue unpack(MessageUnpacker messageUnpacker) throws IOException {
        final String key = messageUnpacker.unpackString();
        final Object value = convert(messageUnpacker.unpackValue());
        return new KeyValue() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public String getValueAsString() {
                return (String) value;
            }

            @Override
            public int getValueAsInt() {
                return (int) value;
            }

            @Override
            public boolean getValueAsBoolean() {
                return (boolean) value;
            }

            @Override
            public Collection getValueAsCollection() {
                return (Collection) value;
            }
        };

    }

    private interface KeyValue {

        String getKey();

        Object getValue();

        String getValueAsString();

        int getValueAsInt();

        boolean getValueAsBoolean();

        Collection getValueAsCollection();
    }


    public List<Command> decode(final String transmitted) throws CodecException {
    }

}
