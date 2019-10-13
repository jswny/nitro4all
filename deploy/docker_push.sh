#!/usr/bin/env bash

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build --tag "$REPOSITORY" .
docker tag "$REPOSITORY" "${REPOSITORY}":"${DOCKER_IMAGE_TAG}"
docker push "${REPOSITORY}":"${DOCKER_IMAGE_TAG}"