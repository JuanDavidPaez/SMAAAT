package SMA.GuardsData;

import BESA.Kernel.Agent.Event.DataBESA;

public class Message extends DataBESA {

    private static int counter = 0;
    public final int id;
    protected String fromAgentAlias;
    protected String toAgentAlias;
    protected Class toGuard;
    protected Class replyGuard;

    public Message(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        if (counter < Integer.MAX_VALUE - 1) {
            counter++;
        } else {
            counter = 0;
        }
        this.id = counter;
        this.fromAgentAlias = fromAgentAlias;
        this.toAgentAlias = toAgentAlias;
        this.toGuard = toGuard;
    }

    public int getId() {
        return id;
    }

    public String fromAgentAlias() {
        return fromAgentAlias;
    }

    public String toAgentAlias() {
        return toAgentAlias;
    }

    public Class toGuard() {
        return toGuard;
    }

    public Class replyGuard() {
        return replyGuard;
    }

    public void setReplyGuard(Class guard) {
        this.replyGuard = guard;
    }

    public void updateToReply() {
        if (replyGuard == null) {
            throw new RuntimeException("El mensaje " + this.toString() + " no tiene configurada una guarda para responder.");
        }
        String aux = fromAgentAlias;
        fromAgentAlias = toAgentAlias;
        toAgentAlias = aux;
        toGuard = replyGuard;
    }

    @Override
    public String toString() {
        return this.getClass() + " Id: " + this.id + " From: " + this.fromAgentAlias + " To: " + this.toAgentAlias;
    }
}