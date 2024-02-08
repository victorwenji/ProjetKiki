// scalastyle:off println

package main.scala.main

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import java.time._
import org.apache.spark.sql.functions.{unix_timestamp, from_unixtime}


import org.apache.spark.sql.functions.{col, explode}

import java.time.LocalDate

object FormattedData {

  def main(args:Array[String]): Unit = {
    
    val spark = SparkSession
      .builder
      .appName("jobformated")
      .getOrCreate()

    if (args.length < 1) {
      print("Usage: MnMcount <mnm_file_dataset>")
      sys.exit(1)
    }
    // get the M&M data set file name
    var csvfile = args(0)
    
      //lecture du fichier
    var df = spark.read.option("header","true").option("delimiter","\\t").csv(csvfile)

    //repartition du dataframe 
    df = df.repartition(6)

    df.cache()
    
    //afficher le schema
    df.printSchema

    //création d'une vue temporelle 
    df.createOrReplaceTempview("ma_table")

    //selection des colonnes utiles
    val selectedData = spark.sql("""
      SELECT
        code,
        url,
        created_t,
        last_modified_t,
        product_name,
        brands,
        quantity,
        categories,
        categories_tags,
        labels,
        labels_tags,
        countries,
        countries_tags,
        ingredients_text,
        allergens,
        product_quantity,
        traces,
        traces_tags,
        serving_size,
        energy_100g,
        proteins_100g,
        fat_100g,
        carbohydrates_100g,
        sugars_100g,
        fiber_100g,
        sodium_100g,
        additives_n,
        additives_tags,
        nutrition_score_fr_100g,
        main_category,
        image_url,
        nutriscore_score,
        nutriscore_grade,
        ecoscore_score,
        ecoscore_grade
      FROM
        ma_table
      """)


//3.supprimsion des doublons en se basant sur le code du produit
var duplicateData=selectedData.dropDuplicates("code")

duplicateData=selectedData.dropDuplicates("product_name")

//1.supprimer les produits dont la quantité est null, le nom est null, le code
val product_namenull=duplicateData.na.drop(Seq("quantity","product_name","code"))


//4.convertir les dates à un format unique


val dateFormat = "yyyy-MM-dd HH:mm:ss"
val dataWithCustomTimestamps = {product_namenull
  .withColumn("created_t", from_unixtime(col("created_t"), dateFormat).cast("timestamp"))
  .withColumn("last_modified_t", from_unixtime(col("last_modified_t"), dateFormat).cast("timestamp"))}

  dataWithCustomTimestamps.select("code","product_name","created_t","last_modified_t").show(5)


  //write du dataframe final sur hadoop

   val currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  dataWithCustomTimestamps.write.mode("overwrite").parquet(s"hdfs://localhost:9000/user/ubuntu/off_formatted/$currentDate")


  }

    /*
    * Write your code here
    * Just use the currentDate*/
}