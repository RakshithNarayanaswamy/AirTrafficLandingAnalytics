# Air Traffic Landing Analytics

A Scala-based analytics system that analyzes San Francisco International Airport (SFO) landing data, forecasts future landing volumes, and detects anomalies.

## Project Overview

This project processes historical airline landing data to:
- Aggregate landing statistics by month and airline
- Calculate 6-month moving average baselines
- Forecast landing volumes for the next 6 months
- Detect anomalies when actual values deviate more than 30% from predictions

## Tech Stack

- **Scala 2.13**
- **Akka Typed** - Actor-based reactive architecture
- **TableParser (tableparser-spark 1.2.5)** - Type-safe CSV parsing
- **Apache Spark 4.0.1** - Dataset + aggregation example
- **JDK 17** - Required build/runtime
- **ScalaTest + Akka TestKit** - Testing framework

## Data Source

- **Source:** San Francisco International Airport (SFO)
- **File:** `Air_Traffic_Landings_Statistics_20251118.csv`
- **Records:** 44,502 landing records
- **Time Span:** 1999 - 2025
- **Airlines:** 175 airlines

## Project Structure

```
airtrafficlanding/
├── src/main/scala/air/
│   ├── actors/           # Akka Typed Actors
│   │   ├── Guardian.scala
│   │   ├── LandingFetcher.scala
│   │   ├── LandingProcessor.scala
│   │   ├── LandingRecommender.scala
│   │   ├── RecordKeeper.scala
│   │   └── CsvWriter.scala
│   ├── app/              # Application entry point
│   │   ├── Run.scala
│   │   └── TableParserSparkExample.scala
│   ├── formats/          # Data parsing utilities
│   │   └── Formats.scala
│   ├── messages/         # Actor protocols
│   ├── model/            # Data models
│   │   └── LandingRecord.scala
│   └── service/          # Business logic
│       ├── BaselineCalculator.scala
│       ├── ForecastService.scala
│       ├── LandingReader.scala
│       └── LandingTableParser.scala
├── src/test/scala/air/   # Test files
├── src/main/resources/   # Small sample CSV for TableParser+Spark demo
├── output/               # Generated CSV files
├── lib/                  # Optional unmanaged jars (not required for TableParser+Spark)
└── build.sbt
```

## How to Run

### Prerequisites

- **JDK 17** (required)
- sbt

1. Make sure you have SBT installed

2. Clone the repository
```bash
git clone [your-repo-url]
cd airtrafficlanding
```

3. Run the application
```bash
sbt run
```

4. Or specify a custom CSV path
```bash
sbt "run path/to/your/data.csv"
```

### TableParser + Spark example (for the course requirement)

This repo includes a minimal demo that:
1) parses a small CSV from `src/main/resources/air_traffic_sample.csv` using TableParser, and
2) converts it to a Spark `Dataset` and runs a simple aggregation.

Run:
```bash
sbt "runMain air.app.TableParserSparkExample"
```

## Output Files

The system generates two CSV files in the `output/` folder:

### forecasts.csv
Predicted landing volumes for the next 6 months per airline.
```
airline,next_period,predicted_landings,predicted_weight_lb
```

### anomalies.csv
Detected anomalies where actual values deviate more than 30% from predictions.
```
airline,period,actual,predicted,deviation_percent
```


## Testing

Run all tests:
```bash
sbt test
```

Test coverage includes:
- BaselineCalculatorSpec - Monthly aggregation, moving average
- LandingForecasterSpec - Forecasting algorithm
- LandingReaderSpec - CSV parsing
- GuardianSpec, LandingFetcherSpec, LandingProcessorSpec - Actor behavior
- RecordKeeperSpec - Anomaly storage
- CsvWriterSpec - File output

## Architecture

The system uses a dual-path architecture:

**Batch Path:**
LandingReader → BaselineCalculator → ForecastService

**Reactive Path (Akka Actors):**
Guardian → LandingFetcher → LandingProcessor → LandingRecommender → RecordKeeper → CSV Output

## Course

CSYE 7200 - Big Data System Engineering

Northeastern University