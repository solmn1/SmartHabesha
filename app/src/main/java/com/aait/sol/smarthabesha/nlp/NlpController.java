package com.aait.sol.smarthabesha.nlp;

import com.aait.sol.smarthabesha.models.Actions;
import com.aait.sol.smarthabesha.models.Errors;
import com.aait.sol.smarthabesha.models.Input;
import com.aait.sol.smarthabesha.models.Query;

import java.util.ArrayList;

/**
 * Created by Sol on 5/7/2017.
 */

public class NlpController {
    private Query nlpQuery;

    public String[] getCommand(String txt){
        return txt.split(" ");
    }

    public Query getQuery(String trans, Actions.ActionType prevAction, Input.InputType inputType){

        nlpQuery = new Query();
        String[] command = getCommand(trans);

        if(inputType == Input.InputType.FIRST){
            if(command.length == 1){

                String com = command[0];

                /*
                * The command is time
                */
                if(com.compareToIgnoreCase("ሠዓት") ==0 || com.compareToIgnoreCase("ሰዓት")==0 || com.compareToIgnoreCase("ሠአት")==0 || com.compareToIgnoreCase("ሰአት")==0){
                    nlpQuery.setActionType(Actions.ActionType.TIME);
                }

                /*
                * Call command
                */
                else if(com.contains("ደዉ") || com.contains("ደውለው")|| com.contains("ደው") || com.contains("ደዉል") || com.contains("ደውል") || com.contains("ደዉል")|| com.contains("ደውይ") || com.contains("ደዉይ") || com.contains("ደውሉ") || com.contains("ደዉሉ")){
                    nlpQuery.setActionType(Actions.ActionType.CALL);
                }

                /*
                * Message Command
                */
                else if(com.contains("መልክት") || com.contains("መልእክት") || com.contains("መልዓክት") || com.contains("ሜሴጅ") || com.contains("ሜ\u1224ጅ")){
                    nlpQuery.setActionType(Actions.ActionType.MESSAGE);
                }

                /*
                * Email command
                */
                else if(trans.contains("ኢሜል")){
                    nlpQuery.setActionType(Actions.ActionType.EMAIL);
                }

                /*
                *  Reminder Command
                */
                else if(trans.contains("ቀጠሮ")) {
                    nlpQuery.setActionType(Actions.ActionType.ALARM);
                }

                /*
                * Alarm Command
                */
                else if(trans.contains("አስታውኝ")){
                    nlpQuery.setActionType(Actions.ActionType.REMINDER);
                }

                /*
                * Save new contact command
                */
                else if(com.contains("ስልክ")){
                    nlpQuery.setActionType(Actions.ActionType.NEW_CONTACT);
                }
                else{
                    nlpQuery.setErrorType(Errors.ErrorType.UNKNOWN_COMMAND);
                }
            }
        }
        else if(inputType == Input.InputType.SECOND){

            if(prevAction == Actions.ActionType.CALL){}
            else if(prevAction == Actions.ActionType.MESSAGE){}
            else if(prevAction == Actions.ActionType.EMAIL){}
            else if(prevAction == Actions.ActionType.ALARM){}
            else if(prevAction == Actions.ActionType.REMINDER){}
            else if(prevAction == Actions.ActionType.NEW_CONTACT){}
        }
        else if(inputType == Input.InputType.THIRD){

            if(prevAction == Actions.ActionType.MESSAGE){}
            else if(prevAction == Actions.ActionType.EMAIL){}
        }

        return nlpQuery;
    }
}
