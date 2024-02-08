//on tape d`abord 
su hadoop
mot de passe
// recuperation de la donnee  depuis l`url et stockage dans un reperttoire de la machine
sudo mkdir -p /home/workspace/data/off_raw/
// attribution des droits aux repertoire

sudo chown -R ubuntu:ubuntu /home/workspace/data/off_raw
ls /home/workspace/data/off_raw 

//s commande sur hdfs pour recuperer la donnee et la stocke sur hdfs tart 
su hadoop
start-dfs.show
start-yarn.sh
je cree le repertoire off_raw en interface graphique sur hdfs

hdfs dfs -chown ubuntu:ubuntu /user/ubuntu/off_raw
hdfs dfs -chmod 777 /user/ubuntu/off_raw

//commande pour lancer extract.sh
sh extrat.sh


spark-submit --class main.scala.mnmc.Formated /home/ubuntu/Downloads/mnmcount/scala/target/scala-2.12/main-scala-mnmc_2.12-1.0.jar


//pour airflow
pip install apache-airflow-providers-apache-spark==4.7.1
