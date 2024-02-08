from airflow import DAG
from datetime import datetime, timedelta
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from airflow.operators.bash import BashOperator


airflow_owner = "NOUMEN_Frech_Warren"  # Nom du propriétaire du projet (Ton nom)
dag_name = "Competition_dag"  # Nom du DAG (exemple: datalake_dag)
jar_path = "/home/ubuntu/workspace/scala/target/scala-2.12/main-scala-main_2.12-1.0.jar"  # Chemin du fichier java généré avec sbt clean package

ingestion_task_name = "match_task"  # Nom de la tache d'ingestion (expemple: ingestion_task)

formatted_task_name = "clean_task"  # Nom de la tache de formattage (expemple: formatted_task)
java_class_of_formatted_task = "FormattedData"   # Nom de la classe à exécuter pour le formattage (expemple: main.scala.mnm.MnMcount)

combine_task_name = "usageMatch_task"  # Nom de la tache de combinaison (expemple: combined_task)
java_class_of_combine_task = "CombinedData"  # Nom de la classe à exécuter pour la combinaison (expemple: main.scala.mnm.MnMcount)




default_args = {
    "owner": airflow_owner,
    "retries": 5,
    "retry_delay": timedelta(minutes=5)
}

with DAG(
        dag_id=dag_name,
        description="This is dag to manage the data lake",
        schedule_interval="@weekly",
        start_date=datetime(2024, 0o1, 0o1),
        default_args=default_args
) as dag:
    pass
    extraction_task = BashOperator(
        task_id='executer_mon_script',
        bash_command='/home/ubuntu/airflow/extraction.sh "{{ ds }}" '
    )


    formatted_task = SparkSubmitOperator(
        task_id=formatted_task_name,  # formatted
        conn_id='spark_default',
        application=jar_path,
        java_class=java_class_of_formatted_task,
        application_args=["hdfs://localhost:9000/user/ubuntu/off_raw/'{{ ds }}'.csv"],
    )

    combined_task = SparkSubmitOperator(
        task_id=combine_task_name,  # combined
        conn_id='spark_default',
        application=jar_path,
        java_class=java_class_of_combine_task,
        application_args=["hdfs://localhost:9000/user/ubuntu/off_formatted/'{{ ds }}'"],
        
    )

  

    extraction_task >> formatted_task >> combined_task