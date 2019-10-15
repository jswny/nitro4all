openssl aes-256-cbc -K "$encrypted_200a04de1233_key" -iv "$encrypted_200a04de1233_iv" -in "${TRAVIS_BUILD_DIR}"/"${DEPLOY_DIR}"/deploy_key.enc -out "${TRAVIS_BUILD_DIR}"/"${DEPLOY_DIR}"/deploy_key -d
eval "$(ssh-agent -s)"
chmod 600 "${TRAVIS_BUILD_DIR}"/"${DEPLOY_DIR}"/deploy_key
ssh-add "${TRAVIS_BUILD_DIR}"/"${DEPLOY_DIR}"/deploy_key
ssh-keyscan -t "$TRAVIS_SSH_KEY_TYPES" -H "$DEPLOY_SERVER" 2>&1 >> "${TRAVIS_HOME}"/.ssh/known_hosts
