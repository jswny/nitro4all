public class State {
    private boolean nitroCheck = true;
    private boolean enabled = true;

    public boolean toggleNitroCheck() {
        nitroCheck = !nitroCheck;
        return nitroCheck;
    }

    public boolean getNitroCheck() { return nitroCheck; }

    public boolean toggleEnabled() {
        enabled = !enabled;
        return enabled;
    }

    public boolean getEnabled() { return enabled; }
}
