package save_logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateHandler {
    private String dir;
    public StateHandler(String dir) {
        this.dir = dir;
    }

    private JSONArray constructJsonArray(List<Saveable> saveableObjects) {
        JSONArray jsonArray = new JSONArray();

        saveableObjects.forEach(object -> {
            State state = object.getState();
            JSONObject jsonUpperLevelMap = new JSONObject();
            JSONObject jsonLowerLevelMap = new JSONObject();

            state.getKeys().forEach(key -> {
                jsonLowerLevelMap.put(key, state.getElement(key));
            });

            jsonUpperLevelMap.put(object.getPrefix(), jsonLowerLevelMap);
            jsonArray.add(jsonUpperLevelMap);
        });

        return jsonArray;
    }

    private State constructState(Map<String, Object> map) {
        State state = new State();
        map.keySet().forEach(key -> {
            state.putElement(key, map.get(key));
        });

        return state;
    }

    public void save(List<Saveable> saveableObjects) {
        JSONArray jsonArray = constructJsonArray(saveableObjects);

        try {
            File file = new File(dir);
            file.createNewFile();
            file.setWritable(true);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, State> restoreAllData() {
        JSONParser jsonParser = new JSONParser();
        File file = new File(dir);
        if (!file.exists())
            return null;

        try (FileReader fileReader = new FileReader(file)) {
            var rawData = jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) rawData;
            Map<String, State> stateMap = new HashMap<>();

            jsonArray.forEach(jsonObject -> {
                Map<String, Object> map = (HashMap) jsonObject;
                map.keySet().forEach(key -> {
                    var element = (HashMap) map.get(key);
                    State state = constructState(element);
                    stateMap.put(key, state);
                });
            });
            return stateMap;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
