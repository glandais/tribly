# `export $(... | xargs)` splits values on whitespace, quoted or not, so a display name like
# "Gaby Landais" would be exported as two broken words. Let the shell parse the file instead.
set -a
. ./.env
set +a
cd backend
export QUARKUS_CONTAINER_IMAGE_GROUP=""
export QUARKUS_CONTAINER_IMAGE_NAME="pedalons-backend"
export QUARKUS_CONTAINER_IMAGE_TAG="$ENV_NAME"
mvn clean package -DskipTests -Dquarkus.container-image.build=true
cd ../frontend
mkdir -p src/assets/legal
cp ../privacy/*.md src/assets/legal/
docker build --progress=plain -t pedalons-frontend:$ENV_NAME .
rm -rf src/assets/legal
cd ..
