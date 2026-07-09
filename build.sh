export $(grep -v '^#' .env | xargs)
cd backend
export QUARKUS_CONTAINER_IMAGE_REGISTRY="ghcr.io"
export QUARKUS_CONTAINER_IMAGE_GROUP="pedalons"
export QUARKUS_CONTAINER_IMAGE_NAME="pedalons-backend"
export QUARKUS_CONTAINER_IMAGE_TAG="$ENV_NAME"
mvn clean package -DskipTests -Dquarkus.container-image.build=true
cd ../frontend
mkdir -p src/assets/legal
cp ../privacy/*.md src/assets/legal/
docker build --progress=plain -t ghcr.io/pedalons/pedalons-frontend:$ENV_NAME .
rm -rf src/assets/legal
cd ..
