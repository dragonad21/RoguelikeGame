package datalayer.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import datalayer.dto.StatisticsRun;
import datalayer.dto.StatisticsRecord;
import core.config.WorldConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatisticsService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveRun(StatisticsRun run) {
        List<StatisticsRecord> records = loadAll();
        records.add(new StatisticsRecord(run));
        JsonService.save(WorldConstants.SCOREBOARD_FILE_PATH, records);
    }

    public static List<StatisticsRecord> loadAll() {
        try {
            File file = new File(WorldConstants.SCOREBOARD_FILE_PATH);
            if (!file.exists()) return new ArrayList<>();
            StatisticsRecord[] array = mapper.readValue(file, StatisticsRecord[].class);
            return new ArrayList<>(Arrays.asList(array));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static List<StatisticsRecord> loadSorted() {
        List<StatisticsRecord> records = loadAll();
        records.sort((a, b) -> b.getTreasures() - a.getTreasures());
        return records;
    }
}
