package com.example.recyclerview.Bean;

public class NotepadBean {
    private String id;
    //private String notepadTitle;  //记录的标题
    private String notepadContent;//记录的内容
    private String notepadTime;//保存记录的时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //public String getNotepadTitle(){return notepadTitle;}

    //public void setNotepadTitle() {this.notepadTitle = notepadTitle;}
    public String getNotepadContent() {
        return notepadContent;
    }

    public void setNotepadContent(String notepadContent) {
        this.notepadContent = notepadContent;
    }

    public String getNotepadTime() {
        return notepadTime;
    }

    public void setNotepadTime(String notepadTime) {
        this.notepadTime = notepadTime;
    }
}
