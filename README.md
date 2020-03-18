![Java CI](https://github.com/julien-gm/all-team-hat/workflows/Java%20CI/badge.svg?branch=master)

# all-team-hat
Generates as much as possible the most balanced distribution of players between n teams

## Usage


### With file

```bash
$ mvn clean package
$ java -jar target/all-team-hat-1.0.0.jar CalculatorJob --nbTeams=6 --nbRuns=20 -file my_file.csv
```

### With Google spreadsheet

1. Retrieve your `credentials.json` following the step 1 on [this page](https://developers.google.com/sheets/api/quickstart/java).
2. Add it to the folder `src/main/resources/`

```bash
$ mvn clean package
$ java -jar target/all-team-hat-1.0.0.jar CalculatorJob --nbTeams=6 --nbRuns=20 -sheet my_sheet_id
```
