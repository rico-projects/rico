package dev.rico.internal.remoting.communication.codec;

public interface CodecConstants {

    String CALL_ACTION_COMMAND_ID = "CA";
    String CREATE_CONTEXT_COMMAND_ID = "CCX";
    String CREATE_CONTROLLER_COMMAND_ID = "CC";
    String DESTROY_CONTEXT_COMMAND_ID = "DCX";
    String DESTROY_CONTROLLER_COMMAND_ID = "DC";
    String CREATE_BEAN_COMMAND_ID = "B";
    String CREATE_BEAN_TYPE_COMMAND_ID = "BT";
    String BEAN_REMOVED_COMMAND_ID = "R";
    String VALUE_CHANGED_COMMAND_ID = "V";
    String LIST_ADD_COMMAND_ID = "LA";
    String LIST_REMOVE_COMMAND_ID = "LR";
    String LIST_REPLACE_COMMAND_ID = "LP";
    String ERROR_RESPONSE_COMMAND_ID = "ER";
    String INTERNAL_ERROR_COMMAND_ID = "IE";

    String ID_ATTRIBUTE = "i";
    String COMMAND_TYPE_ATTRIBUTE = "t";
    String NAME_ATTRIBUTE = "n";
    String VALUE_ATTRIBUTE = "v";
    String PARAMS_ATTRIBUTE = "p";
    String PARENT_ATTRIBUTE = "p";
    String MODEL_ATTRIBUTE = "m";
    String CONTROLLER_ATTRIBUTE = "c";
    String BEAN_ATTRIBUTE = "b";
    String CLASS_ATTRIBUTE = "c";
    String REQUEST_ATTRIBUTE = "r";
    String MESSAGE_ATTRIBUTE = "m";
    String COUNT_ATTRIBUTE = "c";
    String FROM_ATTRIBUTE = "f";
    String TO_ATTRIBUTE = "to";
}
