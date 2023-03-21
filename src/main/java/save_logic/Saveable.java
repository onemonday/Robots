package save_logic;

public interface Saveable {
    State getState();
    void restoreState(State state);
    String getPrefix();
}
