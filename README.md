# OC Helper
As I am shifting to 1.20+ version playing, I'll try to rewrite it to fit multiple versions, this one will be archived.

Springboot based app to collect & manage in-game oc components.

## Usage
For this project the programme is designed to work with [OCLua](https://github.com/RhyVis/OCLua) scripts executed in-game.
It sends the scripts to OCLua main script periodically, and receives report from in-game OC.

The application requires JDK 17+ to work, to execute it, run `java -jar OCHelper-{version}.jar` in console or daemon or anything you like.

## Config
Before starting the program, you need to copy the **application.yml** from `OCHelper-{version}.jar\BOOT-INF\classes\` and edit some config lines.

`server.port`: --- You and OCLua access to the application through this.

`spring.datasource.url`: jdbc:sqlite:{filepath} --- The database path for storing past infomation.

`path.icon-panel-path`: --- For item_panel you dumped by NEI, used for icon display, by default you need to put all the icons in `{workingfloder}\public\res\item_table\`

`csv-path`: --- For csv you dumped by NEI, used for localization.

`json-path` --- Deprecated.

`lua-scripts-path` --- The base lua scripts path, all scripts in it will be assembled one by one with the `return fun()` generated by the application, it shouldn't contain any lines of `return *` outside the funtions.
