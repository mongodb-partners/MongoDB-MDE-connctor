# BigQuery to MongoDB Dataflow Template

The [BigQueryToMongoDB](../../src/main/java/com/google/cloud/teleport/v2/mongodb/templates/BigQueryToMongoDb.java) pipeline
The BigQuery to MongoDB template is a batch pipeline that reads rows from a BigQUery and writes them to MongoDB as documents. Currently the pipeline stores each rows as a document. For releases, we are planning to add user defined functions to modify the schema before writing the documents to MongoDB.

### Template parameters
**mongoDbUri** : MongoDB Connection URI. For example: _mongodb+srv://<username>:<password>@<server-connection-string>_.

**database** : Database in MongoDB to store the collection. For example: _my-db_.

**collection** : Name of the collection inside MongoDB database. For example: _my-collection_.

**inputTableSpec** : BigQuery input table spec. For example: _bigquery-project:dataset.output_table_.

## Getting Started

### Requirements
* Java 11
* Maven
* Bigquery dataset
* MongoDB host exists and is operational

### Building Template
This is a Flex Template meaning that the pipeline code will be containerized and the container will be used to launch the Dataflow pipeline.

#### Building Container Image
* Set environment variables.
  Set the pipeline vars
```sh
export PROJECT="gcp-pov"
export IMAGE_NAME="pubsub-to-mongodb"
export BUCKET_NAME=gs://vshanbh01
export TARGET_GCR_IMAGE=gcr.io/${PROJECT}/${IMAGE_NAME}
export BASE_CONTAINER_IMAGE=gcr.io/dataflow-templates-base/java11-template-launcher-base
export BASE_CONTAINER_IMAGE_VERSION=latest
export TEMPLATE_MODULE="googlecloud-to-mongodb"
export APP_ROOT=/template/${TEMPLATE_MODULE}
export COMMAND_SPEC=${APP_ROOT}/resources/pubsub-to-mongodb-command-spec.json
export TEMPLATE_IMAGE_SPEC=${BUCKET_NAME}/images/pubsub-to-mongodb-image-spec.json

export MONGODB_HOSTNAME="mongodb+srv://<username>:<password>@<server-connection-string>"
export MONGODB_DATABASE_NAME=<database name>
export MONGODB_COLLECTION_NAME=<Collection name>
export SUBSCRIPTION=<input table spec>
```

* Build and push image to Google Container Repository
```sh
mvn clean install -Dimage=${TARGET_GCR_IMAGE} \
                  -Dbase-container-image=${BASE_CONTAINER_IMAGE} \
                  -Dbase-container-image.version=${BASE_CONTAINER_IMAGE_VERSION} \
                  -Dapp-root=${APP_ROOT} \
                  -Dcommand-spec=${COMMAND_SPEC} \
                  -am -pl ${TEMPLATE_MODULE}
```

#### Creating Image Spec

* Create spec file in Cloud Storage under the path ${TEMPLATE_IMAGE_SPEC} describing container image location and metadata.
```json

```

### Testing Template

The template unit tests can be run using:
```sh
mvn test
```

### Executing Template

The template requires the following parameters:
* mongoDBUri: List of MongoDB node to connect to, ex: my-node1:port
* database: The database in mongoDB where the collection exists, ex: my-db
* collection: The collection in mongoDB database to put the documents to, ex: my-collection
* deadletterTable: Deadletter table for failed inserts in form: project-id:dataset.table


Template can be executed using the following gcloud command.
```sh
export JOB_NAME="${TEMPLATE_MODULE}-`date +%Y%m%d-%H%M%S-%N`"
gcloud beta dataflow flex-template run pubsub-to-mongodb \
        --project=${PROJECT} --region=us-east1 \
        --template-file-gcs-location=${TEMPLATE_IMAGE_SPEC} \
        --parameters mongoDbUri=${MONGODB_HOSTNAME},database=${MONGODB_DATABASE_NAME},collection=${MONGODB_COLLECTION_NAME},subscription=${SUBSCRIPTION}
```
