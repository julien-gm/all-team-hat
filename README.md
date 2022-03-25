![Maven test](https://github.com/julien-gm/all-team-hat/workflows/Maven%20test/badge.svg?branche=master)
![Validate format](https://github.com/julien-gm/all-team-hat/workflows/Validate%20format/badge.svg?branche=master)
![Coverage](https://github.com/julien-gm/all-team-hat/workflows/Coverage/badge.svg?branche=master)
[![codecov](https://codecov.io/gh/julien-gm/all-team-hat/branch/master/graph/badge.svg)](https://codecov.io/gh/julien-gm/all-team-hat)

# all-team-hat
Generates as much as possible the most balanced distribution of players between n teams

## Usage


### With file

#### Using the CLI
```bash
$ mvn clean package
$ java -jar target/all-team-hat-2.2.0.jar CalculatorJob --nbTeams=6 --nbRuns=20 -file my_file.csv
```

#### Using the UI
You can either download the latest release or package it using `maven`.
```bash
$ java -jar target/all-team-hat-2.2.0-jar-with-dependencies.jar CalculatorUI
```

### With Google spreadsheet

#### /!\ Not working for the moment !
1. Retrieve your `credentials.json` following the step 1 on [this page](https://developers.google.com/sheets/api/quickstart/java).
1. Add it to the folder `src/main/resources/`
1. Retrieve your `sheetid`

```bash
$ mvn clean package
$ java -jar target/all-team-hat-1.0.0.jar CalculatorJob --nbTeams=6 --nbRuns=20 -sheet my_sheet_id
```
