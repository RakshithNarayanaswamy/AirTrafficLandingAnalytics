package air.app

import air.service.LandingRow
import com.phasmidsoftware.tableparser.core.table.Table
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.io.Codec
import scala.util.{Failure, Success}

/**
 * Minimal, self-contained example showing **TableParser + Spark** working together.
 *
 * Run it with:
 *   sbt "runMain air.app.TableParserSparkExample"
 *
 * It parses a small CSV from src/main/resources using TableParser,
 * converts it to a Spark Dataset, then does a simple aggregation.
 */
object TableParserSparkExample {

  def main(args: Array[String]): Unit = {

    // Spark local session (works with sbt run)
    implicit val spark: SparkSession =
      SparkSession.builder()
        .appName("AirTrafficLanding-TableParser-Spark-Example")
        .master("local[*]")
        .getOrCreate()

    import spark.implicits._
    implicit val codec: Codec = Codec.UTF8

    // --- 1) Parse CSV from resources using TableParser ---
    // LandingRow provides the implicit TableParser[Table[LandingRow]] via TableParserHelper.
    val tableTry = com.phasmidsoftware.tableparser.core.table.Table
      .parseResource[Table[LandingRow]]("/air_traffic_sample.csv", getClass)

    tableTry match {
      case Success(table) =>
        val ds: Dataset[LandingRow] = spark.createDataset(table.toSeq)

        // --- 2) Spark computation (simple + obvious) ---
        // landing_count / total_landed_weight are strings in the raw CSV; convert them for aggregation.
        val cleaned = ds
          .withColumn("landing_count_int", org.apache.spark.sql.functions.regexp_replace($"landing_count", ",", "").cast("int"))
          .withColumn("total_weight_long", org.apache.spark.sql.functions.regexp_replace($"total_landed_weight", ",", "").cast("long"))

        val topAirlines = cleaned
          .groupBy($"operating_airline")
          .agg(
            org.apache.spark.sql.functions.sum($"landing_count_int").as("total_landings"),
            org.apache.spark.sql.functions.sum($"total_weight_long").as("total_weight")
          )
          .orderBy($"total_landings".desc)
          .limit(10)

        topAirlines.show(truncate = false)

      case Failure(e) =>
        throw new RuntimeException(s"Failed to parse /air_traffic_sample.csv: ${e.getMessage}", e)
    }

    spark.stop()
  }
}
