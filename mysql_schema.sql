SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `honybot`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bet`
--

CREATE TABLE `bet` (
  `id` int(10) UNSIGNED NOT NULL,
  `channel` varchar(50) COLLATE utf8_general_mysql500_ci NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bet_option`
--

CREATE TABLE `bet_option` (
  `id` int(10) UNSIGNED NOT NULL,
  `bet_id` int(10) UNSIGNED NOT NULL,
  `option_index` varchar(16) COLLATE utf8_general_mysql500_ci NOT NULL,
  `name_hr` varchar(255) COLLATE utf8_general_mysql500_ci NOT NULL,
  `quota` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bet_template`
--

CREATE TABLE `bet_template` (
  `ID` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_general_mysql500_ci NOT NULL,
  `arguments` text COLLATE utf8_general_mysql500_ci NOT NULL,
  `description` text COLLATE utf8_general_mysql500_ci NOT NULL,
  `type` varchar(255) COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bet_wager`
--

CREATE TABLE `bet_wager` (
  `id` int(10) UNSIGNED NOT NULL,
  `option_id` int(10) UNSIGNED NOT NULL,
  `viewername` varchar(50) COLLATE utf8_general_mysql500_ci NOT NULL,
  `amount` int(10) UNSIGNED NOT NULL,
  `win` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `channel_viewer_stat`
--

CREATE TABLE `channel_viewer_stat` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `viewer` int(10) UNSIGNED NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `chatstats`
--

CREATE TABLE `chatstats` (
  `username` varchar(100) NOT NULL,
  `stat` varchar(100) NOT NULL,
  `value` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `commands`
--

CREATE TABLE `commands` (
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `command` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `text` text CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `cost` int(11) NOT NULL,
  `whisper` tinyint(1) NOT NULL,
  `takesUserInput` tinyint(1) NOT NULL,
  `permission` int(11) NOT NULL,
  `vip` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `counter`
--

CREATE TABLE `counter` (
  `id` int(11) NOT NULL,
  `username` varchar(50) COLLATE utf8_general_mysql500_ci NOT NULL,
  `name` varchar(50) COLLATE utf8_general_mysql500_ci NOT NULL,
  `value` int(11) NOT NULL,
  `initial_value` int(11) NOT NULL,
  `path` varchar(32) COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `counter_command`
--

CREATE TABLE `counter_command` (
  `id` int(11) NOT NULL,
  `counter_id` int(11) NOT NULL,
  `command` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `output` text CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `cost` int(10) UNSIGNED NOT NULL,
  `permission` int(11) NOT NULL,
  `vip` int(11) NOT NULL,
  `type` enum('increase','decrease','reset') CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `nowhisper`
--

CREATE TABLE `nowhisper` (
  `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `redeemed_reward`
--

CREATE TABLE `redeemed_reward` (
  `ID` int(10) UNSIGNED NOT NULL,
  `reward_id` int(10) UNSIGNED NOT NULL,
  `viewername` varchar(50) COLLATE utf8_unicode_520_ci NOT NULL,
  `comment` text COLLATE utf8_unicode_520_ci NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_520_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `reward`
--

CREATE TABLE `reward` (
  `ID` int(10) UNSIGNED NOT NULL,
  `username` varchar(50) COLLATE utf8_unicode_520_ci NOT NULL,
  `command` varchar(255) COLLATE utf8_unicode_520_ci NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_520_ci NOT NULL,
  `response` text COLLATE utf8_unicode_520_ci NOT NULL,
  `description` text COLLATE utf8_unicode_520_ci NOT NULL,
  `image_url` text COLLATE utf8_unicode_520_ci NOT NULL,
  `cost` int(11) NOT NULL,
  `permission` tinyint(3) UNSIGNED NOT NULL,
  `vip` tinyint(3) UNSIGNED NOT NULL,
  `enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_520_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `sc2game`
--

CREATE TABLE `sc2game` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `player` varchar(20) COLLATE utf8_general_mysql500_ci NOT NULL,
  `opponent` varchar(20) COLLATE utf8_general_mysql500_ci NOT NULL,
  `player_race` enum('p','r','t','z') COLLATE utf8_general_mysql500_ci NOT NULL,
  `opponent_race` enum('p','r','t','z') COLLATE utf8_general_mysql500_ci NOT NULL,
  `result` enum('Victory','Defeat','Tie') COLLATE utf8_general_mysql500_ci NOT NULL,
  `length` mediumint(10) UNSIGNED NOT NULL,
  `region` enum('eu','us','kr','cn','sea') COLLATE utf8_general_mysql500_ci NOT NULL,
  `session` int(10) UNSIGNED NOT NULL,
  `played_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `sc2player`
--

CREATE TABLE `sc2player` (
  `season` tinyint(4) NOT NULL,
  `id` int(11) UNSIGNED NOT NULL,
  `battle_tag` varchar(20) COLLATE utf8_general_mysql500_ci NOT NULL,
  `name` varchar(20) COLLATE utf8_general_mysql500_ci NOT NULL,
  `region` enum('eu','us','kr','sea','cn') COLLATE utf8_general_mysql500_ci NOT NULL,
  `race` enum('terran','zerg','protoss','random') COLLATE utf8_general_mysql500_ci NOT NULL,
  `realm` enum('1','2','3','') COLLATE utf8_general_mysql500_ci NOT NULL,
  `clan_tag` varchar(6) COLLATE utf8_general_mysql500_ci NOT NULL,
  `clan_name` varchar(32) COLLATE utf8_general_mysql500_ci NOT NULL,
  `clan_id` int(10) UNSIGNED NOT NULL,
  `ladder_id` int(10) UNSIGNED NOT NULL,
  `league` enum('Bronze','Silver','Gold','Platinum','Diamond','Master','Grandmaster') COLLATE utf8_general_mysql500_ci NOT NULL,
  `tier` enum('1','2','3','') COLLATE utf8_general_mysql500_ci NOT NULL,
  `mmr` smallint(6) NOT NULL,
  `points` smallint(6) NOT NULL,
  `wins` smallint(5) UNSIGNED NOT NULL,
  `losses` smallint(5) UNSIGNED NOT NULL,
  `ties` smallint(5) UNSIGNED NOT NULL,
  `streak_max` smallint(5) UNSIGNED NOT NULL,
  `streak_current` smallint(5) UNSIGNED NOT NULL,
  `rank_previous` tinyint(3) UNSIGNED NOT NULL,
  `rank_current` tinyint(3) UNSIGNED NOT NULL,
  `joined` datetime NOT NULL,
  `last_played` datetime NOT NULL,
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `path` varchar(255) COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `sc_league_boundary`
--

CREATE TABLE `sc_league_boundary` (
  `region` varchar(16) COLLATE utf8_general_mysql500_ci NOT NULL,
  `league` varchar(32) COLLATE utf8_general_mysql500_ci NOT NULL,
  `tier` smallint(5) UNSIGNED NOT NULL,
  `lower` smallint(5) UNSIGNED NOT NULL,
  `upper` smallint(5) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `settings`
--

CREATE TABLE `settings` (
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `setting` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `value` text CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) COLLATE utf8_general_mysql500_ci NOT NULL,
  `twitch_auth` varchar(30) COLLATE utf8_general_mysql500_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `sc2session` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `registration_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `viewers`
--

CREATE TABLE `viewers` (
  `channel` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_mysql500_ci NOT NULL,
  `points` int(11) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL DEFAULT '0',
  `bets` int(11) NOT NULL DEFAULT '0',
  `betswon` int(11) NOT NULL DEFAULT '0',
  `pointswon` int(11) NOT NULL DEFAULT '0',
  `pointslost` int(11) NOT NULL DEFAULT '0',
  `totalbetvalue` int(11) NOT NULL DEFAULT '0',
  `vip` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `bet`
--
ALTER TABLE `bet`
  ADD PRIMARY KEY (`id`),
  ADD KEY `channel` (`channel`);

--
-- Indizes für die Tabelle `bet_option`
--
ALTER TABLE `bet_option`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bet_id` (`bet_id`);

--
-- Indizes für die Tabelle `bet_template`
--
ALTER TABLE `bet_template`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `user_id` (`user_id`);

--
-- Indizes für die Tabelle `bet_wager`
--
ALTER TABLE `bet_wager`
  ADD PRIMARY KEY (`id`),
  ADD KEY `option_id` (`option_id`),
  ADD KEY `username` (`viewername`);

--
-- Indizes für die Tabelle `channel_viewer_stat`
--
ALTER TABLE `channel_viewer_stat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indizes für die Tabelle `chatstats`
--
ALTER TABLE `chatstats`
  ADD PRIMARY KEY (`username`,`stat`),
  ADD KEY `username` (`username`),
  ADD KEY `stat` (`stat`);

--
-- Indizes für die Tabelle `commands`
--
ALTER TABLE `commands`
  ADD PRIMARY KEY (`username`,`command`);

--
-- Indizes für die Tabelle `counter`
--
ALTER TABLE `counter`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name_username` (`username`,`name`),
  ADD KEY `username` (`username`),
  ADD KEY `name` (`name`);

--
-- Indizes für die Tabelle `counter_command`
--
ALTER TABLE `counter_command`
  ADD PRIMARY KEY (`id`),
  ADD KEY `counter_id` (`counter_id`,`command`);

--
-- Indizes für die Tabelle `nowhisper`
--
ALTER TABLE `nowhisper`
  ADD PRIMARY KEY (`username`);

--
-- Indizes für die Tabelle `redeemed_reward`
--
ALTER TABLE `redeemed_reward`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `reward_id` (`reward_id`),
  ADD KEY `viewername` (`viewername`);

--
-- Indizes für die Tabelle `reward`
--
ALTER TABLE `reward`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `username` (`username`),
  ADD KEY `enabled` (`enabled`);

--
-- Indizes für die Tabelle `sc2game`
--
ALTER TABLE `sc2game`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `opponent` (`opponent`),
  ADD KEY `opponent_race` (`opponent_race`),
  ADD KEY `outcome` (`result`),
  ADD KEY `session` (`session`);

--
-- Indizes für die Tabelle `sc2player`
--
ALTER TABLE `sc2player`
  ADD PRIMARY KEY (`season`,`id`,`battle_tag`,`race`,`region`),
  ADD KEY `battle_tag` (`battle_tag`,`name`,`region`,`race`,`clan_tag`,`clan_name`,`league`,`tier`,`mmr`,`points`,`wins`,`losses`);

--
-- Indizes für die Tabelle `sc_league_boundary`
--
ALTER TABLE `sc_league_boundary`
  ADD PRIMARY KEY (`region`,`league`,`tier`);

--
-- Indizes für die Tabelle `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`username`,`setting`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`username`);

--
-- Indizes für die Tabelle `viewers`
--
ALTER TABLE `viewers`
  ADD PRIMARY KEY (`channel`,`name`),
  ADD KEY `name` (`name`),
  ADD KEY `channel` (`channel`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `bet`
--
ALTER TABLE `bet`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `bet_option`
--
ALTER TABLE `bet_option`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `bet_template`
--
ALTER TABLE `bet_template`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `bet_wager`
--
ALTER TABLE `bet_wager`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `channel_viewer_stat`
--
ALTER TABLE `channel_viewer_stat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `counter`
--
ALTER TABLE `counter`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `counter_command`
--
ALTER TABLE `counter_command`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `redeemed_reward`
--
ALTER TABLE `redeemed_reward`
  MODIFY `ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `reward`
--
ALTER TABLE `reward`
  MODIFY `ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `sc2game`
--
ALTER TABLE `sc2game`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT für Tabelle `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `counter_command`
--
ALTER TABLE `counter_command`
  ADD CONSTRAINT `FOREIGN KEY` FOREIGN KEY (`counter_id`) REFERENCES `counter` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
