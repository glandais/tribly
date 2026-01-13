cd backend
export QUARKUS_CONTAINER_IMAGE_IMAGE="ghcr.io/glandais/tribly-backend:latest"
mvn clean package -DskipTests -Dquarkus.container-image.build=true
cd ../frontend
docker build --progress=plain -t ghcr.io/glandais/tribly-frontend:latest .
cd ..
