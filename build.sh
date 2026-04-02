cd backend
export QUARKUS_CONTAINER_IMAGE_IMAGE="ghcr.io/glandais/pedalons-backend:latest"
mvn clean package -DskipTests -Dquarkus.container-image.build=true
cd ../frontend
mkdir -p src/assets/legal
cp ../privacy/*.md src/assets/legal/
docker build --progress=plain -t ghcr.io/glandais/pedalons-frontend:latest .
rm -rf src/assets/legal
cd ..
