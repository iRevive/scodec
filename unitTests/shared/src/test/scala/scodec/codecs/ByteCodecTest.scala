package scodec
package codecs

import org.scalacheck.Gen
import scodec.bits.BitVector

class ByteCodecTest extends CodecSuite {
  def check(low: Byte, high: Byte)(f: (Byte) => Unit): Unit =
    forAll(Gen.choose(low, high)) { n =>
      f(n)
    }

  "the byte codec" should {
    "roundtrip" in {
      forAll { (n: Byte) =>
        roundtrip(byte, n)
      }
    }
  }
  "the ubyte(n) codec" should {
    "roundtrip" in {
      forAll(Gen.choose(0, 127)) { n =>
        roundtrip(ubyte(7), n.toByte)
      }
    }
  }

  "the byte codecs" should {
    "return an error when value to encode is out of legal range" in {
      byte(7).encode(Byte.MaxValue) shouldBe Attempt.failure(
        Err("127 is greater than maximum value 63 for 7-bit signed byte")
      )
      byte(7).encode(Byte.MinValue) shouldBe Attempt.failure(
        Err("-128 is less than minimum value -64 for 7-bit signed byte")
      )
      ubyte(7).encode(-1) shouldBe Attempt.failure(
        Err("-1 is less than minimum value 0 for 7-bit unsigned byte")
      )
    }

    "return an error when decoding with too few bits" in {
      byte.decode(BitVector.low(4)) shouldBe Attempt.failure(Err.insufficientBits(8, 4))
    }
  }
}
