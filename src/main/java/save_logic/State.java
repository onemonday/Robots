package save_logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State {
    private final Map<String, Object> map;

    public State() {
        this.map = new HashMap<>();
    }

    public Object getElement(String elementName) {
        return map.get(elementName);
    }

    public void putElement(String elementName, Object element) {
        map.put(elementName, element);
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

//    public State merge(State state1, State state2) {
//        State mergedState = new State();
//
//        return mergedState;
//    }

}
