package fr.tobby.tripnjoyback.model.websocket;

public class Greeting {

    private String content;

    public Greeting(final String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }
}
