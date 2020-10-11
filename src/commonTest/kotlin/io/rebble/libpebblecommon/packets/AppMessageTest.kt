package io.rebble.libpebblecommon.packets

import assertUByteArrayEquals
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.soywiz.klock.DateTime.Companion.fromString
import io.rebble.libpebblecommon.util.DataBuffer
import kotlin.test.*

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
internal class AppMessageTest {
    @Test
    fun serializeDeserializePushMessage() {
        val testPushMessage = AppMessage.AppMessagePush(
            5u,
            uuidFrom("30880933-cead-49f6-ba94-3a6f8cd3218a"),
            listOf(
                AppMessageTuple.createUByteArray(77u, ubyteArrayOf(1u, 170u, 245u)),
                AppMessageTuple.createString(6710u, "Hello World"),
                AppMessageTuple.createString(7710u, "Emoji: \uD83D\uDC7D."),
                AppMessageTuple.createByte(38485u, -7),
                AppMessageTuple.createUByte(2130680u, 177u.toUByte()),
                AppMessageTuple.createShort(2845647u, -20),
                AppMessageTuple.createUShort(2845648u, 49885u.toUShort()),
                AppMessageTuple.createInt(2845649u, -707573),
                AppMessageTuple.createUInt(2845650u, 2448461u)
            )
        )

        val bytes = testPushMessage.m.toBytes()
        val newMessage = AppMessage.AppMessagePush().apply {
            m.fromBytes(DataBuffer(bytes))
        }

        val list = newMessage.dictionary.list

        assertEquals(testPushMessage.dictionary.list.size, list.size)

        assertUByteArrayEquals(ubyteArrayOf(1u, 170u, 245u), list[0].dataAsBytes)
        assertEquals("Hello World", list[1].dataAsString)
        assertEquals("Emoji: \uD83D\uDC7D.", list[2].dataAsString)
        assertEquals(-7, list[3].dataAsSignedNumber)
        assertEquals(177, list[4].dataAsUnsignedNumber)
        assertEquals(-20, list[5].dataAsSignedNumber)
        assertEquals(49885, list[6].dataAsUnsignedNumber)
        assertEquals(-707573, list[7].dataAsSignedNumber)
        assertEquals(2448461, list[8].dataAsUnsignedNumber)

        assertEquals(testPushMessage, newMessage)
    }

    @Test
    fun appMessageShortShouldBeLittleEndian() {
        val testPushMessage = AppMessage.AppMessagePush(
            0u,
            Uuid(0L, 0L),
            listOf(
                AppMessageTuple.createShort(0u, -50),
            )
        )

        val expectedMessage = ubyteArrayOf(
            1u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u,
            0u, 1u,
            0u, 0u, 0u, 0u,
            3u,
            2u, 0u,
            206u, 255u
        )

        assertUByteArrayEquals(
            expectedMessage,
            testPushMessage.m.toBytes()
        )
    }

    @Test
    fun appMessageUShortShouldBeLittleEndian() {
        val testPushMessage = AppMessage.AppMessagePush(
            0u,
            Uuid(0L, 0L),
            listOf(
                AppMessageTuple.createUShort(0u, 4876u),
            )
        )

        val expectedMessage = ubyteArrayOf(
            1u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u,
            0u, 1u,
            0u, 0u, 0u, 0u,
            2u,
            2u, 0u,
            12u, 19u
        )

        assertUByteArrayEquals(
            expectedMessage,
            testPushMessage.m.toBytes()
        )
    }

    @Test
    fun appMessageIntShouldBeLittleEndian() {
        val testPushMessage = AppMessage.AppMessagePush(
            0u,
            Uuid(0L, 0L),
            listOf(
                AppMessageTuple.createInt(0u, -90000),
            )
        )

        val expectedMessage = ubyteArrayOf(
            1u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u,
            0u, 1u,
            0u, 0u, 0u, 0u,
            3u,
            4u, 0u,
            112u, 160u, 254u, 255u
        )

        assertUByteArrayEquals(
            expectedMessage,
            testPushMessage.m.toBytes()
        )
    }

    @Test
    fun appMessageUIntShouldBeLittleEndian() {
        val testPushMessage = AppMessage.AppMessagePush(
            0u,
            Uuid(0L, 0L),
            listOf(
                AppMessageTuple.createUInt(0u, 900000u),
            )
        )

        val expectedMessage = ubyteArrayOf(
            1u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u,
            0u, 1u,
            0u, 0u, 0u, 0u,
            2u,
            4u, 0u,
            160u, 187u, 13u, 0u
        )

        assertUByteArrayEquals(
            expectedMessage,
            testPushMessage.m.toBytes()
        )
    }

    @Test
    fun appMessageStringShouldBeBigEndianAndTerminatedWithZero() {
        val testPushMessage = AppMessage.AppMessagePush(
            0u,
            Uuid(0L, 0L),
            listOf(
                AppMessageTuple.createString(0u, "Hello"),
            )
        )

        val expectedMessage = ubyteArrayOf(
            1u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u,
            0u, 1u,
            0u, 0u, 0u, 0u,
            1u,
            6u, 0u,
            'H'.toInt().toUByte(),
            'e'.toInt().toUByte(),
            'l'.toInt().toUByte(),
            'l'.toInt().toUByte(),
            'o'.toInt().toUByte(),
            0u
        )

        assertUByteArrayEquals(
            expectedMessage,
            testPushMessage.m.toBytes()
        )
    }

    @Test
    fun appMessageBytesShouldBeBigEndian() {
        val testPushMessage = AppMessage.AppMessagePush(
            0u,
            Uuid(0L, 0L),
            listOf(
                AppMessageTuple.createUByteArray(0u, ubyteArrayOf(1u, 2u, 3u)),
            )
        )

        val expectedMessage = ubyteArrayOf(
            1u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u,
            0u, 0u, 0u, 0u,
            0u, 1u,
            0u, 0u, 0u, 0u,
            0u,
            3u, 0u,
            1u, 2u, 3u
        )

        assertUByteArrayEquals(
            expectedMessage,
            testPushMessage.m.toBytes()
        )
    }
}