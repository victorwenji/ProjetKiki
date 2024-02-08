// scalastyle:off println

package main.scala.mnmc

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.{unix_timestamp,from_unixtime}
import org.apache.spark.sql.functions._
import java.time._

/**
  * Usage: MnMcount <mnm_file_dataset>
  */
object Formated {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("Formated")
      .getOrCreate()

    
    val pathfood = "hdfs://localhost:9000/user/ubuntu/off_raw/en.openfoodfacts.org.products.csv"

    val food1 = spark.read.option("header","true").option("delimiter","\\t").csv(pathfood)

    //food1.repartition(5)

    //custom schema

    val customSchemaDDL = """
        `code` STRING,
        `url` STRING,
        `product_name` STRING,
        `brands` STRING,
        `quantity` STRING,
        `categories` STRING,
        `categories_tags` ARRAY<STRING>,
        `labels` STRING,
        `labels_tags` ARRAY<STRING>,
        `countries` STRING,
        `countries_tags` ARRAY<STRING>,
        `ingredients_text` STRING,
        `traces` STRING,
        `traces_tags` ARRAY<STRING>,
        `serving_size` STRING,
        `energy_100g` DOUBLE,
        `proteins_100g` DOUBLE,
        `fat_100g` DOUBLE,
        `carbohydrates_100g` DOUBLE,
        `sugars_100g` DOUBLE,
        `fiber_100g` DOUBLE,
        `sodium_100g` DOUBLE,
        `additives_n` LONG,
        `additives_tags` ARRAY<STRING>,
        `nutrition_grade_fr` STRING,
        `main_category` STRING,
        `image_url` STRING
    """

    val selectfood1 = {food1.select(
        "code",
        "url",
        "created_t",
        "last_modified_t",
        "product_name",
        "brands",
        "quantity",
        "categories",
        "categories_tags",
        "labels",
        "labels_tags",
        "countries",
        "countries_tags",
        "ingredients_text",
        "allergens",
        "product_quantity",
        "traces",
        "traces_tags",
        "serving_size",
        "energy_100g",
        "proteins_100g",
        "fat_100g",
        "carbohydrates_100g",
        "sugars_100g",
        "fiber_100g",
        "sodium_100g",
        "additives_n",
        "additives_tags",
        "nutrition-score-fr_100g", 
        "main_category",
        "image_url",
        "nutriscore_score",      
        "nutriscore_grade",
        "ecoscore_score",
        "ecoscore_grade"
    )}
    
    /*Affichage des résultats
   selectfood1.show()

    val sqlframe=selectfood1.createOrReplaceTempView("produit")


    spark.sql("select product_name,brands,quantity,energy_100g,'nutrition-score-fr_100g' from produit where quantity is not null limit 2").show(false)


    spark.sql("select * from produit where quantity is not null limit 10").show*/



    //food1.cache()

    ///////début de traitement spark


    //food1.cache()

    //3.supprimsion des doublons en se basant sur le code du produit
    val duplicateData=selectfood1.dropDuplicates("code")

    val duplicateData1=selectfood1.dropDuplicates("product_name")

    //1.supprimer les produits dont la quantité est null, le nom est null, le code
    val product_namenull=duplicateData1.na.drop(Seq("quantity","product_name","code"))


    //4.convertir les dates à un format unique

    import org.apache.spark.sql.functions.{unix_timestamp, from_unixtime}

    val dateFormat = "yyyy-MM-dd HH:mm:ss"

    val dataWithCustomTimestamps = {product_namenull
    .withColumn("created_t", from_unixtime(col("created_t"), dateFormat).cast("timestamp"))
    .withColumn("last_modified_t", from_unixtime(col("last_modified_t"), dateFormat).cast("timestamp"))}

    dataWithCustomTimestamps.select("code","product_name","created_t","last_modified_t")

    //write du dataframe final sur hadoop

    val currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    dataWithCustomTimestamps.write.mode("overwrite").parquet(s"hdfs://localhost:9000/user/ubuntu/off_formatted/$currentDate")
  }
}
// scalastyle:on println
