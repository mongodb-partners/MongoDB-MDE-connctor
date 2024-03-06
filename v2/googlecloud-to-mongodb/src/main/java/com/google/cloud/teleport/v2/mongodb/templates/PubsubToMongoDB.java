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
package com.google.cloud.teleport.v2.mongodb.templates;

import com.google.cloud.teleport.metadata.Template;
import com.google.cloud.teleport.metadata.TemplateCategory;
import com.google.cloud.teleport.v2.mongodb.options.PubsubToMongoDBOptions.MongoDbOptions;
import com.google.cloud.teleport.v2.mongodb.options.PubsubToMongoDBOptions.PubsubReadOptions;
import com.google.cloud.teleport.v2.mongodb.templates.PubsubToMongoDB.Options;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.mongodb.MongoDbIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.bson.Document;

/**
 * The {@link PubsubToMongoDB} pipeline is a batch pipeline which ingests data from MongoDB and
 *
 * <p>Check out <a
 * href="https://github.com/GoogleCloudPlatform/DataflowTemplates/blob/main/v2/mongodb-to-googlecloud/PubsubToMongoDB.md">README</a>
 * for instructions on how to use or modify this template.
 */
@Template(
    name = "Pubsub_to_MongoDB",
    category = TemplateCategory.BATCH,
    displayName = "PubSub to MongoDB",
    description = "",
    optionsClass = Options.class,
    flexContainerName = "pubsub-to-mongodb",
    documentation =
        "https://cloud.google.com/dataflow/docs/guides/templates/provided/mongodb-to-bigquery",
    contactInformation = "https://cloud.google.com/support",
    preview = true,
    requirements = {
      "The source Pubsub subscription must exist.",
      "The targe MongoDB instance must be accessible from the Dataflow worker machines."
    })
public class PubsubToMongoDB {
  /**
   * Options supported by {@link PubsubToMongoDB}
   *
   * <p>Inherits standard configuration options.
   */
  public interface Options extends PipelineOptions, MongoDbOptions, PubsubReadOptions {}

  private static class ParseAsDocumentsFn extends DoFn<String, Document> {
    @ProcessElement
    public void processElement(ProcessContext context) {
      context.output(Document.parse(context.element()));
    }
  }

  public static void main(String[] args) {
    Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);

    run(options);
  }

  public static boolean run(Options options) {
    Pipeline pipeline = Pipeline.create(options);

    PCollection<PubsubMessage> messages = null;
    messages =
        pipeline.apply(
            "Read PubSub Events",
            PubsubIO.readMessagesWithAttributes().fromSubscription(options.getInputSubscription()));

    messages
        .apply(
            "PubSub Message",
            ParDo.of(
                new DoFn<PubsubMessage, Document>() {
                  @ProcessElement
                  public void process(ProcessContext c) {

                    // String payloadString = new String(pubSubMessage.getPayload(),
                    // StandardCharsets.UTF_8);
                    String payloadString =
                        new String(c.element().getPayload(), StandardCharsets.UTF_8);
                    Document doc = new Document();
                    Document parsedDoc = doc.parse(payloadString);
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(c.element().getAttributeMap());
                    jsonString = jsonString.replace("mde.", "mde_");
                    Document parsedAttributes = doc.parse(jsonString);

                    parsedDoc.append("attributes", parsedAttributes);

                    c.output(parsedDoc);
                  }
                }))
        .apply(
            "Put to MongoDB",
            MongoDbIO.write()
                .withUri(options.getMongoDBUri())
                .withDatabase(options.getDatabase())
                .withCollection(options.getCollection())
                .withIgnoreSSLCertificate(true));

    pipeline.run();
    return true;
  }
}
