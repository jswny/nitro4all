#!/usr/bin/env bash

cd /srv/nitro4all || return 1
git pull
docker-compose down
docker-compose build
docker-compose up -d