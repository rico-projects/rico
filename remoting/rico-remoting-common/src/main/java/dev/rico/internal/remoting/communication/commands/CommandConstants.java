package dev.rico.internal.remoting.communication.commands;

public interface CommandConstants {

    String CALL_ACTION_COMMAND_ID = "CA";
    String CREATE_CONTEXT_COMMAND_ID = "CCX";
    String CREATE_CONTROLLER_COMMAND_ID = "CC";
    String DESTROY_CONTEXT_COMMAND_ID = "DCX";
    String DESTROY_CONTROLLER_COMMAND_ID = "DC";
    String BEAN_CREATED_COMMAND_ID = "B";
    String BEAN_REMOVED_COMMAND_ID = "R";
    String VALUE_CHANGED_COMMAND_ID = "V";
    String LIST_SPLICE_COMMAND_ID = "S";

    String COMMAND_ID_ATTRIBUTE = "i";
    String NAME_ATTRIBUTE = "n";
    String VALUE_ATTRIBUTE = "v";
    String PARAMS_ATTRIBUTE = "p";
    String CONTROLLER_ID_ATTRIBUTE = "c";
}
