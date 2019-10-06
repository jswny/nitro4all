public class State {
    private boolean nitroCheck = true;

    public boolean toggleNitroCheck() {
        nitroCheck = !nitroCheck;
        return nitroCheck;
    }

    public boolean getNitroCheck() { return nitroCheck; }
}
