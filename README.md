
MongoDB MDE connector using dataflow Pub/Sub to MongoDB template.
---
The manufacturing industry is undergoing a transformative shift, driven by data-driven insights and intelligent automation. Traditional methods of data collection and analysis are no longer sufficient to keep pace with the demands of today's competitive landscape. This is where Google Manufacturing Data Engine (MDE) and MongoDB Atlas come into play, offering a powerful duo for optimizing your factory floor.

MDE acts as the foundational cloud solution for your data journey. It simplifies the process of collecting data from various industrial assets and systems, including sensors, machines, and control systems.

MongoDB Atlas, a developer data platform, complements MDE by offering Scalability and Flexibility to automatically scale up and down to accommodate your evolving data needs, making it ideal for dynamic manufacturing environments.

With MongoDB MDE connector we don't need the effort to rewrite the application integration. The processed data written to MongoDB can be queried into the application directly.


### How to setup?

As a pre-requisite to deploy the end-to-end setup one needs to have MDE subscription access and MongoDB Atlas cluster. [Get started](https://www.mongodb.com/docs/atlas/getting-started/) with MongoDB Atlas setup if you are new to Atlas.

The dataflow jobs are available as both [classic](./v1/README_PubSub_to_BigQuery.md) and [flex templates](v2/googlecloud-to-mongodb/docs/PubSubToMongoDB/PubsubToMongoDB.md). Both the templates require users to pass MongoDB URI, database name and collection name.

#### 1. Get MongoDB Atlas Connection URI, database name and collection name
- In the Atlas UI, select your Database Deployment and Click **Database** in the top-left corner of Atlas.
- In the **Database Deployments** view, click **Connect** for the database deployment to which you want to connect.
- Replace <password> with the **password** specified when you created your database user.
- You can name your database and collection based on your preference.


#### 2. Google cloud setup:
- You need access to Google cloud PubSub subscription exposed by MDE to read the data being published.
- Refer [Dataflow access control](https://cloud.google.com/dataflow/docs/concepts/access-control) to provide with the minimal permission required to run the Dataflow jobs. You will required Dataflow Admin or Dataflow Developer to build and run the Dataflow job.
- Refer v2 [readme](./v2/googlecloud-to-mongodb/docs/PubSubToMongoDB/PubsubToMongoDB.md) file to setup environment variables to stage and run the dataflow flex template.


#### Query MongoDB data:
- To query documents from MongoDB Atlas use the [MQL](https://www.mongodb.com/docs/manual/tutorial/query-documents/)(MongoDB Query language).
