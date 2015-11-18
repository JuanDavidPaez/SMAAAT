package SMA.GuardsData;

import BESA.Kernel.Agent.Event.DataBESA;

public class Message extends DataBESA {

    protected String fromAgentAlias;
    protected String toAgentAlias;
    protected Class toGuard;
    protected Class replyGuard;

    public Message(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        this.fromAgentAlias = fromAgentAlias;
        this.toAgentAlias = toAgentAlias;
        this.toGuard = toGuard;
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
        return this.getClass() + " From: " + this.fromAgentAlias + " To: " + this.toAgentAlias ;
    }
    
}