package com.happypeople.vi;

/** This models the writable part of a LinesModel
 */
public interface LinesModelEditor extends LinesModel {
    /** replace content of line, edit */
    void replace(long lineNo, String newVersionOfLine);

    /** create new line */
    void insertAfter(long lineNo, String newLine);
    void insertBefore(long lineNo, String newLine);

    /** remove a line */
    void remove(long lineNo);
}
