![Maven test](https://github.com/julien-gm/all-team-hat/workflows/Maven%20test/badge.svg?branche=master)
![Validate format](https://github.com/julien-gm/all-team-hat/workflows/Validate%20format/badge.svg?branche=master)
![Coverage](https://github.com/julien-gm/all-team-hat/workflows/Coverage/badge.svg?branche=master)
[![codecov](https://codecov.io/gh/julien-gm/all-team-hat/branch/master/graph/badge.svg)](https://codecov.io/gh/julien-gm/all-team-hat)

# all-team-hat
Generates as much as possible the most balanced distribution of players between n teams

## Usage

Go to http://15.188.55.121:8080/

### Input file and options

The input file should be a XLSX file containing the following columns in the header:
* `Prénom` : First name of the player, used with last name to avoid duplicates
* `Nom` : Last name of the player, used with first name to avoid duplicates.
* `Pseudo` : Nickname of the player.
* `Email` : Used to prevent duplicates and to check for real players.
* `Club` : To dispatch correctly players from the same club across teams.
* `Age` : Used to avoid having all the young players in the same team.
* `Sexe` : Used to have mixed teams.
* `Handler?` : Used to make sure each team has enough handlers.
 Answer can be `Oui`, `Non` or any other answer that will be interpreted as `if needed`.
* Skills columns should be next to each other.

_Optional_:
* `Jour` : Used when the tournament take place on several days.
* `Binôme` : Used to try to associate 2 players.
Name of the column can be renamed using the option `teamMateColName`.

### With file

#### Using the CLI
```bash
$ mvn clean package
$ java -jar target/all-team-hat-2.2.0.jar CalculatorJob --nbTeams=6 --nbRuns=20 -file my_file.csv
```

##### options
* `file` : the CSV file to parse
* `nbTeams` : number of teams to generate (default is 6)
* `nbRuns` : The more runs you have, the more chance you have to have the best composition.
Be carreful, it takes 5 minutes for one run for 100 players. 
* `invalidTeamPenalty` : Composition is not valid if the number of player for each day is not equally repartitioned.
Sometimes, you may want to allow this composition. You can use a value between 0 and 2000 depending on what you want.
Default value is 200. The higher the value, the more chance you have to have a valid composition. 
* `teammatePenalty` : If a 2 teammates are not in the same team, we add a penalty. Default value is 50.
* `teamMateColName` : The name of the column "teammate". Default is "Binôme".
* `nbSkills` : Number of player skills (for instance technical and speed). Default is 3.
* `skillFirstCol` : Number of the first skill column. Default is 9.

Google spreadsheet options (not working at the moment): 
* `sheet` : Google spreadsheet id
* `range` : Sheet name and range to parse

#### Using the UI
You can either download the latest release or package it using `maven`.
```bash
$ java -jar target/all-team-hat-2.2.0-jar-with-dependencies.jar CalculatorUI
```

##### Options

* `List of players` : see `file` option
* `Number of teams` : see `nbTeams` option
* `Number of runs` : see `nbRuns` option
* `Invalid team penalty` : see `invalidTeamPenalty` option
* `Teammate failure penalty` : see `teammatePenalty` option
* `teammate column name` : see `teamMateColName` option
* `Number of skills` : see `nbSkills` option
* `Skill first column` : see `skillFirstCol` option

### With Google spreadsheet

#### /!\ Not working for the moment !
1. Retrieve your `credentials.json` following the step 1 on [this page](https://developers.google.com/sheets/api/quickstart/java).
1. Add it to the folder `src/main/resources/`
1. Retrieve your `sheetid`

```bash
$ mvn clean package
$ java -jar target/all-team-hat-1.0.0.jar CalculatorJob --nbTeams=6 --nbRuns=20 -sheet my_sheet_id
```
