#!/bin/bash
set -e

BUCKET_NAME="goticket-dev"
SEED_IMAGES_DIR="/seed-images"

echo "Ensuring S3 bucket exists: ${BUCKET_NAME}"

awslocal s3 mb "s3://${BUCKET_NAME}" || true

awslocal s3api put-bucket-acl \
  --bucket "${BUCKET_NAME}" \
  --acl public-read

echo "Bucket ${BUCKET_NAME} is ready and set to public-read"

if [ -d "${SEED_IMAGES_DIR}" ]; then
  echo "Uploading seed images from ${SEED_IMAGES_DIR}"

  for file in "${SEED_IMAGES_DIR}"/evento-*; do
    [ -f "${file}" ] || continue

    filename="$(basename "${file}")"
    extension=".${filename##*.}"
    event_number="$(echo "${filename}" | sed -E 's/^evento-([0-9]+).*/\1/')"

    if [[ ! "${event_number}" =~ ^[0-9]+$ ]]; then
      echo "Skipping ${filename}: unable to infer event number."
      continue
    fi

    s3_key="events/${event_number}/cover${extension}"
    echo "Uploading ${filename} -> s3://${BUCKET_NAME}/${s3_key}"
    awslocal s3 cp "${file}" "s3://${BUCKET_NAME}/${s3_key}"
  done
else
  echo "Seed images directory not found (${SEED_IMAGES_DIR}); skipping image upload."
fi
