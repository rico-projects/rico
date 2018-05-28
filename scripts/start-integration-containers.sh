#!/bin/bash
../gradlew :integration-tests:copyWar
cd ../integration-tests/integration-tests
docker-compose rm -f -s
docker-compose build
docker-compose up