package se.gigurra.scala_zmq_test

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.zeromq.ZContext

import zmq.ZMQ

object Main {

  def main(args: Array[String]) {

    val port = 8080
    val ip = "192.168.1.211"

    val client = Future {

      val ctx = new ZContext
      val socket = ctx.createSocket(ZMQ.ZMQ_REQ)
      socket.connect(s"tcp://$ip:$port")
      socket.send("Hello")

      println(s"Client received: ${socket.recvStr()}")

      ctx.close()
    }

    /*
    val server = Future {

      val ctx = new ZContext
      val socket = ctx.createSocket(ZMQ.ZMQ_ROUTER)
      socket.bind(s"tcp://0.0.0.0:$port")

      val pieces = recvAll(socket)

      for (piece <- pieces)
        println(s"Server received: ${new String(piece, Charset.defaultCharset)}")

      Util.sendAll(socket, pieces)

      ctx.close()
    }

    Await.result(server, 2 seconds)
    */

    Await.result(client, 2 seconds)
  }

}