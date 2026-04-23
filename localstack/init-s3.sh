#!/bin/bash
set -e

echo "Creating S3 bucket: goticket-dev"

awslocal s3 mb s3://goticket-dev

awslocal s3api put-bucket-acl \
  --bucket goticket-dev \
  --acl public-read

echo "Bucket goticket-dev created and set to public-read"
