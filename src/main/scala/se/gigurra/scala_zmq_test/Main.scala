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

    val port = 12345
    val addr = s"tcp://127.0.0.1:$port"

    val server = Future {

      val ctx = new ZContext
      val socket = ctx.createSocket(ZMQ.ZMQ_REQ)
      socket.connect(addr)
      socket.send("Hello")

      val reply = socket.recvStr()
      println(s"Client received: $reply")

      ctx.close()
    }

    val client = Future {

      val ctx = new ZContext
      val socket = ctx.createSocket(ZMQ.ZMQ_REP)
      socket.bind(addr)

      val msg = socket.recvStr()
      println(s"Server received: $msg")

      socket.send("GoodBye")

      ctx.close()
    }

    Await.result(server, 10 seconds)
    Await.result(client, 10 seconds)
  }
}