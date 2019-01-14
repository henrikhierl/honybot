# [Honybot](https://honybot.com/)

## About the project
This project contains the source code of https://honybot.com/. Honybot is a twitch.tv chat bot that offers
a lot of features like custom currency, vip-system, (automated) betting, custom commands and some neat **Starcraft 2 Features**.

This started as a fun project (and still is) and grew a lot bigger than I expected. Code quality is pretty horrible
most of the times. The main goal was to get things to work. There are probably still some german comments, some useless
console prints and a lot of useless(?) TODOs.

If you want to fork this project and create your own website off of it, it will probably not work out of the box. There are still
a lot of dependencies in locations (e.g. in html) that reference to static sources outside of this project.
For example some images, JavaScript or the HonyHelper might still be loaded from https://static.honybot.com

## Requirements to run
- MySQL Database (recommended version 5.7)
- Twitch.tv developer access
- SC2 Ladder Updater (optional)

## Setup
`clone project`

Setup a MySQL database and execute the script `mysql_schema.sql`. This should create all required tables. You might have
to create some db entries, like a dummy user for betting stats.

Rename or copy `config.properties.dist` file to `config.properties` and fill it out.
More information can be found down below.

Register an app at https://glass.twitch.tv/console/apps and generate the ClientSecret. 
Your app's OAuth Redirect URL needs to be the same as the one in the configuration file, e.g. `http://localhost:4567/auth`.
Paste the ClientSecret and Client-ID into the `config.properties`

The Chatbot needs a twitch dedicated account and an OAuth token, to join chat channels. Therefore you need to generate
one and save it together with the name into `config.properties`. 

Import project into IDE:
- IntellIJ IDEA: New project from existing source > use Gradle
- Eclipse: Should just work out of the box

### Using honybot on a server that runs Apache

If you want to use the bot on a server that also runs Apache, here are additional directives for HTTP(S) that might help:

```
<Location />
	ProxyPass http://127.0.0.1:[PORT]/
	ProxyPassReverse http://127.0.0.1:[PORT]/
</Location>
```

## Config
All configuration is located in the `config.properties` file. I have no idea why there are two oauth params...

```
# Admin password to log into any account as admin. For the love of god, please change this
adminPassword=password

# Webserver Port
port=4567

# twitch config
twitchChatBotName=
twitchOauth=oauth:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
twitchClientId=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
twitchClientSecret=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
twitchCommunityId=
twitchOauthToken=oauth:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
twitchLoginRedirectURI=http://XXXX:PORT/auth

#db config - this are only defaualt values. You can change this to your db settings
dbHost=localhost
dbPort=3306
dbName=honybot
dbUser=honybot
dbPassword=password
```

## Co-Dependent projects
This bot uses code from other repos, that I don't have added to GitHub yet. They will follow soon.

- HonyHelper
- [Honybot React Panel](https://github.com/Honydev/honybot_react_panel)
- Starcraft 2 Ladder Updater

## Problems

The bot still uses the old twitch API (v5) and is based on channel names instead of channel IDs. This already
breaks Stuff and might break even more Stuff in the future.

# How to contribute
Just create a pull request. Or let me know of you find some bugs. Create issues. You know. all that stuff.

Here are some things that it could really need right now:
- **Logging** - There is absolutely no useful logging.
- **ORM** - Database stuff looks bad. It works, but it's not nice. A lot of copy and paste happened here.
- **JSON** - I currently use *org.json.simple*. Using something like *Jackson* might be better.
- **IRC Bot** - Honybot uses PIRCBot. It might be worth to use https://kitteh.org/ in the future.
- **Open Source Design** - The website is built on a Themeforest template. I suck at webdesign, so I bought a template.
It would be cool to have an own, unique design.
- **Cleaner code in general**

# License
This repository is licensed under the GNU GENERAL PUBLIC LICENSE Version 3, as stated in the LICENSE file.

Files in `/src/main/resources/assets` are licensed under the Themeforest Regular License
(https://themeforest.net/licenses/terms/regular), unless stated otherwise.
