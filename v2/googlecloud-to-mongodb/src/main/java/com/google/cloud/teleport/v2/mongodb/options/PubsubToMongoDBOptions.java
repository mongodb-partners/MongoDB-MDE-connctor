/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.teleport.v2.mongodb.options;

import com.google.cloud.teleport.metadata.TemplateParameter;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.PipelineOptions;

/**
 * The {@link PubsubToMongoDBOptions} class provides the custom execution options passed by the
 * executor at the command-line.
 */
public class PubsubToMongoDBOptions {
  /** MongoDB write Options initialization. */
  public interface MongoDbOptions extends PipelineOptions, DataflowPipelineOptions {

    @TemplateParameter.Text(
        order = 1,
        description = "MongoDB Connection URI",
        helpText = "URI to connect to MongoDB Atlas.")
    String getMongoDBUri();

    void setMongoDBUri(String getMongoDBUri);

    @TemplateParameter.Text(
        order = 2,
        description = "MongoDB Database",
        helpText = "Database in MongoDB to store the collection.",
        example = "my-db")
    String getDatabase();

    void setDatabase(String database);

    @TemplateParameter.Text(
        order = 3,
        description = "MongoDB collection",
        helpText = "Name of the collection inside MongoDB database.",
        example = "my-collection")
    String getCollection();

    void setCollection(String collection);
  }

  /** BigQuery read Options initialization. */
  public interface PubsubReadOptions extends PipelineOptions, DataflowPipelineOptions {

    @TemplateParameter.PubsubSubscription(
        order = 1,
        description = "Pubsub subscription",
        helpText = "pubsub subscription",
        example = "")
    String getInputSubscription();

    void setInputSubscription(String inputSubscription);
  }
}
