#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ]
then
  DOCKER_IMAGE_TAG='latest'
else
  DOCKER_IMAGE_TAG="$TRAVIS_BRANCH"
fi

DOCKER_IMAGE_NAME="${DOCKER_USERNAME}"/"${REPOSITORY}":"${DOCKER_IMAGE_TAG}"

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build --tag "$REPOSITORY" .
docker tag "$REPOSITORY" "$DOCKER_IMAGE_NAME"
docker push "$DOCKER_IMAGE_NAME"