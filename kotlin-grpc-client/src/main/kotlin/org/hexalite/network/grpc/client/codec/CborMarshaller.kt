package org.hexalite.network.grpc.client.codec

import io.grpc.MethodDescriptor.Marshaller
import java.io.InputStream

class CborMarshaller<T>: Marshaller<T> {
    override fun parse(stream: InputStream?): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun stream(value: T): InputStream {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}