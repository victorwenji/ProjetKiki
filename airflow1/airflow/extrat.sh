#!/bin/bash

# Définir le lien vers le fichier à télécharger
url="https://static.openfoodfacts.org/data/en.openfoodfacts.org.products.csv.gz"

# Date et heure actuelles
#current_date=$(date +'%Y-%m-%d')
#current_time=$(date +'%H-%M-%S')

# Définir le nom du fichier téléchargé
#/home/workspace/data/off_raw/
#filename="/home/Documents/food_data.csv.gz"

# Télécharger le fichier depuis le lien
wget -P /home/workspace/data/off_raw/ "$url" 

# Décompresser le fichier gzip
gunzip /home/workspace/data/off_raw/en.openfoodfacts.org.products.csv.gz

hdfs dfs -put -f /home/workspace/data/off_raw/en.openfoodfacts.org.products.csv /user/ubuntu/off_raw
