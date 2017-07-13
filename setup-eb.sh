#!/usr/bin/env bash
set -x
set -e

echo "[profile gr8conf]" > ~/.aws/config
echo "aws_access_key_id=$AWS_ACCESS_KEY_ID" >> ~/.aws/config
echo "aws_secret_access_key=$AWS_SECRET_ACCESS_KEY" >> ~/.aws/config