package ankihelper.server.managers;

import app.AnkiHelperApp;
import models.AnkiRequestBody;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Component
public class AnkiManager {
    private final AnkiHelperApp ankiHelper;

    public AnkiManager() {
        this.ankiHelper = AnkiHelperApp.initializeAnkiHelper();
    }

    public ArrayList<String> performNameAndDescribeAnalysis(String filePath) {
        return this.ankiHelper.analyzeNameAndDescribe(filePath);
    }
}
