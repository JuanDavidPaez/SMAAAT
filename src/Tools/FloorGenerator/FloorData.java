package Tools.FloorGenerator;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FloorData {

    public int pxResolution = 10;
    public int XSize;
    public int YSize;
    public List<GridPoint> walls = new ArrayList();

    public static FloorData loadDataFromFile(String filePath) {
        FloorData floorData = null;
        try {
            String jsonFileContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            floorData = gson.fromJson(jsonFileContent, FloorData.class);
        } catch (IOException ex) {
            Logger.getLogger(FloorEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return floorData;
    }
}
