import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Prudhvi Raj on 3/2/2016.
  */
object TwitterStreaming {
  def main(args: Array[String]) {
    val filters = args

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    System.setProperty("twitter4j.oauth.consumerKey", "iy7wgK1MTpgAEjFBaCugJ0vPa")
    System.setProperty("twitter4j.oauth.consumerSecret", "SsjW4hYkGxTqriHRnvSmjHNGbFxns4Uu6V76RCyGEZ3pkZM6UU")
    System.setProperty("twitter4j.oauth.accessToken", "2517903619-PTMsCBpOOYmdFdY8TWuWBD10Ua2aqYF2XnlKrpn")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "C3A9p3duBttHadP5zgVIpDK2uRBRE4Ak0QPxf5aShT55J")

    //Create a spark configuration with a custom name and master
    // For more master configuration see  https://spark.apache.org/docs/1.2.0/submitting-applications.html#master-urls
    val sparkConf = new SparkConf().setAppName("STweetsApp").setMaster("local[*]")
    //Create a Streaming COntext with 2 second window
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets
    val stream = TwitterUtils.createStream(ssc, None, filters)
    stream.print()
    val text = stream.flatMap(status => status.getText.split(" "))

    //Finding the top hash Tags on 30 second window
//    val topCounts30 = text.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(30))
//      .map{case (topic, count) => (count, topic)}
//      .transform(_.sortByKey(false))
    //Finding the top hash Tgas on 10 second window
    val topCounts10 = text.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(10))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))

    // Print popular hashtags
//    topCounts30.foreachRDD(rdd => {
//      val topList = rdd.take(10)
//      println("\nPopular topics in last 30 seconds (%s total):".format(rdd.count()))
//      topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
//    })

    topCounts10.foreachRDD(rdd => {

      val topList = rdd.take(10)
      var s:String="Tweets:\n"
      println("\nPopular topics in last 10 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, tag) => println("%s (%s count)".format(tag, count)); s+=tag+" : "+count+ "(count)\n"}
      SocketClient.sendCommandToRobot(s);
    })

    ssc.start()


    ssc.awaitTermination()
  }
}
