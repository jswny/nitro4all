#!/usr/bin/env bash

DOCKER_IMAGE_NAME="${DOCKER_USERNAME}"/"${REPOSITORY}":"${DOCKER_IMAGE_TAG}"

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker build --tag "$DOCKER_IMAGE_NAME" .
docker push "$DOCKER_IMAGE_NAME"