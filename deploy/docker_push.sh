#!/usr/bin/env bash

if [ -z "$TRAVIS_TAG" ]
then
  DOCKER_IMAGE_TAG="$TRAVIS_TAG"
elif [ "$TRAVIS_BRANCH" = 'master' ]
then
  DOCKER_IMAGE_TAG='latest'
else
  echo 'Could not find a Git tag or an appropriate branch to use for the Docker image tag!'
  exit 1
fi

DOCKER_IMAGE_NAME="${DOCKER_USERNAME}"/"${REPOSITORY}":"${DOCKER_IMAGE_TAG}"

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build --tag "$REPOSITORY" .
docker tag "$REPOSITORY" "$DOCKER_IMAGE_NAME"
docker push "$DOCKER_IMAGE_NAME"