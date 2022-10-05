version=$(PGPASSWORD=root psql -c "SELECT value FROM db_metadata WHERE key = 'db_version'" -h localhost -p 5432 -U root -t --csv postgres)
echo $version

if [ $version -eq -1 ]; then
  echo 'init'
fi