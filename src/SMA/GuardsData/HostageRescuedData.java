package SMA.GuardsData;

public class HostageRescuedData extends Message {

    public HostageRescuedData(String fromAgentAlias, String toAgentAlias, Class toGuard) {
        super(fromAgentAlias, toAgentAlias, toGuard);
    }
}
