package datalayer.serialization;

import core.config.WorldConstants;
import datalayer.dto.GameSnapshot;
import core.domain.Game;

import java.io.File;

public class SaveService {

    public static void saveGame(Game game) {
        if (game == null) return;
        GameSnapshot snapshot = game.toSnapshot();
        if (snapshot != null) {
            JsonService.save(WorldConstants.SAVE_FILE_PATH, snapshot);
        }
    }

    public static GameSnapshot loadGame() {
        return JsonService.load(WorldConstants.SAVE_FILE_PATH, GameSnapshot.class);
    }

    public static boolean hasSave() {
        return new File(WorldConstants.SAVE_FILE_PATH).exists();
    }

    public static void deleteSave() {
        File file = new File(WorldConstants.SAVE_FILE_PATH);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.err.println("Failed to delete save file: " + WorldConstants.SAVE_FILE_PATH);
            }
        }
    }
}