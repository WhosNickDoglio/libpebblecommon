package io.rebble.libpebblecommon.packets

import io.rebble.libpebblecommon.exceptions.PacketDecodeException
import io.rebble.libpebblecommon.protocolhelpers.PebblePacket
import io.rebble.libpebblecommon.structmapper.SBytes
import io.rebble.libpebblecommon.structmapper.SUShort
import io.rebble.libpebblecommon.structmapper.StructMapper
import io.rebble.libpebblecommon.util.DataBuffer

const val HEADER_SIGNATURE = 0xFEEDU
const val FOOTER_SIGNATURE = 0xBEEFU

open class QemuInboundPacket {
    val m = StructMapper()
    val signature = SUShort(m, HEADER_SIGNATURE.toUShort())
    val protocol = SUShort(m)
    val length = SUShort(m)

    class QemuSPP: QemuInboundPacket() {
        val payload = SBytes(m)
        val footer = SUShort(m, FOOTER_SIGNATURE.toUShort())

        init {
            payload.linkWithSize(length)
        }
    }

    companion object {
        fun deserialize(packet: UByteArray): QemuInboundPacket {
            val buf = DataBuffer(packet)
            val meta = StructMapper()
            val header = SUShort(meta)
            val protocol = SUShort(meta)
            meta.fromBytes(buf)
            return when (protocol.get()) {
                1u.toUShort() -> QemuSPP().also { it.m.fromBytes(buf) }
                else -> {
                    println("Warning: QEMU packet left generic")
                    QemuInboundPacket().also { it.m.fromBytes(buf) }
                }
            }
        }
    }
}