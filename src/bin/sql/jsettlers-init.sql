--# -*- mode: sql -*-
--#
--# This script defines the schemas, and creates the tables 
--# used by JSettlers.
--#

CREATE DATABASE IF NOT EXISTS socdata;

GRANT ALL PRIVILEGES
        ON socdata.*
        TO 'socuser'@'localhost'
        IDENTIFIED BY 'socpass';

USE socdata;

CREATE TABLE users (
        nickname      VARCHAR(20), 
        host          VARCHAR(50), 
        password      VARCHAR(20), 
        email         VARCHAR(50), 
        lastlogin     DATE,
        wins          INT      DEFAULT 0,
        losses        INT      DEFAULT 0,
        face          SMALLINT DEFAULT 1,
        totalpoints   INT      DEFAULT 0,
        PRIMARY KEY (nickname)
);

CREATE TABLE logins (
        nickname      VARCHAR(20), 
        host          VARCHAR(50), 
        lastlogin     DATE,
        INDEX (nickname)
);

CREATE TABLE games (
        gamename      VARCHAR(20), 
        player1       VARCHAR(20), 
        player2       VARCHAR(20), 
        player3       VARCHAR(20), 
        player4       VARCHAR(20), 
        score1        SMALLINT, 
        score2        SMALLINT, 
        score3        SMALLINT, 
        score4        SMALLINT, 
        starttime     TIMESTAMP,
        PRIMARY KEY (gamename)
);

CREATE TABLE robotparams (
        robotname                VARCHAR(20), 
        maxgamelength            INT, 
        maxeta                   INT, 
        etabonusfactor           FLOAT, 
        adversarialfactor        FLOAT, 
        leaderadversarialfactor  FLOAT, 
        devcardmultiplier        FLOAT, 
        threatmultiplier         FLOAT, 
        strategytype             INT, 
        starttime                TIMESTAMP, 
        endtime                  TIMESTAMP, 
        wins                     INT      DEFAULT 0, 
        losses                   INT      DEFAULT 0, 
        tradeFlag                BOOL,
        totalpoints              INT      DEFAULT 0,
        PRIMARY KEY (robotname)
);

