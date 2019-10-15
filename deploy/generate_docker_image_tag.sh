#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ]
then
  DOCKER_IMAGE_TAG='latest'
else
  DOCKER_IMAGE_TAG="$TRAVIS_BRANCH"
fi

echo "$DOCKER_IMAGE_TAG"
