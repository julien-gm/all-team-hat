package utils;

import static utils.Parameters.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.CopySheetToAnotherSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import computation.TeamsGenerator;
import domain.Composition;
import domain.Player;
import domain.Team;

public class SheetsPlayersParser implements PlayersParserInterface {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously
     * saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private String inputSpreadsheetId;
    private String range;
    private Sheets sheets;

    public SheetsPlayersParser(String sheetId, String range) {
        this.inputSpreadsheetId = sheetId;
        this.range = range;
        System.out.println(range);
        this.setSheets();
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsPlayersParser.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private void setSheets() {
        try {
            // Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            this.sheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME).build();

        } catch (IOException | GeneralSecurityException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public TeamsGenerator getTeamsGenerator() {
        List<Player> allPlayers = new ArrayList<>();
        try {
            ValueRange response = this.sheets.spreadsheets().values().get(inputSpreadsheetId, range).execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {

                for (List<Object> row : values) {
                    if (isValidEmail(row.get(column_email.getIntValue()).toString())) {
                        Player player = new Player(row.get(column_pseudo.getIntValue()).toString());
                        player.setClub(row.get(column_club.getIntValue()).toString());
                        player.setGender(row.get(column_gender.getIntValue()).toString().matches(".*[f|F].*")
                                ? Player.Gender.FEMME : Player.Gender.HOMME);
                        player.setLastName(row.get(column_lastName.getIntValue()).toString());
                        player.setFirstName(row.get(column_firstName.getIntValue()).toString());
                        player.setEmail(row.get(column_email.getIntValue()).toString());
                        player.setHandler(getHandlerValue(row.get(column_handler.getIntValue()).toString()));

                        int i = column_firstSkill.getIntValue();
                        double skillValue;
                        for (int colNum = column_firstSkill.getIntValue(); colNum <= column_lastSkill
                                .getIntValue(); colNum++) {
                            try {
                                skillValue = Double.parseDouble(row.get(i++).toString());
                                player.getSkillsList().add(skillValue);
                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                System.out.println(e.getMessage());
                            }
                        }

                        setPlayerDay(row, player);
                        setTeamMate(allPlayers, row, player);
                        allPlayers.add(player);
                    } else {
                        System.err.printf("%s has no valid email: %s\n", row.get(column_pseudo.getIntValue()),
                                row.get(column_email.getIntValue()));
                    }
                }
                System.out.println("Computing " + allPlayers.size() + " players.");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new TeamsGenerator(allPlayers);
    }

    private void setPlayerDay(List row, Player player) {
        try {
            player.setDay(Integer.parseInt(row.get(column_excludeDay.getIntValue()).toString()));
        } catch (Exception ignored) {
        }
    }

    private void setTeamMate(List<Player> allPlayers, List row, Player player) {
        try {
            allPlayers.stream()
                    .filter(p -> p.getNickName().equalsIgnoreCase(row.get(column_teammate.getIntValue()).toString()))
                    .findFirst().ifPresent(player::setTeamMate);
        } catch (Exception ignored) {
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return (email != null) && Pattern.compile(emailRegex).matcher(email).matches();
    }

    private Player.Handler getHandlerValue(String handler) {
        if ("oui".equals(handler) || "Handler".equals(handler)) {
            return Player.Handler.YES;
        } else if ("non".equals(handler) || "Middle".equals(handler) || handler.startsWith("aucune idée")) {
            return Player.Handler.NO;
        } else {
            return Player.Handler.MAYBE;
        }
    }

    @Override
    public void write(Composition compo) {
        try {
            List<List<Object>> values = new ArrayList<>();
            int skillSize = 0;
            int firstLineTeam, lastLineTeam;
            for (Team team : compo.getTeams()) {
                firstLineTeam = values.size() + 1;
                int numPlayer = 1;
                for (Player p : team.getPlayers()) {
                    if (p.isReal()) {
                        List<Object> teamValues = new ArrayList<>();
                        teamValues.add(numPlayer);
                        teamValues.add(p.getNickName() + (p.getDay() != 0 ? " (" + p.getDay() + ")" : ""));
                        teamValues.add(p.getClub());
                        teamValues.add(p.getFirstName() + " " + p.getLastName());
                        teamValues.add(p.getHandler().toString());
                        teamValues.add(p.getGender().toString());
                        teamValues.addAll(p.getSkillsList());
                        skillSize = p.getSkillsList().size();
                        values.add(teamValues);
                        numPlayer++;
                    }
                }
                lastLineTeam = values.size();

                List<Object> teamAverage = new ArrayList<>();
                teamAverage.add("");
                teamAverage.add(team.getPlayers().stream().filter(Player::isReal).filter(p -> p.playsTheSameDay(1))
                        .count() + " - "
                        + team.getPlayers().stream().filter(Player::isReal).filter(p -> p.playsTheSameDay(2)).count());
                teamAverage.add("=D" + (lastLineTeam + 1) + "/2+E" + (lastLineTeam + 1));
                teamAverage.add(team.getPlayers().stream().filter(p -> p.getHandler() == Player.Handler.MAYBE).count());
                teamAverage.add(team.getPlayers().stream().filter(p -> p.getHandler() == Player.Handler.YES).count());
                teamAverage.add(team.getPlayers().stream().filter(p -> p.getGender() == Player.Gender.FEMME).count());

                char col = 'G';
                for (int skillNumber = 0; skillNumber < skillSize; skillNumber++) {
                    teamAverage.add("=ARRONDI(AVERAGEA(" + col + firstLineTeam + ":" + col + lastLineTeam + ");2)");
                    col++;
                }

                values.add(teamAverage);
            }
            List<ValueRange> data = new ArrayList<>();
            String title = "results!A1:" + ('A' + values.get(0).size() - 1) + values.size();
            data.add(new ValueRange().setRange(title).setValues(values));

            CopySheetToAnotherSpreadsheetRequest requestBody = new CopySheetToAnotherSpreadsheetRequest();
            requestBody.setDestinationSpreadsheetId(outputSheetId.getStringValue());

            sheets.spreadsheets().sheets().copyTo(title, 0, requestBody);// setProperties(new
                                                                         // SheetProperties().setTitle(title));
            BatchUpdateValuesRequest body = new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED")
                    .setData(data);
            BatchUpdateValuesResponse result = sheets.spreadsheets().values()
                    .batchUpdate(outputSheetId.getStringValue(), body).execute();
            System.out.printf("%d cells updated.\n", result.getTotalUpdatedCells());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private String createNewSpreadsheet() throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle("Teams generator"));
        spreadsheet = sheets.spreadsheets().create(spreadsheet).setFields("spreadsheetId").execute();
        return spreadsheet.getSpreadsheetId();
    }
}
