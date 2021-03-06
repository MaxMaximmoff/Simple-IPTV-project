package com.meMaxMaximmoff.executabe;
/**
 * Created by tgermain on 30/12/2017.
 */
public class ParsingException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int line;

    public ParsingException(int line, String message) {
        super(message + " at line " + line);
        this.line = line;
    }

    public ParsingException(int line, String message, Exception cause) {
        super(message + " at line " + line, cause);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
