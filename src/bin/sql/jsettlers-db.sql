--# -*- mode: sql -*-
--#
--# This script creates the initial database and SOC user
--# used by JSettlers.
--#

CREATE DATABASE IF NOT EXISTS socdata;

GRANT ALL PRIVILEGES
        ON socdata.*
        TO 'socuser'@'localhost'
        IDENTIFIED BY 'socpass';

USE socdata;
