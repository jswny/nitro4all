#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ]
then
  DEPLOY_VARIANT='beta'
else
  DEPLOY_VARIANT='stable'
fi

DISCORD_TOKEN_VARIABLE="DISCORD_TOKEN_${DEPLOY_VARIANT}"
DISCORD_TOKEN=$(echo "${DISCORD_TOKEN_VARIABLE}" | tr [:lower:] [:upper:])

if [ -z "$DISCORD_TOKEN" ]
then
  echo '$DISCORD_TOKEN is empty! Exiting...'
  exit 1
fi

DEPLOY_DIR="${DEPLOY_ROOT_DIR}"/"${REPOSITORY}"-"$DEPLOY_VARIANT"

ssh "${DEPLOY_USERNAME}"@"$DEPLOY_SERVER" mkdir "$DEPLOY_DIR"
scp docker-compose.yml "${DEPLOY_USERNAME}"@"$DEPLOY_SERVER":"$DEPLOY_DIR"

ssh "${DEPLOY_USERNAME}"@"$DEPLOY_SERVER" "docker pull '${DOCKER_IMAGE_NAME}'"
ssh "${DEPLOY_USERNAME}"@"$DEPLOY_SERVER" "cd '${DEPLOY_DIR}' && DISCORD_TOKEN='${DISCORD_TOKEN}' TAG='${DOCKER_IMAGE_TAG}' docker-compose up -d"
