package com.aait.sol.smarthabesha.models;

/**
 * Created by Sol on 5/18/2017.
 */

public class Input {
    public static enum InputType{
        /*
        * the first input for specific functionality
        * eg.. for message: contact name or number
        */
        FIRST,

        /*
        * the second input for specific functionality
        * eg... for message: the content of message
        */
        SECOND,

        /*
        * the third input for specific functionality
        * if it requires.
        */
        THIRD,

        FOURTH
    }
}
