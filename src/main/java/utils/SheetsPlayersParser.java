package utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import computation.TeamsGenerator;
import domain.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public class SheetsPlayersParser implements PlayersParserInterface {

    // TODO set it in a config file or as option at runtime
    private static final int NUM_COL_PSEUDO = 0;
    private static final int NUM_COL_CLUB = 1;
    private static final int NUM_COL_GENDER = 2;
    private static final int NUM_COL_FIRST_NAME = 3;
    private static final int NUM_COL_LAST_NAME = 4;
    private static final int NUM_COL_EMAIL = 5;
    private static final int NUM_COL_HANDLER = 6;
    private static final int NUM_COL_FIRST_SKILL = 7;
    private static final int NUM_COL_LAST_SKILL = 11;

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private String spreadsheetId;
    private String range;


    public SheetsPlayersParser(String sheetId, String range) {
        this.spreadsheetId = sheetId;
        this.range = range;
        System.out.println(range);
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsPlayersParser.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    public TeamsGenerator getTeamsGenerator() {
        List<Player> allPlayers = new ArrayList<>();
        try {
            // Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                for (List row : values) {
                    if (isPlayer(row)) {
                        Player player = new Player(row.get(NUM_COL_PSEUDO).toString());
                        player.setClub(row.get(NUM_COL_CLUB).toString());
                        player.setGender(row.get(NUM_COL_GENDER).toString().startsWith("F") ? Player.Gender.FEMME : Player.Gender.HOMME);
                        player.setLastName(row.get(NUM_COL_LAST_NAME).toString());
                        player.setFirstName(row.get(NUM_COL_FIRST_NAME).toString());
                        player.setEmail(row.get(NUM_COL_EMAIL).toString());
                        String handler = row.get(NUM_COL_HANDLER).toString();
                        player.setHandler(
                                handler.equals("oui") ? Player.Handler.YES : handler.equals("non") ? Player.Handler.NO : Player.Handler.MAYBE);

                        // Getting skills
                        // Skipping the first 8 columns that we just read
                        int i = NUM_COL_FIRST_SKILL;
                        double skillValue;
                        for (int colNum = NUM_COL_FIRST_SKILL; colNum <= NUM_COL_LAST_SKILL; colNum++) {
                            try {
                                skillValue = Double.parseDouble(row.get(i++).toString());
                                player.getSkillsList().add(skillValue);
                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        allPlayers.add(player);
                    } else {
                        System.err.printf("%s has no valid email: %s", row.get(NUM_COL_PSEUDO), row.get(NUM_COL_EMAIL));
                    }
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            System.err.println(e.getMessage());
        }
        return new TeamsGenerator(allPlayers);
    }

    private boolean isPlayer(List row) {
        return isValidEmail(row.get(5).toString());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        return (email != null) && Pattern.compile(emailRegex).matcher(email).matches();
    }
}
