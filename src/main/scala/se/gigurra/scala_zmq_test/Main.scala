package se.gigurra.scala_zmq_test

import java.nio.charset.Charset

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.zeromq.ZContext
import org.zeromq.ZMQ.Socket

import zmq.ZMQ

object Main {

  def main(args: Array[String]) {

    val port = 12345
    val addr = s"tcp://127.0.0.1:$port"

    val client = Future {

      val ctx = new ZContext
      val socket = ctx.createSocket(ZMQ.ZMQ_REQ)
      socket.connect(addr)
      socket.send("Hello")

      val reply = socket.recvStr()
      println(s"Client received: $reply")

      ctx.close()
    }

    val server = Future {

      val ctx = new ZContext
      val socket = ctx.createSocket(ZMQ.ZMQ_ROUTER)
      socket.bind(addr)

      val pieces = recvAll(socket)

      for (piece <- pieces) {
        println(s"Server received: ${new String(piece, Charset.defaultCharset)}")
      }

      sendAll(socket, pieces)

      ctx.close()
    }

    Await.result(server, 10 seconds)
    Await.result(client, 10 seconds)
  }

  def recvAll(socket: Socket): Seq[Array[Byte]] = {
    val pieces = new ArrayBuffer[Array[Byte]]
    pieces += socket.recv()
    while (socket.hasReceiveMore())
      pieces += socket.recv()
    pieces
  }

  def sendAll(socket: Socket, pieces: Seq[Array[Byte]]) {
    for (piece <- pieces) {
      if (piece != pieces.last)
        socket.sendMore(piece)
      else
        socket.send(piece)
    }
  }

}