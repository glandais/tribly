set -e

cd backend
mvn clean package -DskipTests
cd ../frontend
pnpm check
cd ../mobile
bash check.sh

