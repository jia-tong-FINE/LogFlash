package workflow;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String, String> parameter;
    public static Map<String, Integer> valueStates = new HashMap<>();

    static {
        //reserve a flag for each valueState
        valueStates.put("transferParamMatrix", 0);
        valueStates.put("tcfgValueState", 0);
        valueStates.put("parseTree", 0);
        valueStates.put("templateMap", 0);
    }
}
