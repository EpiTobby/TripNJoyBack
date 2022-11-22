#!/bin/sh

read -p "Github username: " gh_user
read -s -p "Github personal access token: " gh_token
echo

GH_USERNAME=$gh_user GH_KEY=$gh_token docker-compose up --build